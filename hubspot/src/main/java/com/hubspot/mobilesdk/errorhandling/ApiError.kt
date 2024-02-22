/*************************************************
 * ApiError.kt
 * Hubspot Mobile SDK
 *
 * Copyright (c) 2024 Hubspot, Inc.
 ************************************************/
package com.hubspot.mobilesdk.errorhandling

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

//Todo:Need to update this class based on response error body
/**
 * Internal data class to represent HttpException error content
 */
@JsonClass(generateAdapter = true)
internal data class ApiError(
    @Json(name = "status")
    val status: String,
    @Json(name = "message")
    val displayMessage: String,
)