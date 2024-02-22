package com.example.demo

import android.app.Application
import com.hubspot.mobilesdk.HubspotManager
import com.hubspot.mobilesdk.metadata.ChatPropertyKey
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
class MainApplication : Application() {
    @Inject
    lateinit var hubspotManager: HubspotManager

    override fun onCreate() {
        super.onCreate()

        hubspotManager.enableLogs()
        hubspotManager.configure()
        hubspotManager.startChat()
        hubspotManager.setChatProperties(
            mapOf(
                ChatPropertyKey.CameraPermissions.chatPropertyValue to "false",
                ChatPropertyKey.PhotoPermissions.chatPropertyValue to "false",
                ChatPropertyKey.NotificationPermissions.chatPropertyValue to "false",
                ChatPropertyKey.LocationPermissions.chatPropertyValue to "false"
            )
        )
    }
}
