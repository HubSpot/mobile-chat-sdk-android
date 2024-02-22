/*************************************************
 * CompileAndUploadMetaDataUseCase.kt
 * Hubspot Mobile SDK
 *
 * Copyright (c) 2024 Hubspot, Inc.
 ************************************************/
package com.hubspot.mobilesdk.usecases

import android.content.Context
import android.os.Build
import com.hubspot.mobilesdk.HubspotManager
import com.hubspot.mobilesdk.errorhandling.runCatchingErrors
import com.hubspot.mobilesdk.metadata.ChatPropertyKey
import com.hubspot.mobilesdk.model.MetaDataRequest
import com.hubspot.mobilesdk.network.NetworkDependencies
import com.hubspot.mobilesdk.util.DeviceInformation
import retrofit2.Response

/**
 * UseCase that handles posting of chat metadata to the hubspot server
 */
internal class CompileAndUploadMetaDataUseCase(
    private val manager: HubspotManager,
    val context: Context
) : UseCase<String, Response<Unit>>() {

    override suspend fun execute(): Response<Unit> = runCatchingErrors {
        return NetworkDependencies.getHubspotApi().passData(
            pId = manager.getPortalId(), tId = parameters, request = MetaDataRequest(manager.userIdentityEmail, manager.userIdentityToken, fetchDeviceInformation())
        )
    }

    /**
     * Fetches device information and organizes them in key value pairs before passing them to Hubspot backend
     */
    private fun fetchDeviceInformation(): Map<String, String> {
        return manager.getChatProperties().plus(
            mapOf(
                ChatPropertyKey.DeviceModel.chatPropertyValue to Build.MODEL.toString(),
                ChatPropertyKey.OperatingSystemVersion.chatPropertyValue to "Android " + Build.VERSION.RELEASE,
                ChatPropertyKey.ScreenResolution.chatPropertyValue to DeviceInformation.getScreenResolution(),
                ChatPropertyKey.ScreenSize.chatPropertyValue to DeviceInformation.getScreenSize(context),
                ChatPropertyKey.DeviceOrientation.chatPropertyValue to DeviceInformation.getScreenOrientation(context),
                ChatPropertyKey.AppVersion.chatPropertyValue to DeviceInformation.getAppVersion(context),
                ChatPropertyKey.BatteryLevel.chatPropertyValue to DeviceInformation.getBatteryLevel(context),
                ChatPropertyKey.Platform.chatPropertyValue to "android"
            )
        )
    }
}