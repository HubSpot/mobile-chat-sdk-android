/*************************************************
 * DeviceTokenParams.kt
 * Hubspot Mobile SDK
 *
 * Copyright (c) 2024 Hubspot, Inc.
 ************************************************/
package com.hubspot.mobilesdk.model

/**
 *  DeviceTokenParams is used for pass a parameter while calling AddNewDeviceTokenAPI
 */
data class DeviceTokenParams(val portalId: String, val pushToken: String)