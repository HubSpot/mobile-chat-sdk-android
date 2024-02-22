/*************************************************
 * ChatPropertyKey.kt
 * Hubspot Mobile SDK
 *
 * Copyright (c) 2024 Hubspot, Inc.
 ************************************************/
package com.hubspot.mobilesdk.metadata

/**
 * Defines keys for all device specific metadata information. These properties are automatically
 * collected by the sdk before uploading it to Hubspot server
 */
sealed class ChatPropertyKey(val chatPropertyValue: String) {
    /**
     * Gets the the model of the device
     */
    data object DeviceModel : ChatPropertyKey(chatPropertyValue = "device_model")

    /**
     * Gets the os version of the device
     */
    data object OperatingSystemVersion : ChatPropertyKey(chatPropertyValue = "os_version")

    /**
     * Gets the resolution for the device including height and width
     */
    data object ScreenResolution : ChatPropertyKey(chatPropertyValue = "screen_resolution")

    /**
     * Gets diagonal screen size for the device
     */
    data object ScreenSize : ChatPropertyKey(chatPropertyValue = "screen_size")

    /**
     * Gets screen orientation for the device
     */
    data object DeviceOrientation : ChatPropertyKey(chatPropertyValue = "device_orientation")

    /**
     * Gets current version of the application using this SDK
     */
    data object AppVersion : ChatPropertyKey(chatPropertyValue = "app_version")

    /**
     * Gets battery level of the device
     */
    data object BatteryLevel : ChatPropertyKey(chatPropertyValue = "battery_level")

    /**
     * Gets camera permissions
     */
    data object CameraPermissions : ChatPropertyKey(chatPropertyValue = "camera_permissions")

    /**
     * Gets photo permissions
     */
    data object PhotoPermissions : ChatPropertyKey(chatPropertyValue = "photo_library_permissions")

    /**
     * Gets notifications permissions
     */
    data object NotificationPermissions : ChatPropertyKey(chatPropertyValue = "notification_permissions")

    /**
     * Gets location permissions
     */
    data object LocationPermissions : ChatPropertyKey(chatPropertyValue = "location_permissions")
    /**
     * Gets the platform
     */
    data object Platform : ChatPropertyKey(chatPropertyValue = "platform")
}