/*************************************************
 * HubspotApi.kt
 * Hubspot Mobile SDK
 *
 * Copyright (c) 2024 Hubspot, Inc.
 ************************************************/
package com.hubspot.mobilesdk.metadata

import retrofit2.Response
import com.hubspot.mobilesdk.model.AddDeviceTokenRequest
import com.hubspot.mobilesdk.model.AddDeviceTokenResponse
import com.hubspot.mobilesdk.model.MetaDataRequest
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

/**
 * Internal interface listing all Hubspot API endpoints
 */
internal interface HubspotApi {
    @POST("metadata")
    suspend fun passData(
        @Query("portalId") pId: String,
        @Query("threadId") tId: String,
        @Body request: MetaDataRequest
    ): Response<Unit>

    @POST("device-token")
    suspend fun addNewToken(
        @Query("portalId") pId: String,
        @Body request: AddDeviceTokenRequest
    ): AddDeviceTokenResponse

    @DELETE("device-token/{deviceToken}")
    suspend fun deleteToken(
        @Path("deviceToken") deviceToken: String,
        @Query("portalId") pId: String
    ): Response<Unit>
}