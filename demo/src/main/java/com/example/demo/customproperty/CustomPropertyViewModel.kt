package com.example.demo.customproperty

import com.example.demo.base.BaseViewModel
import com.hubspot.mobilesdk.HubspotManager
import dagger.hilt.android.lifecycle.HiltViewModel
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class CustomPropertyViewModel @Inject constructor(private val manager: HubspotManager) :
    BaseViewModel<CustomPropertyAction>() {

    override fun processAction(action: CustomPropertyAction) {
        when (action) {
            is CustomPropertyAction.AddProperty -> {
                if (addChatProperty(action.key, action.value)) {
                    dispatchEffect(CustomPropertyEffect.PropertyAdded(action.key, action.value))
                }
            }
        }
    }

    private fun addChatProperty(key: String, value: String): Boolean {
        return try {
            val updatedProperties = manager.getChatProperties().plus(Pair(key, value))
            manager.setChatProperties(updatedProperties)
            true
        } catch (e: Exception) {
            Timber.v("Couldn't add chat property : ${e.printStackTrace()}")
            false
        }
    }

}