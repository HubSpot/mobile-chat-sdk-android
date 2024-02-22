package com.example.demo.setting

import com.example.demo.base.Action
import com.example.demo.base.Effect

sealed class SettingAction : Action() {
    data class ValidateSetting(val email: String, val token: String) : SettingAction()
    data object EmailTokenAvailable : SettingAction()
}

sealed class SettingEffect : Effect() {
    data class EmailTokenAvailable(val email: String, val token: String) : SettingEffect()
    data class Configure(val email: String, val token: String) : SettingEffect()
}