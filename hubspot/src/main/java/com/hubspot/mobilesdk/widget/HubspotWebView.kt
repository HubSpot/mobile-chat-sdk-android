/*************************************************
 * HubspotWebView.kt
 * Hubspot Mobile SDK
 *
 * Copyright (c) 2024 Hubspot, Inc.
 ************************************************/
package com.hubspot.mobilesdk.widget

import android.content.Context
import android.util.AttributeSet
import android.webkit.WebView
import androidx.lifecycle.coroutineScope
import androidx.lifecycle.findViewTreeLifecycleOwner
import com.hubspot.mobilesdk.HubspotManager
import com.hubspot.mobilesdk.config.HubspotConfigError
import com.hubspot.mobilesdk.errorhandling.NetworkError
import com.hubspot.mobilesdk.firebase.PushNotificationChatData
import com.hubspot.mobilesdk.usecases.CompileAndUploadMetaDataUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import timber.log.Timber
import java.util.Locale

/**
 * HubspotWebView class is used for setting a custom headers, setting a user agent string
 * It also loads the chat url including email and token
 */

class HubspotWebView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
) : WebView(context, attrs, defStyleAttr) {
    private var manager: HubspotManager = HubspotManager.getInstance(context)
    private var hsThreadId: String? = null
    private val onThreadIdFetched: () -> Unit = {
        findViewTreeLifecycleOwner()?.lifecycle?.coroutineScope?.launch(Dispatchers.IO) {
            when {
                !hsThreadId.isNullOrBlank() -> {
                    try {
                        CompileAndUploadMetaDataUseCase(manager, context)
                            .setParameters(hsThreadId.toString())
                            .execute()
                    } catch (error: NetworkError) {
                        Timber.e(error.errorMessage ?: HubspotConfigError.MetaDataAPIFailure.message)
                    }
                }

                else -> {
                    try {
                        if (HubspotWebViewClient.JSBridge.isConversationIdAvailable()) {
                            CompileAndUploadMetaDataUseCase(manager, context)
                                .setParameters(HubspotWebViewClient.JSBridge.retrieveConversationId())
                                .execute()
                        }
                    } catch (error: NetworkError) {
                        Timber.e(error.errorMessage ?: HubspotConfigError.MetaDataAPIFailure.message)
                    }
                }
            }
        }
    }

    /**
     * This show method shows the ChatURL which updates the chatflow if it passes as an arguments
     * It also manages the UserAgent for Mobile and integrated javascript for load the widget
     * It embedded extra header for the language changes
     * Finally, it load the URL including all the changes
     */
    fun show(chatFlow: String? = null, pushData: PushNotificationChatData? = null) {
        this.hsThreadId = pushData?.threadId
        manager.configure()
        val chatURL = manager.chatURL(chatFlow, pushData)
        isFocusableInTouchMode = true
        val userAgent = "${settings.userAgentString}$HUBSPOT_MOBILE_CONFIG"
        settings.userAgentString = userAgent
        settings.javaScriptEnabled = true
        webViewClient = HubspotWebViewClient()
        (webViewClient as HubspotWebViewClient).setActionAfterJsEvaluation(onThreadIdFetched)
        addJavascriptInterface(HubspotWebViewClient.JSBridge, JAVASCRIPT_INTERFACE_NAME)
        val headers = HashMap<String, String>()
        headers["Accept-Language"] = Locale.getDefault().toString()
        loadUrl(chatURL, headers)
    }

    /**
     * @suppress("NOT_DOCUMENTED")
     */
    companion object {
        const val HUBSPOT_MOBILE_CONFIG = " HubspotMobileSDK"
        const val JAVASCRIPT_INTERFACE_NAME = "nativeApp"
    }
}