package com.example.demo.bottomnavigation

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.demo.databinding.FragmentFabBinding
import com.hubspot.mobilesdk.HubspotWebActivity

// This class is used for showing the HubspotFloatingActionButton
// Click of HubspotFloatingActionButton opens the HubspotWebActivity directly

class FABFragment : Fragment() {
    private var _binding: FragmentFabBinding? = null
    private val binding get() = _binding!!
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFabBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
            binding.buttonFloating.setOnClickListener {
                startActivity(Intent(requireContext(), HubspotWebActivity::class.java))
        }
    }
}