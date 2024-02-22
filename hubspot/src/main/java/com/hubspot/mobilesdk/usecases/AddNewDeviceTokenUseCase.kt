/*************************************************
 * AddNewDeviceTokenUseCase.kt
 * Hubspot Mobile SDK
 *
 * Copyright (c) 2024 Hubspot, Inc.
 ************************************************/

package com.hubspot.mobilesdk.usecases

import com.hubspot.mobilesdk.model.AddDeviceTokenResponse
import com.hubspot.mobilesdk.errorhandling.runCatchingErrors
import com.hubspot.mobilesdk.model.AddDeviceTokenRequest
import com.hubspot.mobilesdk.model.DeviceTokenParams
import com.hubspot.mobilesdk.network.NetworkDependencies

internal class AddNewDeviceTokenUseCase : UseCase<DeviceTokenParams, AddDeviceTokenResponse>() {
    override suspend fun execute(): AddDeviceTokenResponse = runCatchingErrors {
        return NetworkDependencies.getHubspotApi().addNewToken(
            pId = parameters.portalId, request = AddDeviceTokenRequest(devicePushToken = parameters.pushToken, platform = ANDROID)
        )
    }

    companion object {
        private const val ANDROID = "android"
    }
}