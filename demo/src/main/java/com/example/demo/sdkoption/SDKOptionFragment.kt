/*************************************************
 * SDKOptionFragment.kt
 * Hubspot Mobile SDK
 *
 * Copyright (c) 2024 Hubspot, Inc.
 ************************************************/
package com.example.demo.sdkoption

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import com.example.demo.R
import com.example.demo.base.BaseFragment
import com.example.demo.base.Effect
import com.example.demo.base.observe
import com.example.demo.base.viewBinding
import com.example.demo.databinding.FragmentSdkOptionBinding
import com.example.demo.extention.navigateTo
import com.hubspot.mobilesdk.HubspotManager
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class SDKOptionFragment : BaseFragment(R.layout.fragment_sdk_option) {
    private val binding by viewBinding(FragmentSdkOptionBinding::bind)
    private val viewModel by viewModels<SDKOptionViewModel>()

    @Inject
    lateinit var hubspotManager: HubspotManager

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        with(binding) {
            toolbar.setNavigationOnClickListener {
                activity?.supportFragmentManager?.popBackStack()
            }

            textViewPortalIDValue.text = hubspotManager.getPortalId()
            textViewHubletValue.text = hubspotManager.getHublet()
            textViewEnvValue.text = hubspotManager.getEnvironment()
            textViewChatFlowValue.text = hubspotManager.getDefaultChatFlow()

            textViewConfigure.setOnClickListener {
                navigateTo(SDKOptionFragmentDirections.actionGoToSetting())
            }
            textViewClearConfig.setOnClickListener {
                viewModel.processAction(SDKOptionAction.ClearData)
            }
        }

        viewModel.processAction(SDKOptionAction.EmailTokenAvailable)
        setUpObservers()
    }

    private fun setUpObservers() {
        viewLifecycleOwner.observe(viewModel.effect, ::updateEffect)
    }

    override fun updateEffect(effect: Effect) {
        super.updateEffect(effect)
        when (effect) {
            is SDKOptionEffect.EmailTokenAvailable -> {
                with(binding) {
                    groupEmailToken.isVisible = true
                    groupNotSet.isVisible = false
                    textViewEmailValue.text = effect.email
                    textViewTokenValue.text = effect.token
                }
            }

            is SDKOptionEffect.ClearData -> {
                with(binding) {
                    groupEmailToken.isVisible = false
                    groupNotSet.isVisible = true
                }

            }
        }
    }

}