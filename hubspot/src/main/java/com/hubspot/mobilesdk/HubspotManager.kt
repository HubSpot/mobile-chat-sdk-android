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
import com.hubspot.mobilesdk.config.Environment
import com.hubspot.mobilesdk.config.Hublet
import com.hubspot.mobilesdk.config.HubspotConfig
import com.hubspot.mobilesdk.config.HubspotConfig.Companion.defaultConfigFileName
import com.hubspot.mobilesdk.config.HubspotConfigError
import com.hubspot.mobilesdk.config.HubspotEnvironment
import com.hubspot.mobilesdk.util.PreferenceHelper
import com.hubspot.mobilesdk.errorhandling.NetworkError
import com.hubspot.mobilesdk.firebase.PushNotificationChatData
import com.hubspot.mobilesdk.model.DeviceTokenParams
import com.hubspot.mobilesdk.network.NetworkDependencies
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
 * It also builds chatURL @see [chatURL]
 * It persists the user's email and identity token on the device @see [setUserIdentity]
 * It sets chat properties for chat session @see [getChatProperties] & @see [setChatProperties]
 * It sends the PushToken to Hubspot API @see [setPushToken]
 **/
class HubspotManager private constructor(private val context: Context) {

    @Volatile
    private var hubspotConfig: HubspotConfig? = null

    private val configureLock = Any()
    private val logsLock = Any()

    private var chatProperties: HashMap<String, String> = HashMap()
    private val hubspotPref = PreferenceHelper(context)
    val userIdentityEmail: String
        get() = hubspotPref.email.toString()
    val userIdentityToken: String
        get() = hubspotPref.token.toString()
    private val fcmToken: String
        get() = hubspotPref.fcmToken.toString()

    private var loggingTree: Timber.Tree? = null

    /**
     * Writes a log line marking the start of a chat.
     **/
    @Deprecated(
        message = "startChat() only logs. Launch HubspotWebActivity or use one of the chat widgets instead.",
    )
    fun startChat() {
        Timber.w("CHAT STARTED..!!")
    }

    /**
     * Enable the logs.
     **/
    fun enableLogs() {
        synchronized(logsLock) {
            if (loggingTree != null) return
            val tree = object : Timber.DebugTree() {
                override fun createStackElementTag(element: StackTraceElement) =
                    "(${element.fileName}:${element.lineNumber})"
            }
            loggingTree = tree
            Timber.plant(tree)
        }
    }

    /**
     * Disable the logs.
     **/
    fun disableLogs() {
        synchronized(logsLock) {
            loggingTree?.let {
                Timber.uproot(it)
                loggingTree = null
            }
        }
    }

    /**
     * Creates Hubspot configuration from JSON asset file.
     *
     * When user uses the demo app, it always call this method to configure with the hubspot sdk.
     * This is the only method which is handled by the application.
     *
     * This method is synchronous and must be called before any other SDK method.
     * Requires a valid `hubspot-info.json` file in the app's assets folder.
     *
     * Does nothing if configure has already been called for this instance.
     **/
    fun configure() {
        synchronized(configureLock) {
            if (hubspotConfig != null) {
                // Configuration already exists, no need to configure again
                return
            }

            val jsonString = context.assets.open(defaultConfigFileName)
                .bufferedReader()
                .use { it.readText() }
            val json = Json.decodeFromString<HubspotConfig>(jsonString)

            configure(
                environment = json.environment,
                hublet = json.hublet,
                portalId = json.portalId,
                defaultChatFlow = json.defaultChatFlow,
            )
        }
    }

