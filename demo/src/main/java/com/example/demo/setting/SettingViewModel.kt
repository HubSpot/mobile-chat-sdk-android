package com.example.demo.setting

import androidx.lifecycle.viewModelScope
import com.example.demo.base.BaseViewModel
import com.example.demo.datastore.UserInfoDataStore
import com.example.demo.datastore.UserProfile
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingViewModel @Inject constructor(private val dataStore: UserInfoDataStore) : BaseViewModel<SettingAction>() {
    override fun processAction(action: SettingAction) {
        when (action) {
            is SettingAction.EmailTokenAvailable -> emailTokenAvailable()
            is SettingAction.ValidateSetting -> validateSettings(action.email, action.token)
        }
    }

    private fun emailTokenAvailable() {
        viewModelScope.launch {
            val profile = dataStore.getUserProfile()
            with(profile) {
                if (email.isNotEmpty() && token.isNotEmpty()) {
                    dispatchEffect(SettingEffect.EmailTokenAvailable(email, token))
                }
            }

        }
    }

    private fun validateSettings(email: String, token: String) {
        viewModelScope.launch {
            dataStore.saveUserProfile(UserProfile(email, token))
        }
        dispatchEffect(SettingEffect.Configure(email, token))
    }
}