/*************************************************
 * HubspotConfiguration.kt
 * Hubspot Mobile SDK
 *
 * Copyright (c) 2024 Hubspot, Inc.
 ************************************************/
package com.hubspot.mobilesdk.config

import androidx.annotation.Keep
import com.hubspot.mobilesdk.config.HubspotConfig.Companion.defaultConfigFileName
import kotlinx.serialization.Serializable
import java.util.Locale

/**
 * HubspotEnvironment class has either QA or PRODUCTION variables
 */
sealed class HubspotEnvironment(open var env: String) {
    object QA : HubspotEnvironment("qa")

    object PRODUCTION : HubspotEnvironment("prod")
}

/**
 * Hublet class represents the application sub domain based on Locale
 */
internal data class Hublet(val id: String) {
    private val defaultUS = "na1"
    val appsSubDomain: String
        get() {
            return if (id.lowercase(Locale.ROOT) == defaultUS) {
                "app"
            } else {
                "app-$id"
            }
        }
}

/**
 * Hubspot Configuration Error class represents different errors when missing properties
 */
internal sealed class HubspotConfigError : Throwable() {
    /**
     * Shows error when hublet id is missing
     */
    data object MissingHubletID : HubspotConfigError() {
        override val message: String
            get() = "Couldn't find a hublet id from this file $defaultConfigFileName"
    }

    /**
     * Shows error when portal id is missing
     */
    data object MissingPortalID : HubspotConfigError() {
        override val message: String
            get() = "Couldn't find a portal Id from this file: $defaultConfigFileName"
    }

    /**
     * Shows error when environment is missing
     */
    data object MissingEnvironment : HubspotConfigError() {
        override val message: String
            get() = "Couldn't find a environment from this file: $defaultConfigFileName"
    }

    /**
     * Shows error when default chatflow  is missing
     */
    data object MissingDefaultChatFlow : HubspotConfigError() {
        override val message: String
            get() = "Couldn't find a default chat flow from this file: $defaultConfigFileName"
    }

    /**
     * Shows error when Add new device token API failed
     */
    data object AddNewDeviceTokenAPIFailure : HubspotConfigError() {
        override val message: String
            get() = "Add new device token API fails from the hubspot"
    }

    /**
     * Shows error when Delete new device token API failed
     */
    data object DeleteDeviceTokenAPIFailure : HubspotConfigError() {
        override val message: String
            get() = "Delete device token API fails from the hubspot"
    }

    /**
     * Shows error when Meta Data API Failed
     */
    data object MetaDataAPIFailure : HubspotConfigError() {
        override val message: String
            get() = "MetaData API fails from the hubspot"
    }
}

@Keep
@Serializable
internal data class HubspotConfig(
    val environment: String,
    val hublet: String,
    val portalId: String,
    val defaultChatFlow: String,
) {
    /**
     * @suppress("NOT_DOCUMENTED")
     */
    companion object {
        const val defaultConfigFileName = "hubspot-info.json"
    }
}
