package com.example.demo.customchatflow

import android.content.Intent
import android.os.Bundle
import android.view.View
import com.example.demo.R
import com.example.demo.base.BaseFragment
import com.example.demo.base.viewBinding
import com.example.demo.databinding.FragmentCustomChatFlowBinding
import com.hubspot.mobilesdk.HubspotWebActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CustomChatFlowFragment : BaseFragment(R.layout.fragment_custom_chat_flow) {
    private val binding by viewBinding(FragmentCustomChatFlowBinding::bind)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        with(binding) {
            toolbar.setNavigationOnClickListener {
                activity?.supportFragmentManager?.popBackStack()
            }
            buttonDefaultChatFlowId.setOnClickListener {
                startActivity(Intent(requireContext(), HubspotWebActivity::class.java))
            }
            buttonChatFlowId.setOnClickListener {
                val intent = Intent(requireContext(), HubspotWebActivity::class.java)
                intent.putExtra(HubspotWebActivity.CHAT_FLOW_KEY, CHAT_FLOW_ID_VALUE)
                startActivity(intent)
            }
            buttonCustomChatFlowId.setOnClickListener {
                val intent = Intent(requireContext(), HubspotWebActivity::class.java)
                intent.putExtra(HubspotWebActivity.CHAT_FLOW_KEY, binding.editTextChatFlowId.text.toString())
                startActivity(intent)
            }
        }
    }

    companion object {
        const val CHAT_FLOW_ID_VALUE = "mobileflow2"
    }
}