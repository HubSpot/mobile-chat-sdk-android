package com.example.demo

import com.example.demo.base.Action
import com.example.demo.base.Effect

sealed class MainAction : Action() {
    data class SaveFCMToken(val token: String) : MainAction()
    data class SetPushTokenInHubspotManager(val token: String) : MainAction()
    data object GetFCMToken : MainAction()
    data object ClearData : MainAction()
    data class UpdateNotificationPermissionProperty(val permission: Boolean) : MainAction()
    data class DeletePushTokenInHubspotManager(val token: String) : MainAction()
}

sealed class MainEffect : Effect() {
    data class ShowFCMToken(val fcmToken: String?) : MainEffect()
    data object TokenDeleted : MainEffect()
}