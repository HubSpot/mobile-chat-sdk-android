/*************************************************
 * HubspotWebActivity.kt
 * Hubspot Mobile SDK
 *
 * Copyright (c) 2024 Hubspot, Inc.
 ************************************************/
package com.hubspot.mobilesdk

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.View
import android.webkit.ValueCallback
import android.webkit.WebChromeClient
import android.webkit.WebView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationManagerCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import com.hubspot.mobilesdk.databinding.ActivityWebviewBinding
import com.hubspot.mobilesdk.firebase.HubspotFirebaseMessagingService
import com.hubspot.mobilesdk.firebase.HubspotFirebaseMessagingService.Companion.HS_PUSH_DATA
import com.hubspot.mobilesdk.firebase.PushNotificationChatData
import com.hubspot.mobilesdk.metadata.ChatPropertyKey

/**
 * HubspotWebActivity class manages the File Uploading (including image, videos, any type of file etc.)
 */
class HubspotWebActivity : AppCompatActivity() {
    private lateinit var binding: ActivityWebviewBinding

    private var fileUploadCallback: ValueCallback<Array<Uri>>? = null
    private val manager = HubspotManager.getInstance(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWebviewBinding.inflate(layoutInflater)
        setContentView(binding.root)
        applyEdgeToEdgeInsets()
        val chatFlowId = intent.getStringExtra(CHAT_FLOW_KEY)
        val pushData = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.extras?.getSerializable(HS_PUSH_DATA, PushNotificationChatData::class.java)
        } else {
            intent.getSerializableExtra(HS_PUSH_DATA) as PushNotificationChatData?
        }
        val notificationPermission = NotificationManagerCompat.from(this).areNotificationsEnabled()
        pushNotificationPermissionGrant(notificationPermission)
        with(binding.hubspotwebview) {
            settings.javaScriptEnabled = true
            show(chatFlowId, pushData)
            addChatScreenCloseListener { finish() }
            webChromeClient = object : WebChromeClient() {
                override fun onShowFileChooser(webView: WebView?, filePathCallback: ValueCallback<Array<Uri>>?, fileChooserParams: FileChooserParams?): Boolean {
                    // When we press back button without selecting any component(image/video/audio etc.),
                    // this should open the window to choose the file again from the phone.
                    // So that's why we have used onReceive(null) because user didn't select anything and press back
                    // More Info: https://developer.android.com/reference/android/webkit/WebChromeClient.html?authuser=1#onShowFileChooser(android.webkit.WebView,%20android.webkit.ValueCallback%3Candroid.net.Uri[]%3E,%20android.webkit.WebChromeClient.FileChooserParams)
                    fileUploadCallback?.onReceiveValue(null)
                    fileUploadCallback = filePathCallback
                    val intent = Intent(Intent.ACTION_GET_CONTENT)
                    intent.addCategory(Intent.CATEGORY_OPENABLE)
                    intent.type = "*/*"
                    val chooserIntent = Intent.createChooser(intent, getString(R.string.choose_file))
                    this@HubspotWebActivity.startActivityForResult(chooserIntent, FILE_CHOOSER_REQUEST_CODE)
                    return true
                }
            }
        }
    }

    private fun pushNotificationPermissionGrant(notificationPermission: Boolean) {
        val updateChatProperty = manager.getChatProperties()
        updateChatProperty[ChatPropertyKey.NotificationPermissions.chatPropertyValue] = notificationPermission.toString()
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        if (intent?.action == HubspotFirebaseMessagingService.PUSH_ACTION) {
            pushNotificationPermissionGrant(NotificationManagerCompat.from(this).areNotificationsEnabled())
            val appIntent = Intent(this, HubspotWebActivity::class.java)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                intent.putExtra(HS_PUSH_DATA, intent.extras?.getSerializable(HS_PUSH_DATA, PushNotificationChatData::class.java))
            } else {
                intent.putExtra(HS_PUSH_DATA, intent.getSerializableExtra(HS_PUSH_DATA))
            }
            startActivity(appIntent)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == FILE_CHOOSER_REQUEST_CODE) {
            if (fileUploadCallback == null) {
                super.onActivityResult(requestCode, resultCode, data)
                return
            }
            val results: Array<Uri>? = when {
                resultCode == RESULT_OK && data?.data != null -> arrayOf(data.data!!)
                else -> null
            }
            fileUploadCallback?.onReceiveValue(results)
            fileUploadCallback = null
        } else {
            super.onActivityResult(requestCode, resultCode, data)
        }
    }

    private fun applyEdgeToEdgeInsets() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.VANILLA_ICE_CREAM) {
            val view = findViewById<View>(android.R.id.content)
            ViewCompat.setOnApplyWindowInsetsListener(view) { v, windowInsets ->
                //Retrieve the Insets for system bars, display cutouts and ime
                val bars = windowInsets.getInsets(
                    WindowInsetsCompat.Type.systemBars() or
                            WindowInsetsCompat.Type.displayCutout() or
                            WindowInsetsCompat.Type.ime()
                )
                //Update the padding for the screen content with the retrieved insets
                v.updatePadding(
                    left = bars.left,
                    top = bars.top,
                    right = bars.right,
                    bottom = bars.bottom,
                )
                windowInsets
            }
        }
    }

    /**
     * @suppress("NOT_DOCUMENTED")
     */
    companion object {
        const val FILE_CHOOSER_REQUEST_CODE = 1
        const val CHAT_FLOW_KEY = "chatflow"
    }
}

