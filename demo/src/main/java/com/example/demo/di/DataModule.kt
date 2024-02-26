package com.example.demo.di

import android.content.Context
import com.example.demo.datastore.UserInfoDataStore
import com.example.demo.datastore.UserInfoDataStoreImpl
import com.hubspot.mobilesdk.HubspotManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DataModule {
    @Singleton
    @Provides
    fun provideUserInfoDataStore(userInfoDataStoreImpl: UserInfoDataStoreImpl): UserInfoDataStore {
        return userInfoDataStoreImpl
    }

    @Singleton
    @Provides
    fun providesHubspotManager(@ApplicationContext context: Context): HubspotManager {
        return HubspotManager.getInstance(context)
    }
}