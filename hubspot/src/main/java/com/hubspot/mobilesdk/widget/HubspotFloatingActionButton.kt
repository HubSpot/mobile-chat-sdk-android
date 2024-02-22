/*************************************************
 * HubspotFloatingActionButton.kt
 * Hubspot Mobile SDK
 *
 * Copyright (c) 2024 Hubspot, Inc.
 ************************************************/
package com.hubspot.mobilesdk.widget

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.util.AttributeSet
import androidx.core.content.ContextCompat
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.hubspot.mobilesdk.R
import com.hubspot.mobilesdk.HubspotWebActivity

/**
 * HubspotFloatingActionButton class is used for setting an image for the FloatingButton
 * It also handle the clicks which opens the HubspotWebActivity directly
 */
class HubspotFloatingActionButton @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
) : FloatingActionButton(context, attrs, defStyleAttr) {

    init {
        setImageDrawable(
            ContextCompat.getDrawable(
                context,
                R.drawable.ic_chat
            )
        )
        setOnClickListener {
            context.startActivity(Intent(context as Activity, HubspotWebActivity::class.java))
        }
    }
}
