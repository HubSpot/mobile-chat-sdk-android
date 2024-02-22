/*************************************************
 * PreferenceHelper.kt
 * Hubspot Mobile SDK
 *
 * Copyright (c) 2024 Hubspot, Inc.
 ************************************************/
package com.hubspot.mobilesdk.util

import android.content.Context
import android.content.SharedPreferences

/**
 * The PreferenceHelper class used to store the email and token for the hubspot configuration
 */
class PreferenceHelper(context: Context) {
    private val preferences: SharedPreferences = context.getSharedPreferences(hubspotPreferences, Context.MODE_PRIVATE)

    var email: String?
        get() = preferences.getString(prefEmail, "")
        set(value) = preferences.edit().putString(prefEmail, value).apply()

    var token: String?
        get() = preferences.getString(prefToken, "")
        set(value) = preferences.edit().putString(prefToken, value).apply()

    var fcmToken: String?
        get() = preferences.getString(prefFCMToken, "")
        set(value) = preferences.edit().putString(prefFCMToken, value).apply()

    fun removePreferences() {
        preferences.edit().remove(prefEmail).apply()
        preferences.edit().remove(prefToken).apply()
        preferences.edit().remove(prefFCMToken).apply()
    }

    fun removeFcmToken() {
        preferences.edit().remove(prefFCMToken).apply()
    }

    /**
     * @suppress("NOT_DOCUMENTED")
     */
    companion object {
        private const val prefEmail = "EMAIL"
        private const val prefToken = "TOKEN"
        private const val prefFCMToken = "FCM_TOKEN"
        private const val hubspotPreferences = "hubspot_user_data"
    }
}
