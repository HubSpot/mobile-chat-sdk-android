/*************************************************
 * SDKOptionViewModel.kt
 * Hubspot Mobile SDK
 *
 * Copyright (c) 2024 Hubspot, Inc.
 ************************************************/
package com.example.demo.sdkoption

import androidx.lifecycle.viewModelScope
import com.example.demo.base.BaseViewModel
import com.example.demo.datastore.UserInfoDataStore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SDKOptionViewModel @Inject constructor(private val dataStore: UserInfoDataStore) : BaseViewModel<SDKOptionAction>() {
    override fun processAction(action: SDKOptionAction) {
        when (action) {
            is SDKOptionAction.EmailTokenAvailable -> emailTokenAvailable()
            is SDKOptionAction.ClearData -> clearData()
        }
    }

    private fun clearData() {
        viewModelScope.launch {
            dataStore.clear()
            dispatchEffect(SDKOptionEffect.ClearData)
        }
    }

    private fun emailTokenAvailable() {
        viewModelScope.launch {
            val profile = dataStore.getUserProfile()
            with(profile) {
                if (email.isNotEmpty() && token.isNotEmpty()) {
                    dispatchEffect(SDKOptionEffect.EmailTokenAvailable(email, token))
                }
            }

        }
    }
}