package com.example.demo

import android.app.Application
import com.hubspot.mobilesdk.HubspotManager
import com.hubspot.mobilesdk.metadata.ChatPropertyKey
import dagger.hilt.android.HiltAndroidApp
import timber.log.Timber
import javax.inject.Inject

@HiltAndroidApp
class MainApplication : Application() {
    @Inject
    lateinit var hubspotManager: HubspotManager

    override fun onCreate() {
        super.onCreate()

        hubspotManager.enableLogs()
        try {
            hubspotManager.configure()
        } catch (error: Throwable) {
            Timber.e(error, "Hubspot SDK not configured — check hubspot-info.json in demo/src/main/assets")
        }
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
