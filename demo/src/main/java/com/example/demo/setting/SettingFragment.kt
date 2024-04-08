package com.example.demo.setting

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.viewModels
import com.example.demo.R
import com.example.demo.base.BaseFragment
import com.example.demo.base.Effect
import com.example.demo.base.observe
import com.example.demo.base.viewBinding
import com.example.demo.databinding.FragmentSettingBinding
import com.hubspot.mobilesdk.HubspotManager
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class SettingFragment : BaseFragment(R.layout.fragment_setting) {

    private val viewModel by viewModels<SettingViewModel>()
    private val binding by viewBinding(FragmentSettingBinding::bind)

    @Inject
    lateinit var hubspotManager: HubspotManager

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupUI()
        setUpObservers()

        viewModel.processAction(SettingAction.EmailTokenAvailable)

        with(binding) {
            buttonConfigure.setOnClickListener {
                viewModel.processAction(
                    SettingAction.ValidateSetting(
                        editTextEmail.text.toString(),
                        editTextToken.text.toString()
                    )
                )
            }
        }
    }

    private fun setupUI() {
        binding.toolbar.setNavigationOnClickListener {
            activity?.supportFragmentManager?.popBackStack()
        }
        binding.buttonConfigure.setOnClickListener {
            viewModel.processAction(
                SettingAction.ValidateSetting(
                    binding.editTextEmail.text.toString(),
                    binding.editTextToken.text.toString()
                )
            )
        }
    }

    private fun setUpObservers() {
        viewLifecycleOwner.observe(viewModel.effect, ::updateEffect)
    }


    override fun updateEffect(effect: Effect) {
        super.updateEffect(effect)
        when (effect) {
            is SettingEffect.EmailTokenAvailable -> {
                with(binding) {
                    editTextEmail.setText(effect.email)
                    editTextToken.setText(effect.token)
                }
            }

            is SettingEffect.Configure -> {
                hubspotManager.setUserIdentity(effect.email, effect.token)
                Toast.makeText(requireContext(), getString(R.string.email_token_configured), Toast.LENGTH_SHORT).show()
            }
        }
    }

}