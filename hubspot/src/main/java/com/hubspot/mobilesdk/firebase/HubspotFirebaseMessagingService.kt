/*************************************************
 * HubspotFirebaseMessagingService.kt
 * Hubspot Mobile SDK
 *
 * Copyright (c) 2024 Hubspot, Inc.
 ************************************************/
package com.hubspot.mobilesdk.firebase

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.hubspot.mobilesdk.HubspotManager
import com.hubspot.mobilesdk.HubspotWebActivity
import com.hubspot.mobilesdk.R
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.io.Serializable
import javax.inject.Singleton
import kotlin.random.Random
import kotlin.random.nextInt


/**
 * This class represents the Hubspot Push Notifications from the Firebase Cloud Messaging Service
 */
@Singleton
open class HubspotFirebaseMessagingService : FirebaseMessagingService() {
    /**
     * This method is used when FCM send the token for the first time
     */
    override fun onNewToken(token: String) {
        val manager = HubspotManager.getInstance(applicationContext)
        manager.configure()
        runBlocking {
            launch {
                manager.setPushToken(token)
            }
        }
    }

    /**
     * Receives the remoteMessage from the Push Notification
     */
    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)
        sendPushNotificationData(PushNotificationChatData(message.data))
    }

    /**
     * This method is used for sending a notifications including title, message and chatflowid
     * After getting the notifications, it opens the HubspotWebActivity and open the chaturl
     */
    private fun sendPushNotificationData(pushNotificationChatData: PushNotificationChatData) {
        val intent = Intent(applicationContext, HubspotWebActivity::class.java)
        intent.action = PUSH_ACTION
        intent.putExtra(HS_PUSH_DATA, pushNotificationChatData as Serializable)
        val pendingIntent = PendingIntent.getActivity(applicationContext, 0, intent, PendingIntent.FLAG_ONE_SHOT or PendingIntent.FLAG_IMMUTABLE)
        val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val notificationBuilder = NotificationCompat.Builder(applicationContext, applicationContext.getString(R.string.default_notification_channel_id))
            .setSmallIcon(R.drawable.ic_hubspot)
            .setContentTitle(pushNotificationChatData.title ?: "")
            .setContentText(pushNotificationChatData.body ?: "")
            .setAutoCancel(true)
            .setSound(defaultSoundUri)
            .setContentIntent(pendingIntent)

        val notificationManager = applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val channel = NotificationChannel(applicationContext.getString(R.string.default_notification_channel_id), CHANNEL_NAME, NotificationManager.IMPORTANCE_HIGH)
        notificationManager.createNotificationChannel(channel)
        notificationManager.notify(Random.nextInt(1..999999999), notificationBuilder.build())
    }

    /**
     * @suppress("NOT_DOCUMENTED")
     */
    companion object {
        const val PUSH_ACTION = "hubspot.push.action"
        private const val CHANNEL_NAME = "hubspot_channel_name"
        const val HS_PUSH_DATA = "hsPushData"
    }
}
