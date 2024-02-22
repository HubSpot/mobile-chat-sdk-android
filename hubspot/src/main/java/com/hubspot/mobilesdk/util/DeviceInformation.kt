/*************************************************
 * DeviceInformation.kt
 * Hubspot Mobile SDK
 *
 * Copyright (c) 2024 Hubspot, Inc.
 ************************************************/
package com.hubspot.mobilesdk.util

import android.content.Context
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.content.res.Resources
import android.os.BatteryManager
import timber.log.Timber
import kotlin.math.ceil
import kotlin.math.pow
import kotlin.math.sqrt

/**
 * Consists of static helper methods to fetch device and app specific information
 */
internal class DeviceInformation {

    companion object {

        private const val ORIENTATION_LANDSCAPE = "landscape"
        private const val ORIENTATION_PORTRAIT = "portrait"

        /**
         * Gets battery level of the device
         */
        fun getBatteryLevel(context: Context): String {
            val batteryManager = context.getSystemService(Context.BATTERY_SERVICE) as BatteryManager
            return batteryManager.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY)
                .toString()
        }

        /**
         * Gets screen resolution for the device
         */
        fun getScreenResolution(): String {
            val width = Resources.getSystem().displayMetrics.widthPixels
            val height = Resources.getSystem().displayMetrics.heightPixels
            return "$width x $height"
        }

        /**
         * Gets diagonal screen size for the device
         */
        fun getScreenSize(context: Context): String {
            val configuration = context.resources.configuration
            val baselineDensity = 160.toDouble()
            val heightInInch =
                (configuration.screenHeightDp / baselineDensity) //A dp corresponds to the physical size of a pixel at 160 dpi (https://developer.android.com/training/multiscreen/screendensities.html#TaskUseD)
            val widthInInch = (configuration.screenWidthDp / baselineDensity)
            val diagonalSize = ceil(sqrt(heightInInch.pow(2) + widthInInch.pow(2)))
            return diagonalSize.toString()
        }

        /**
         * Gets current version of the application using this SDK
         */
        fun getAppVersion(context: Context): String {
            return try {
                val pInfo = context.packageManager.getPackageInfo(context.packageName, 0)
                pInfo.versionName
            } catch (e: PackageManager.NameNotFoundException) {
                Timber.e(e)
                "N/A"
            }
        }

        /**
         * Gets screen orientation for the device
         */
        fun getScreenOrientation(context: Context): String {
            val orientation = context.resources.configuration.orientation
            return if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
                ORIENTATION_LANDSCAPE
            } else {
                ORIENTATION_PORTRAIT
            }
        }
    }
}