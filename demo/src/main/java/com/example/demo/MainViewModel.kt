package com.example.demo

import androidx.lifecycle.viewModelScope
import com.example.demo.base.BaseViewModel
import com.example.demo.datastore.UserInfoDataStore
import com.hubspot.mobilesdk.HubspotManager
import com.hubspot.mobilesdk.metadata.ChatPropertyKey
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(private val dataStore: UserInfoDataStore, private val manager: HubspotManager) :
    BaseViewModel<MainAction>() {
    override fun processAction(action: MainAction) {
        when (action) {
            is MainAction.SaveFCMToken -> saveFCMToken(action.token)
            is MainAction.GetFCMToken -> getFCMToken()
            is MainAction.UpdateNotificationPermissionProperty -> updatePermissionProperty(action.permission)
            is MainAction.SetPushTokenInHubspotManager -> setPushToken(action.token)
            is MainAction.DeletePushTokenInHubspotManager -> deletePushToken(action.token)
            is MainAction.ClearData -> clearUserData()
        }
    }

    private fun clearUserData() {
        viewModelScope.launch {
            manager.logout()
            dataStore.clear()
        }
        dispatchEffect(MainEffect.TokenDeleted)
    }

    private fun setPushToken(token: String) {
        viewModelScope.launch {
            manager.setPushToken(token)
        }
    }

    private fun deletePushToken(token: String) {
        viewModelScope.launch {
            manager.deleteDeviceToken(token)
        }
        dispatchEffect(MainEffect.TokenDeleted)
    }

    private fun updatePermissionProperty(permission: Boolean) {
        val updateChatProperty = manager.getChatProperties()
        updateChatProperty[ChatPropertyKey.NotificationPermissions.chatPropertyValue] = permission.toString()
    }

    private fun getFCMToken() {
        viewModelScope.launch {
            val fcmKey = dataStore.getFcmKey()
            dispatchEffect(MainEffect.ShowFCMToken(fcmKey))
        }
    }

    private fun saveFCMToken(fcmKey: String) {
        viewModelScope.launch {
            dataStore.saveFCMKey(fcmKey)
        }
    }
}