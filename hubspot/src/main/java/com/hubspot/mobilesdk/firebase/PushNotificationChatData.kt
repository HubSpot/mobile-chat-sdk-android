
/*************************************************
 * PushNotificationChatData.kt
 * Hubspot Mobile SDK
 *
 * Copyright (c) 2024 Hubspot, Inc.
 ************************************************/
package com.hubspot.mobilesdk.firebase

import java.io.Serializable

/**
 * Holds all the data that is hubspot specific, extracted from a push notification collection.
 * A convenient way to bundle all the known parameters together for passing along from notification delegate to the hubspotchatview
 *
 * These keys are also defined as constants here as key value pair:
 * titleKey = Represent the Notification Title
 * bodyKey = Represent the Notification Body
 * portalIdKey = Represent the Notification Portal Id
 * threadIdKey = Represent the Notification Thread Id
 * chatFlowId = Represent the Notification ChatFlowId
 *
 * The presence of any of these keys is use to indicate that the push message is for a hubspot chat.
 * They are used by the helper method ``HubspotManager/isHubspotNotification(notification)``
 *
 */

class PushNotificationChatData(notificationData: Map<String, String>) : Serializable {

    val title: String? = notificationData[titleKey]
    val body: String? = notificationData[bodyKey]
    val portalId: String? = notificationData[portalIdKey]
    val threadId: String? = notificationData[threadIdKey]
    val chatflow: String? = notificationData[chatflowKey]

    /**
     * We want at least one key to be set, otherwise throw an IllegalArgumentException
     */
    init {
        if (title == null && body == null && portalId == null && threadId == null && chatflow == null) {
            throw IllegalArgumentException("At least one key must be set")
        }
    }

    /**
     * @suppress("NOT_DOCUMENTED")
     */
    companion object {
        const val titleKey = "hsTitle"
        const val bodyKey = "hsBody"
        const val portalIdKey = "hsPortalId"
        const val threadIdKey = "hsThreadId"
        const val chatflowKey = "hsChatflowParam"
    }
}
