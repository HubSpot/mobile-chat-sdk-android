package com.example.demo.base

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

abstract class BaseViewModel<T : Action> : ViewModel() {
    abstract fun processAction(action: T)
    private val _state by lazy { MutableLiveData<State>() }
    val state: LiveData<State> get() = _state

    private val _effect by lazy { SingleLiveEvent<Effect>() }
    val effect: LiveData<Effect> get() = _effect

    private val _loading by lazy { MutableLiveData<Loading>() }
    val loading: LiveData<Loading> get() = _loading

    protected fun dispatchEffect(effect: Effect) {
        _effect.value = effect
    }

    protected fun dispatchLoading(loading: Loading) {
        _loading.value = loading
    }
    protected fun dispatchState(state: State) {
        _state.value = state
    }
    protected fun clearState() {
        _state.value = null
    }
}