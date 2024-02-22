/*************************************************
 * MetaDataRequest.kt
 * Hubspot Mobile SDK
 *
 * Copyright (c) 2024 Hubspot, Inc.
 ************************************************/
package com.hubspot.mobilesdk.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

/**
 * Request data class for MetaData api call
 */
@JsonClass(generateAdapter = true)
data class MetaDataRequest(
    @Json(name = "email")
    val email: String,
    @Json(name = "visitorToken")
    val visitorToken: String,
    @Json(name = "metadata")
    val metaData: Map<String, String>?,
)
