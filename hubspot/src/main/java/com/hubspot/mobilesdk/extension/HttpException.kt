/*************************************************
 * HttpException.kt
 * Hubspot Mobile SDK
 *
 * Copyright (c) 2024 Hubspot, Inc.
 ************************************************/
package com.hubspot.mobilesdk.extension

import com.hubspot.mobilesdk.errorhandling.ApiError
import com.squareup.moshi.Moshi
import retrofit2.HttpException
import timber.log.Timber

private val moshi: Moshi by lazy { Moshi.Builder().build() }


/**
 * Extension function to convert Json content from HttpException response body to corresponding object
 */
internal fun HttpException.toApiError(): ApiError? {
    return try {
        val json = response()?.errorBody()?.string().orEmpty()
        moshi.adapter(ApiError::class.java).fromJson(json)
    } catch (ex: Exception) {
        Timber.e(ex, "Failed parsing api error response")
        null
    }
}