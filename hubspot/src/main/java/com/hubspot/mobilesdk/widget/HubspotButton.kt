/*************************************************
 * HubspotButton.kt
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
import com.google.android.material.button.MaterialButton
import com.hubspot.mobilesdk.HubspotWebActivity
import com.hubspot.mobilesdk.R

/**
 * HubspotButton class is used for setting a localise text
 * It also sets center alignment of the text, sets the background cor and text color
 * While handle the click for the HubspotButton, it opens the HubspotWebActivity directly
 */
class HubspotButton @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
) : MaterialButton(context, attrs, defStyleAttr) {

    init {
        text = context.getText(R.string.start_chat)
        textAlignment = TEXT_ALIGNMENT_CENTER
        setBackgroundColor(ContextCompat.getColor(context, R.color.orange))
        setTextColor(ContextCompat.getColor(context, R.color.white))
        setOnClickListener {
            context.startActivity(Intent(context as Activity, HubspotWebActivity::class.java))
        }
    }
}