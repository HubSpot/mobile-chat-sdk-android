/*************************************************
 * ErrorTransformer.kt
 * Hubspot Mobile SDK
 *
 * Copyright (c) 2024 Hubspot, Inc.
 ************************************************/
package com.hubspot.mobilesdk.errorhandling

/**
 * Interface definition for transforming throwables to NetworkError , another custom defined sub-type of Throwable
 */
internal interface ErrorTransformer {
    fun transform(error: Throwable): Throwable

    /**
     * @suppress("NOT_DOCUMENTED")
     */
    companion object {
        val defaultTransformers by lazy {
            listOf(
                HubspotErrorTransformer,
                ApiErrorTransformer,
                ConnectivityErrorTransformer
            )
        }
    }
}

/**
 * Inline function to catch exception in a lambda and perform transformation operation
 */
internal inline fun <T> runCatchingErrors(
    transformers: List<ErrorTransformer> = ErrorTransformer.defaultTransformers,
    target: () -> T,
): T =
    try {
        target.invoke()
    } catch (incoming: Throwable) {
        val exception = transformers
            .map { it.transform(incoming) }
            .reduce { transformed, another ->
                when {
                    transformed == another -> transformed
                    another == incoming -> transformed
                    else -> another
                }
            }
        throw if (exception is NetworkError) {
            exception
        } else {
            NetworkError.Data.Generic
        }
    }