package com.example.demo.firebase

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.media.RingtoneManager
import androidx.core.app.NotificationCompat
import com.example.demo.R
import com.google.firebase.messaging.RemoteMessage
import com.hubspot.mobilesdk.HubspotManager
import com.hubspot.mobilesdk.firebase.HubspotFirebaseMessagingService
import timber.log.Timber
import kotlin.random.Random
import kotlin.random.nextInt

class DemoFirebaseMessagingService : HubspotFirebaseMessagingService() {

    override fun onMessageReceived(message: RemoteMessage) {
        if (HubspotManager.isHubspotNotification(message.data)) {
            super.onMessageReceived(message)
        } else {
            sendNotificationDemo(message)
        }

    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Timber.i("onNewToken")
    }

    private fun sendNotificationDemo(message: RemoteMessage) {
        val intent = Intent(applicationContext, DemoActivity::class.java)
        intent.putExtra(NOTIFICATION_DATA, message.notification?.title ?: "No Title")
        val pendingIntent = PendingIntent.getActivity(applicationContext, 1, intent, PendingIntent.FLAG_ONE_SHOT or PendingIntent.FLAG_IMMUTABLE)
        val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val notificationBuilder = NotificationCompat.Builder(applicationContext, CHANNEL_ID)
            .setGroupSummary(true)
            .setGroup("Hubspot")
            .setSmallIcon(R.drawable.ic_back_arrow)
            .setContentTitle(message.notification?.title ?: "No Title")
            .setContentText(message.notification?.body ?: "No Body")
            .setAutoCancel(true)
            .setSound(defaultSoundUri)
            .setContentIntent(pendingIntent)

        val notificationManager = applicationContext.getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        val channel = NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_HIGH)
        notificationManager.createNotificationChannel(channel)
        notificationManager.notify(Random.nextInt(1..999999999), notificationBuilder.build())
    }

    companion object {
        private const val CHANNEL_ID = "demo"
        private const val CHANNEL_NAME = "demo_channel_name"
        const val NOTIFICATION_DATA = "data"
    }
}
