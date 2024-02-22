package com.example.demo.customproperty

import com.example.demo.base.Action
import com.example.demo.base.Effect

sealed class CustomPropertyAction: Action() {
    data class AddProperty(val key: String, val value: String) : CustomPropertyAction()
}

sealed class CustomPropertyEffect: Effect() {
    data class PropertyAdded(val key: String, val value: String) : CustomPropertyEffect()
}