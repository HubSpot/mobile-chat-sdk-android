package com.example.demo.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class UserInfoDataStoreImpl @Inject constructor(@ApplicationContext private val context: Context) :
    UserInfoDataStore {
    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = USER_DATASTORE_KEY)
    private val emailKey by lazy { stringPreferencesKey(name = EMAIL_KEY) }
    private val tokenKey by lazy { stringPreferencesKey(name = TOKEN_KEY) }
    private val fcmKey by lazy { stringPreferencesKey(name = FCM_KEY) }

    override suspend fun saveUserProfile(userProfile: UserProfile) {
        context.dataStore.edit { preferences ->
            with(userProfile) {
                preferences[emailKey] = email
                preferences[tokenKey] = token
            }
        }
    }

    override suspend fun getUserProfile(): UserProfile {
        return context.dataStore.data.map { preferences ->
            UserProfile(
                email = preferences[emailKey].orEmpty(),
                token = preferences[tokenKey].orEmpty(),
            )
        }.first()
    }

    override suspend fun saveFCMKey(fcm: String) {
        context.dataStore.edit {
            it[fcmKey] = fcm
        }
    }

    override suspend fun getFcmKey(): String? {
        return context.dataStore.data.first()[fcmKey]
    }

    override suspend fun clear() {
        context.dataStore.edit { preferences -> preferences.clear() }
    }

    companion object {
        private const val USER_DATASTORE_KEY = "user_datastore_key"
        private const val EMAIL_KEY = "email_key"
        private const val TOKEN_KEY = "token_key"
        private const val FCM_KEY = "fcm_key"
    }
}