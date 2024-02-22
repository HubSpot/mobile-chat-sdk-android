package com.example.demo.datastore


interface UserInfoDataStore {
    suspend fun saveUserProfile(userProfile: UserProfile)
    suspend fun getUserProfile(): UserProfile
    suspend fun saveFCMKey(fcmKey: String)
    suspend fun getFcmKey(): String?
    suspend fun clear()
}