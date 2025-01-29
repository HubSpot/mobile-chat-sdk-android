package com.example.demo.bottomnavigation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.demo.databinding.FragmentWebviewBinding
import com.hubspot.mobilesdk.HubspotManager
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

// This class is used for showing the HubspotWebView
// HubspotWebView show method opens the HubspotWebActivity directly

@AndroidEntryPoint
class WebviewFragment : Fragment() {

    private var _binding: FragmentWebviewBinding? = null
    private val binding get() = _binding!!
    @Inject
    lateinit var hubspotManager: HubspotManager

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentWebviewBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        with(binding.webview) {
            addChatScreenCloseListener {
                requireActivity().run { runOnUiThread { onBackPressedDispatcher.onBackPressed() } }
            }
            show()
        }
    }
}