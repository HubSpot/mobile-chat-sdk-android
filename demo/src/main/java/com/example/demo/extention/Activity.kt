/*************************************************
 * Activity.kt
 * Hubspot Mobile SDK
 *
 * Copyright (c) 2025 Hubspot, Inc.
 ************************************************/
package com.example.demo.extention

import android.app.Activity
import android.os.Build
import android.view.View
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import com.example.demo.R

fun Activity.applyEdgeToEdgeInsets() {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.VANILLA_ICE_CREAM) {
        val view = findViewById<View>(android.R.id.content)
        ViewCompat.setOnApplyWindowInsetsListener(view) { v, windowInsets ->
            //Retrieve the Insets for system bars, display cutouts and ime
            val bars = windowInsets.getInsets(
                WindowInsetsCompat.Type.systemBars() or
                        WindowInsetsCompat.Type.displayCutout() or
                        WindowInsetsCompat.Type.ime()
            )
            //Update the padding for the screen content with the retrieved insets
            v.updatePadding(
                left = bars.left,
                top = bars.top,
                right = bars.right,
                bottom = bars.bottom,
            )
            //Set background color for the decor view
            window.decorView.setBackgroundColor(ContextCompat.getColor(this, R.color.orange))
            windowInsets
        }
    }
}