/*************************************************
 * HubspotErrorTransformer.kt
 * Hubspot Mobile SDK
 *
 * Copyright (c) 2024 Hubspot, Inc.
 ************************************************/
package com.hubspot.mobilesdk.errorhandling

import com.hubspot.mobilesdk.config.HubspotConfigError


/**
 * Transforms all domain specific exceptions to NetworkError.
 */
internal object HubspotErrorTransformer : ErrorTransformer {

    override fun transform(error: Throwable): Throwable {
        return when (error) {
            is RuntimeException -> when (error) {
                is HubspotConfigError.MissingHubletID,
                is HubspotConfigError.MissingPortalID,
                is HubspotConfigError.MissingEnvironment,
                is HubspotConfigError.AddNewDeviceTokenAPIFailure,
                -> NetworkError.Data.MissingParams(error.message ?: "Couldn't find parameter")

                else -> NetworkError.Data.Generic
            }

            else -> error
        }
    }

}