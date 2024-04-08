/*************************************************
 * SDKOptionAction.kt
 * Hubspot Mobile SDK
 *
 * Copyright (c) 2024 Hubspot, Inc.
 ************************************************/
package com.example.demo.sdkoption

import com.example.demo.base.Action
import com.example.demo.base.Effect

sealed class SDKOptionAction : Action() {
    data object EmailTokenAvailable : SDKOptionAction()
    data object ClearData: SDKOptionAction()
}

sealed class SDKOptionEffect : Effect() {
    data class EmailTokenAvailable(val email: String, val token: String) : SDKOptionEffect()
    data object ClearData: SDKOptionEffect()
}