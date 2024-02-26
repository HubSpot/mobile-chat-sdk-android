package com.example.demo.floatingbutton

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.demo.databinding.FragmentExtendedFloatingActionButtonBinding
import com.hubspot.mobilesdk.HubspotWebActivity

// This class is used for showing the Material ExtendedFloatingActionButton
// Click of ExtendedFloatingActionButton opens the HubspotWebActivity directly

class ExtendedFloatingButtonFragment : Fragment() {

    private var _binding: FragmentExtendedFloatingActionButtonBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentExtendedFloatingActionButtonBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.extendedFab.setOnClickListener {
            startActivity(Intent(requireContext(), HubspotWebActivity::class.java))
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}