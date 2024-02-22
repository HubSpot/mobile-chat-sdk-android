package com.example.demo.base

import androidx.annotation.CallSuper
import androidx.fragment.app.Fragment

open class BaseFragment(contentLayoutId: Int) : Fragment(contentLayoutId), StateHandler {

    @CallSuper
    override fun updateEffect(effect: Effect) {
        handleNavigation(effect)
    }

    @Suppress("EmptyFunctionBlock")
    @CallSuper
    override fun handleNavigation(effect: Effect) {
    }

    @Suppress("EmptyFunctionBlock")
    @CallSuper
    override fun updateLoading(loading: Loading) {
    }
}