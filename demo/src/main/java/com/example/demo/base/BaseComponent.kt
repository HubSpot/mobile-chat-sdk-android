package com.example.demo.base

open class Action
open class Effect
open class Loading
open class State
sealed class DefaultLoading : Loading() {
    data object Show : DefaultLoading()
    data object Hide : DefaultLoading()
}

interface StateHandler {
    fun updateEffect(effect: Effect)
    fun handleNavigation(effect: Effect)
    fun updateLoading(loading: Loading)
}