    /**
     * Creates HubSpot configuration programmatically.
     *
     * This method allows applications to configure the SDK dynamically without
     * requiring a `hubspot-info.json` asset bundled in the APK.
     *
     * This is useful when configuration values are retrieved securely from a backend
     * service after authentication or during runtime.
     *
     * @param environment HubSpot environment identifier (e.g. "prod", "qa").
     * @param hublet HubSpot data center identifier (e.g. "na1", "eu1").
     * @param portalId HubSpot portal identifier.
     * @param defaultChatFlow Default HubSpot chat flow identifier.
     *
     * This method is synchronous and must be called before any other SDK method.
     */
    fun configure(
        environment: String,
        hublet: String,
        portalId: String,
        defaultChatFlow: String,
    ) {
        if (hublet.isBlank()) throw HubspotConfigError.MissingHubletID
        if (portalId.isBlank()) throw HubspotConfigError.MissingPortalID
        if (defaultChatFlow.isBlank()) throw HubspotConfigError.MissingDefaultChatFlow
        HubspotEnvironment.from(environment) ?: throw HubspotConfigError.MissingEnvironment

        val config = HubspotConfig(
            environment = environment,
            hublet = hublet,
            portalId = portalId,
            defaultChatFlow = defaultChatFlow,
        )

        synchronized(configureLock) {
            hubspotConfig = config
            NetworkDependencies.configure(config)
        }
    }

    /**
     * Set the user identity token and email. These will be included when starting a chat session to identify the users.
     * These values are persisted on the device and aren't held in memory only.
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
     * This method is synchronous and must be called after [configure].
     * @throws HubspotConfigError when either environment is missing or hublet is missing or portalID is missing
     **/
    @Throws(HubspotConfigError::class)
    fun chatURL(chatFlow: String? = null, pushData: PushNotificationChatData? = null): String {
        val config = hubspotConfig
        val hublet = config?.hublet?.let { Hublet(it) } ?: throw HubspotConfigError.MissingHubletID
        val portalId = config?.portalId ?: throw HubspotConfigError.MissingPortalID
        val environment = config?.environment?.let { Environment(it) } ?: throw HubspotConfigError.MissingEnvironment
        val defaultChatFlow = config?.defaultChatFlow

        val components = Uri.Builder()
            .scheme("https")
            .authority("${hublet.appsSubDomain}.hubspot${environment.chatURLSuffix}.com")
            .path("/conversations-visitor-embed")
            .appendQueryParameter("portalId", pushData?.portalId ?: portalId)
            .appendQueryParameter("hublet", hublet.id)
            .appendQueryParameter("env", environment.environment.value)
            .appendQueryParameter("email", hubspotPref.email)
            .appendQueryParameter("identificationToken", hubspotPref.token)
            .build()

        val chatUrl = if (!chatFlow.isNullOrEmpty()) {
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
     * Getter method for hubspot portal id
     * @throws HubspotConfigError when portal id is missing
     **/
    @Throws(HubspotConfigError::class)
    fun getPortalId() = hubspotConfig?.portalId ?: throw HubspotConfigError.MissingPortalID

    /**
     * Getter method for hubspot Hublet
     * @throws HubspotConfigError when hublet id is missing
     **/
    @Throws(HubspotConfigError::class)
    fun getHublet() = hubspotConfig?.hublet ?: throw HubspotConfigError.MissingHubletID

    /**
     * Getter method for hubspot Environment
     * @throws HubspotConfigError when environment is missing
     **/
    @Throws(HubspotConfigError::class)
    fun getEnvironment() = hubspotConfig?.environment ?: throw HubspotConfigError.MissingEnvironment

    /**
     * Getter method for hubspot DefaultChatFlow
     * @throws HubspotConfigError when default chat flow is missing
     **/
    @Throws(HubspotConfigError::class)
    fun getDefaultChatFlow() = hubspotConfig?.defaultChatFlow ?: throw HubspotConfigError.MissingDefaultChatFlow

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
     * This method is used for getting a PushToken from the shared preferences
     */
    fun getPushToken() = hubspotPref.fcmToken.toString()

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

        @Volatile
        private var INSTANCE: HubspotManager? = null

        private val instanceLock = Any()

        @JvmStatic
        fun getInstance(context: Context): HubspotManager {
            INSTANCE?.let { return it }
            return synchronized(instanceLock) {
                INSTANCE ?: HubspotManager(context.applicationContext).also { INSTANCE = it }
            }
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
