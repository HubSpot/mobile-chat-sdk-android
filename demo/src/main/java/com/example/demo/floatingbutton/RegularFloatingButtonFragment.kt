package com.example.demo.floatingbutton

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.demo.databinding.FragmentRegularFloatingActionButtonBinding
import com.hubspot.mobilesdk.HubspotWebActivity

// This class is used for showing the Material FloatingActionButton - Normal
// Click of FloatingActionButton opens the HubspotWebActivity directly

class RegularFloatingButtonFragment : Fragment() {

    private var _binding: FragmentRegularFloatingActionButtonBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRegularFloatingActionButtonBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.buttonRegularFAB.setOnClickListener {
            startActivity(Intent(requireContext(), HubspotWebActivity::class.java))
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}