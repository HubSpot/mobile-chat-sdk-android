/*************************************************
 * AddDeviceTokenRequest.kt
 * Hubspot Mobile SDK
 *
 * Copyright (c) 2024 Hubspot, Inc.
 ************************************************/
package com.hubspot.mobilesdk.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

/**
 * Request data class for AddNewDeviceToken api call
 */
@JsonClass(generateAdapter = true)
data class AddDeviceTokenRequest(
    @Json(name = "devicePushToken")
    val devicePushToken: String,
    @Json(name = "platform")
    val platform: String,
)
