/*************************************************
 * DeleteDeviceTokenUseCase.kt
 * Hubspot Mobile SDK
 *
 * Copyright (c) 2024 Hubspot, Inc.
 ************************************************/
package com.hubspot.mobilesdk.usecases

import com.hubspot.mobilesdk.errorhandling.runCatchingErrors
import com.hubspot.mobilesdk.model.DeviceTokenParams
import com.hubspot.mobilesdk.network.NetworkDependencies
import retrofit2.Response

internal class DeleteDeviceTokenUseCase : UseCase<DeviceTokenParams, Response<Unit>>() {
    override suspend fun execute(): Response<Unit> = runCatchingErrors {
        return NetworkDependencies.getHubspotApi().deleteToken(parameters.pushToken, parameters.portalId)
    }
}