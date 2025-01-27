/*************************************************
 * HubspotWebViewClient.kt
 * Hubspot Mobile SDK
 *
 * Copyright (c) 2024 Hubspot, Inc.
 ************************************************/
package com.hubspot.mobilesdk.widget

import android.content.Intent
import android.net.Uri
import android.webkit.JavascriptInterface
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.core.content.ContextCompat
import timber.log.Timber

/**
 * HubspotWebViewClient class uses custom javascript and render the messages in the logs for the script
 */
internal class HubspotWebViewClient : WebViewClient() {
    override fun onPageFinished(view: WebView?, url: String?) {
        super.onPageFinished(view, url)
        injectJavaScript(view)
    }

    // This method handles the navigation flow for links which opens default phone browser.
    override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?): Boolean {
        val i = Intent(Intent.ACTION_VIEW, Uri.parse(request?.url.toString()))
        view?.context?.let { ContextCompat.startActivity(it, i, null) }
        return true
    }

    /**
     * Internal function to pass lambda to HubspotWebViewClient. This lambda will be invoked when JS evaluation on webview is complete
     */
    internal fun setActionAfterJsEvaluation(actionOnThreadIdFetched: (JsEvents) -> Unit) {
        JSBridge.assignCallback(actionOnThreadIdFetched)
    }

    private fun injectJavaScript(webView: WebView?) {
        val script = """
            function configureHubspotConversations() {
                if (window.HubSpotConversations) {
                    nativeApp.postMessage("info", "Setting up handlers");
                    window.HubSpotConversations.on('conversationStarted', payload => {
                        nativeApp.postMessage("info", JSON.stringify(payload));
                    });
                    window.HubSpotConversations.on('widgetLoaded', payload => {
                        nativeApp.postMessage("info", JSON.stringify(payload));
                    });
                    window.HubSpotConversations.on('userInteractedWithWidget', payload => {
                        nativeApp.postMessage("info", JSON.stringify(payload));
                    });
                    window.HubSpotConversations.on('userSelectedThread', payload => {
                        nativeApp.postMessage("info", JSON.stringify(payload));
                        const conversationId = JSON.stringify(payload.conversation.conversationId);
                        nativeApp.postMessage("conversation_id", conversationId);
                        nativeApp.postConversationId(conversationId);
                    });
                    window.HubSpotConversations.on('sdkCloseButtonClick', payload => {
                        nativeApp.postMessage("info", JSON.stringify(payload));
                        nativeApp.closeWebViewHost()
                    });
                    nativeApp.postMessage("info", "Finished setting up handlers");
                } else {
                    nativeApp.postMessage("info","No object to set handlers on still");
                }
            }
            nativeApp.postMessage("info","Starting main load script");
            if (window.HubSpotConversations) {
                configureHubspotConversations();
            }
            else if (Array.isArray(window.hsConversationsOnReady)) {
                window.hsConversationsOnReady.push(configureHubspotConversations);
            } else {
                window.hsConversationsOnReady = [configureHubspotConversations];
            }
            nativeApp.postMessage("info","Finished main load script");
        """
        webView!!.evaluateJavascript(script) { }
    }

    /**
     * @suppress("NOT_DOCUMENTED")
     */
    object JSBridge {

        private var conversationId: String? = null
        private lateinit var callback: (JsEvents) -> Unit

        /**
         * postMessage is used to show the logs with info and message when interacting with javascript
         */
        @JavascriptInterface
        fun postMessage(info: String, message: String) {
            Timber.e("$info:$message")
        }

        /**
         * Sets the conversation_id/thread_id in JSbridge from injected JS code
         */
        @JavascriptInterface
        fun postConversationId(conversationId: String) {
            this.conversationId = conversationId
            callback.invoke(JsEvents.PostConversationIdEvent(conversationId))
        }

        /**
         * Invokes a callback when the close button in the chat screen is clicked
         */
        @JavascriptInterface
        fun closeWebViewHost() {
            callback.invoke(JsEvents.WebViewHostCloseEvent)
        }

        /**
         * Internal function to check if conversation_id/thread_id is available
         */
        internal fun isConversationIdAvailable(): Boolean {
            return conversationId != null
        }

        /**
         * Internal function to get the conversation_id/thread_id
         */
        internal fun retrieveConversationId(): String {
            return conversationId ?: ""
        }

        /**
         * Internal function to assign callback to the JSBridge object, responsible for interfacing with kotlin code and injected JS code.
         * @param callback lambda that will be invoked after manual JS evaluation  on current webpage
         */
        internal fun assignCallback(callback: (JsEvents) -> Unit) {
            this.callback = callback
        }
    }

    sealed class JsEvents {
        data class PostConversationIdEvent(val conversationId: String): JsEvents()
        data object WebViewHostCloseEvent: JsEvents()
    }

}