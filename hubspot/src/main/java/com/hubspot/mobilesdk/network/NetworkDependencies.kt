/*************************************************
 * NetworkDependencies.kt
 * Hubspot Mobile SDK
 *
 * Copyright (c) 2024 Hubspot, Inc.
 ************************************************/
package com.hubspot.mobilesdk.network

import android.net.Uri
import com.hubspot.mobilesdk.BuildConfig
import com.hubspot.mobilesdk.config.Environment
import com.hubspot.mobilesdk.config.Hublet
import com.hubspot.mobilesdk.config.HubspotConfig
import com.hubspot.mobilesdk.config.HubspotConfigError
import com.hubspot.mobilesdk.metadata.HubspotApi
import com.squareup.moshi.Moshi
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

/**
 * Singleton object providing API instance
 */
internal object NetworkDependencies {

    private var baseUrl = "https://api.hubapi.com/livechat-public/v1/mobile-sdk/"
    private var hubspotApi: HubspotApi? = null

    fun getHubspotApi(): HubspotApi {
        if (hubspotApi == null) {
            synchronized(this) {
                hubspotApi = Retrofit.Builder()
                    .baseUrl(baseUrl)
                    .client(createOkHttpClient())
                    .addConverterFactory(createMoshiConverterFactory(createMoshi()))
                    .build()
                    .create(HubspotApi::class.java)
            }
        }
        return hubspotApi!!
    }

    fun configure(config: HubspotConfig) {
        val hublet = Hublet(config.hublet)
        val environment = Environment(config.environment)
        val configuredUrl = "https://${hublet.apiSubDomain}.hubapi${environment.chatURLSuffix}.com/livechat-public/v1/mobile-sdk/"

        baseUrl = configuredUrl
    }

    private fun createMoshiConverterFactory(moshi: Moshi): MoshiConverterFactory {
        return MoshiConverterFactory.create(moshi)
    }

    private fun createMoshi(): Moshi {
        return Moshi.Builder().build()
    }

    private fun createOkHttpClient(): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(
                HttpLoggingInterceptor().apply {
                    level = if (BuildConfig.DEBUG) {
                        HttpLoggingInterceptor.Level.BODY
                    } else {
                        HttpLoggingInterceptor.Level.NONE
                    }
                }
            ).build()
    }
}