/*************************************************
 * HubspotManager.kt
 * Hubspot Mobile SDK
 *
 * Copyright (c) 2024 Hubspot, Inc.
 ************************************************/
package com.hubspot.mobilesdk

import android.content.Context
import android.net.Uri
import com.hubspot.mobilesdk.HubspotWebActivity.Companion.CHAT_FLOW_KEY
import com.hubspot.mobilesdk.config.Hublet
import com.hubspot.mobilesdk.config.HubspotConfig
import com.hubspot.mobilesdk.config.HubspotConfig.Companion.defaultConfigFileName
import com.hubspot.mobilesdk.config.HubspotConfigError
import com.hubspot.mobilesdk.util.PreferenceHelper
import com.hubspot.mobilesdk.errorhandling.NetworkError
import com.hubspot.mobilesdk.firebase.PushNotificationChatData
import com.hubspot.mobilesdk.model.DeviceTokenParams
import com.hubspot.mobilesdk.usecases.AddNewDeviceTokenUseCase
import com.hubspot.mobilesdk.usecases.DeleteDeviceTokenUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import timber.log.Timber


/** HubspotManager class enable and disable logs @see [enableLogs] & @see[disableLogs]
 * It manages the configuration changes from the assets file @see [configure]
 * It also builds chatURL @see [chatUrl]
 * It stores the email and token in memory @see [setUserIdentity]
 * It sets chat properties for chat session @see [getChatProperties] & @see [setChatProperties]
 * It sends the PushToken to Hubspot API @see [setPushToken]
 **/
class HubspotManager private constructor(private val context: Context) {
    private var hubspotConfig: HubspotConfig? = null
    private var chatProperties: HashMap<String, String> = HashMap()
    private var chatUrl: String = ""
    private val hubspotPref = PreferenceHelper(context)
    val userIdentityEmail: String
        get() = hubspotPref.email.toString()
    val userIdentityToken: String
        get() = hubspotPref.token.toString()
    private val fcmToken: String
        get() = hubspotPref.fcmToken.toString()

    /**
     * Shares the logs with chat started
     **/
    fun startChat() {
        Timber.w("CHAT STARTED..!!")
    }

    /**
     * Enable the logs
     **/
    fun enableLogs() {
        Timber.plant(object : Timber.DebugTree() {
            override fun createStackElementTag(element: StackTraceElement) =
                "(${element.fileName}:${element.lineNumber})"
        })
    }

    /**
     * Disable the logs
     **/
    fun disableLogs() {
        Timber.uprootAll()
    }

    /**
     * It creates Hubspot configurations
     * When user uses the demo app, it always call this method to configure with the hubspot sdk.
     * This is the only method which is handled by the application.
     * @throws HubspotConfigError when either environment is missing or hublet is missing or portalID is missing
     **/
    @Throws(HubspotConfigError::class)
    fun configure() {
        val jsonFileName = defaultConfigFileName
        val jsonString = context.assets.open(jsonFileName)
            .bufferedReader()
            .use { it.readText() }
        val json = Json.decodeFromString<HubspotConfig>(jsonString)
        hubspotConfig = HubspotConfig(json.environment, json.hublet, json.portalId, json.defaultChatFlow)
    }

    /**
     * Set the user identity token and email. These will be included when starting a chat session to identify the users.
     * These values are only stored in memory and aren't persisted.
     * These values are set when user use the setUserIdentity with passing email and token
     * @param token: The token from the identity api. Must not be empty.
     * @param email: The users email address, that matches the token. Must not be empty
     */

    fun setUserIdentity(email: String, token: String) {
        hubspotPref.email = email
        hubspotPref.token = token
    }

