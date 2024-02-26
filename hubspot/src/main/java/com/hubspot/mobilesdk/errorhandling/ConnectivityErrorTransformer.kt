/*************************************************
 * ConnectivityErrorTransformer.kt
 * Hubspot Mobile SDK
 *
 * Copyright (c) 2024 Hubspot, Inc.
 ************************************************/
package com.hubspot.mobilesdk.errorhandling

import java.io.IOException
import java.io.InterruptedIOException
import java.net.BindException
import java.net.ConnectException
import java.net.NoRouteToHostException
import java.net.PortUnreachableException
import java.net.ProtocolException
import java.net.SocketException
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import java.net.UnknownServiceException
import java.nio.channels.ClosedChannelException
import javax.net.ssl.SSLException

/**
 * Transforms all standard Java exceptions to custom defined NetworkError.
 */
internal object ConnectivityErrorTransformer : ErrorTransformer {
    override fun transform(error: Throwable) =
        when (error) {
            is IOException -> when (error) {
                is SocketTimeoutException -> NetworkError.Connectivity.Timeout
                is BindException,
                is ClosedChannelException,
                is ConnectException,
                is NoRouteToHostException,
                is PortUnreachableException,
                -> NetworkError.Connectivity.HostUnreachable

                is InterruptedIOException,
                is UnknownServiceException,
                is UnknownHostException,
                -> NetworkError.Connectivity.FailedConnection

                is ProtocolException,
                is SocketException,
                is SSLException,
                -> NetworkError.Connectivity.BadConnection

                else -> NetworkError.Connectivity.Generic
            }

            else -> error
        }
}