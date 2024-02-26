/*************************************************
 * ApiErrorTransformer.kt
 * Hubspot Mobile SDK
 *
 * Copyright (c) 2024 Hubspot, Inc.
 ************************************************/
package com.hubspot.mobilesdk.errorhandling

import com.hubspot.mobilesdk.errorhandling.NetworkError.*
import com.hubspot.mobilesdk.extension.toApiError
import com.squareup.moshi.JsonDataException
import retrofit2.HttpException
import java.io.FileNotFoundException

/**
 * Transforms all standard Java exceptions along with retrofit defined Http exceptions to custom defined NetworkError.
 */
@Suppress("MagicNumber")
internal object ApiErrorTransformer : ErrorTransformer {
    override fun transform(error: Throwable) =
        when (error) {
            is RuntimeException -> when (error) {
                is JsonDataException -> Data.Parsing(error.message)
                is IllegalArgumentException -> Data.IllegalArgument(error.message)
                is NullPointerException,
                is IndexOutOfBoundsException,
                is FileNotFoundException,
                -> Data.Null(error.message)

                is HttpException -> handleHTTPException(error)
                else -> Data.Generic
            }

            else -> error
        }

    private fun handleHTTPException(error: HttpException) = when (error.code()) {
        401 -> Http.Unauthorized(error.toApiError())
        400, 403, 405, 406, 422 -> Http.BadRequest(error.toApiError())
        404 -> Http.NotFound(error.toApiError())
        408 -> Http.Timeout
        409 -> Http.Conflict(error.toApiError())
        429 -> Http.LimitRateSuppressed
        in 500..599 -> Http.Internal
        else -> Http.Generic
    }
}