package com.example.demo.base

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData

fun <T : Any?, L : LiveData<T>> LifecycleOwner.observe(liveData: L?, body: (T) -> Unit) {
    liveData?.observe(this) { it?.let(body) }
}