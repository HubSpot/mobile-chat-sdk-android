/*************************************************
 * HubspotCallback.kt
 * Hubspot Mobile SDK
 *
 * Copyright (c) 2024 Hubspot, Inc.
 ************************************************/
package com.hubspot.mobilesdk

fun interface HubspotCallback {

    fun onComplete(error: Throwable?)
}
