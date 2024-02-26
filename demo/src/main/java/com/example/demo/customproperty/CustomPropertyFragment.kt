package com.example.demo.customproperty

import android.content.Context
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.demo.R
import com.example.demo.base.BaseFragment
import com.example.demo.base.Effect
import com.example.demo.base.observe
import com.example.demo.base.viewBinding
import com.example.demo.databinding.FragmentCustomPropertiesBinding
import com.example.demo.util.DividerItemDecorator
import com.hubspot.mobilesdk.HubspotManager
import com.hubspot.mobilesdk.metadata.ChatPropertyKey
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject


@AndroidEntryPoint
class CustomPropertyFragment : BaseFragment(R.layout.fragment_custom_properties) {

    private val binding by viewBinding(FragmentCustomPropertiesBinding::bind)
    private val viewModel by viewModels<CustomPropertyViewModel>()

    @Inject
    lateinit var hubspotManager: HubspotManager

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpObservers()
        val listOfDefaultKeys = listOf(
            ChatPropertyKey.CameraPermissions.chatPropertyValue,
            ChatPropertyKey.PhotoPermissions.chatPropertyValue,
            ChatPropertyKey.NotificationPermissions.chatPropertyValue,
            ChatPropertyKey.LocationPermissions.chatPropertyValue,
        )
        val listItems = hubspotManager.getChatProperties()
            .filter {
                !listOfDefaultKeys.contains(it.key)
            }.map {
                PropertyContent(it.key, it.value)
            }

        with(binding.recyclerViewCustomProperties) {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = CustomPropertyAdapter()
            addItemDecoration(
                DividerItemDecorator(
                    ContextCompat.getDrawable(
                        requireContext(),
                        R.drawable.divider
                    )
                )
            )
            (adapter as CustomPropertyAdapter).submitList(listItems)
        }
        with(binding) {
            toolbar.setNavigationOnClickListener {
                activity?.supportFragmentManager?.popBackStack()
            }
            buttonChatProperty.setOnClickListener {
                val key = editTextKey.text.toString()
                val value = editTextValue.text.toString()
                if (key.isNotEmpty()) {
                    viewModel.processAction(CustomPropertyAction.AddProperty(key, value))
                }
            }
        }
    }

    private fun setUpObservers() {
        viewLifecycleOwner.observe(viewModel.effect, ::updateEffect)
    }

    override fun updateEffect(effect: Effect) {
        super.updateEffect(effect)
        when (effect) {
            is CustomPropertyEffect.PropertyAdded -> {
                val propertyAdapter = binding.recyclerViewCustomProperties.adapter as CustomPropertyAdapter
                propertyAdapter.submitList(propertyAdapter.currentList.plus(PropertyContent(effect.key, effect.value)))
                with(binding) {
                    editTextKey.setText("")
                    editTextValue.setText("")
                    editTextValue.clearFocus()
                    editTextKey.clearFocus()
                    val imm = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                    imm.hideSoftInputFromWindow(binding.buttonChatProperty.applicationWindowToken, 0)
                }
            }
        }
    }
}