    /**
     * Create chat URL for hubspot webview
     *
     * @throws HubspotConfigError when either environment is missing or hublet is missing or portalID is missing
     **/
    @Throws(HubspotConfigError::class)
    fun chatURL(chatFlow: String? = null, pushData: PushNotificationChatData? = null): String {
        val hublet = hubspotConfig?.hublet?.let { Hublet(it) } ?: throw HubspotConfigError.MissingHubletID
        val portalId = hubspotConfig?.portalId?.let { it } ?: throw HubspotConfigError.MissingPortalID
        val environment = hubspotConfig?.environment?.let { it } ?: throw HubspotConfigError.MissingEnvironment
        val defaultChatFlow = hubspotConfig?.defaultChatFlow

        val components = Uri.Builder()
            .scheme("https")
            .authority("${hublet.appsSubDomain}.hubspot.com")
            .path("/conversations-visitor-embed")
            .appendQueryParameter("portalId", pushData?.portalId ?: portalId)
            .appendQueryParameter("hublet", hublet.id)
            .appendQueryParameter("env", environment)
            .appendQueryParameter("email", hubspotPref.email)
            .appendQueryParameter("identificationToken", hubspotPref.token)
            .build()

        chatUrl = if (!chatFlow.isNullOrEmpty()) {
            components.buildUpon().appendQueryParameter(CHAT_FLOW_KEY, chatFlow).toString()
        } else if (pushData?.chatflow.isNullOrEmpty()) {
            components.buildUpon().appendQueryParameter(CHAT_FLOW_KEY, defaultChatFlow).toString()
        } else {
            components.buildUpon().appendQueryParameter(CHAT_FLOW_KEY, pushData?.chatflow).toString()
        }
        Timber.i("ChatURL=$chatUrl")
        return chatUrl.replace(oldValue = "%40", newValue = "@")
    }

    suspend fun logout() {
        coroutineScope {
            launch {
                deleteDeviceToken(fcmToken)
            }
        }
        hubspotPref.removePreferences()
    }

    /**
     * Sets properties for current chat session
     * @param keyValuePair Property key value pairs
     **/
    fun setChatProperties(keyValuePair: Map<String, String>) {
        for (key in keyValuePair.keys) {
            Timber.d("$key = ${keyValuePair[key]}")
        }
        if (chatProperties.isNotEmpty()) {
            chatProperties.clear()
        }
        this.chatProperties.plusAssign(keyValuePair)
    }

    /**
     * Getter method for the current chat session properties
     **/
    fun getChatProperties() = chatProperties

    /**
     * Internal getter method for hubspot portal id
     **/
    internal fun getPortalId() = hubspotConfig?.portalId?.let { it } ?: throw HubspotConfigError.MissingPortalID

    /**
     * This method is used for sending the token via Hubspot API
     * @param pushToken: It has FCM token
     **/
    suspend fun setPushToken(pushToken: String) {
        try {
            val response = coroutineScope {
                async(Dispatchers.IO) {
                    AddNewDeviceTokenUseCase()
                        .setParameters(DeviceTokenParams(getPortalId(), pushToken))
                        .execute()
                }.await()
            }
            hubspotPref.fcmToken = response.devicePushToken
        } catch (error: NetworkError) {
            Timber.e(HubspotConfigError.AddNewDeviceTokenAPIFailure.message)
        }
    }

    /**
     * This method is used for deleting the push token via Hubspot API
     * @param pushToken: It has FCM token
     **/
    suspend fun deleteDeviceToken(pushToken: String) {
        try {
            coroutineScope {
                async(Dispatchers.IO) {
                    DeleteDeviceTokenUseCase().setParameters(DeviceTokenParams(getPortalId(), pushToken)).execute()
                }.await()
            }
            hubspotPref.removeFcmToken()
        } catch (ex: NetworkError) {
            Timber.e(HubspotConfigError.DeleteDeviceTokenAPIFailure.message)
        }
    }

    /**
     * @suppress("NOT_DOCUMENTED")
     */
    companion object {
        private var INSTANCE: HubspotManager? = null
        fun getInstance(context: Context): HubspotManager {
            if (INSTANCE == null) {
                INSTANCE = HubspotManager(context)
            }
            return INSTANCE!!
        }

        /**
         * Returns true if at least one element matches from Notification Payload
         */
        fun isHubspotNotification(notificationData: Map<String, String>): Boolean {
            val hasAHubspotKey = notificationData.keys.any { key ->
                key.startsWith(PushNotificationChatData.titleKey) ||
                        key.startsWith(PushNotificationChatData.bodyKey) ||
                        key.startsWith(PushNotificationChatData.chatflowKey) ||
                        key.startsWith(PushNotificationChatData.portalIdKey) ||
                        key.startsWith(PushNotificationChatData.threadIdKey)
            }
            return hasAHubspotKey
        }
    }

}
