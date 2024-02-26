package com.example.demo.extention

import androidx.fragment.app.Fragment
import androidx.navigation.NavDirections
import androidx.navigation.fragment.findNavController
import timber.log.Timber

fun Fragment.navigateTo(action: NavDirections) {
    try {
        findNavController().navigate(action)
    } catch (ex: Exception) {
        Timber.w(ex, "Navigation action/destination not found")
    }
}