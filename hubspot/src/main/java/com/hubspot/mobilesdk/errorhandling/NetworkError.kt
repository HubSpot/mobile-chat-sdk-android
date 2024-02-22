/*************************************************
 * NetworkError.kt
 * Hubspot Mobile SDK
 *
 * Copyright (c) 2024 Hubspot, Inc.
 ************************************************/
package com.hubspot.mobilesdk.errorhandling

import androidx.annotation.StringRes
import com.hubspot.mobilesdk.R


/**
 * Defines all possible exceptions
 */
internal sealed class NetworkError(@StringRes val errorResId: Int, val errorMessage: String? = null) :
    Exception() {
    sealed class Data(errorResId: Int = R.string.default_error_msg, errorMessage: String? = null) : NetworkError(
        errorResId,
        errorMessage
    ) {
        data class Parsing(override val message: String?) : Data(errorMessage = message)
        data class IllegalArgument(override val message: String?) : Data(errorMessage = message)
        data class Null(override val message: String?) : Data(errorMessage = message)
        data object Generic : Data()
        data class MissingParams(override val message: String) : Data(errorMessage = message)
    }

    sealed class Http(errorResId: Int = R.string.default_error_msg, errorMessage: String? = null) : NetworkError(
        errorResId,
        errorMessage
    ) {
        data class Unauthorized(val apiError: ApiError?) : Http(errorMessage = apiError?.displayMessage)
        data class Conflict(val apiError: ApiError?) : Http(errorMessage = apiError?.displayMessage)
        data class NotFound(val apiError: ApiError? = null) : Http(errorMessage = apiError?.displayMessage)
        data object Timeout : Http()
        data object LimitRateSuppressed : Http()
        data class BadRequest(val apiError: ApiError? = null) : Http(errorMessage = apiError?.displayMessage)
        data object Internal : Http()
        data object Generic : Http()
    }

    sealed class Connectivity(errorResId: Int = R.string.label_there_is_an_issue_with_the_network_connection) : NetworkError(
        errorResId
    ) {
        data object Timeout : Connectivity()
        data object HostUnreachable : Connectivity()
        data object FailedConnection : Connectivity()
        data object BadConnection : Connectivity()
        data object Generic : Connectivity()
    }
}