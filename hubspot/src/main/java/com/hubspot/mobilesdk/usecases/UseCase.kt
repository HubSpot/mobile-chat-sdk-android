/*************************************************
 * UseCase.kt
 * Hubspot Mobile SDK
 *
 * Copyright (c) 2024 Hubspot, Inc.
 ************************************************/
package com.hubspot.mobilesdk.usecases

/**
 * An abstract template for all use cases within hubspot sdk
 */
internal abstract class UseCase<Params, out T> {
    private var _parameters: Params? = null
    val parameters: Params
        get() = _parameters ?: throw IllegalArgumentException("Required parameters were null")

    fun setParameters(parameters: Params): UseCase<Params, T> = apply {
        _parameters = parameters
    }

    abstract suspend fun execute(): T
}