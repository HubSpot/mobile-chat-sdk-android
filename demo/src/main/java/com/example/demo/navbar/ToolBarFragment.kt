package com.example.demo.navbar

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.demo.MainActivity
import com.example.demo.R
import com.example.demo.databinding.FragmentCustomToolbarBinding
import com.example.demo.extention.navigateTo
import com.hubspot.mobilesdk.HubspotWebActivity

// This class is used for showing the Custom ToolBar Option
// Click of toolbar button open the HubspotWebActivity directly

class ToolBarFragment : Fragment() {
    private var _binding: FragmentCustomToolbarBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCustomToolbarBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.toolbar.setNavigationOnClickListener {
            navigateTo(ToolBarFragmentDirections.actionToolbarFragmentToMainFragment())
        }

        binding.toolbar.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.chat -> {
                    (activity as MainActivity?)?.supportActionBar?.hide()
                    requireContext().startActivity(
                        Intent(
                            requireContext(),
                            HubspotWebActivity::class.java
                        )
                    )
                    true
                }

                else -> false
            }
        }
    }

    override fun onResume() {
        super.onResume()
        (activity as MainActivity?)?.supportActionBar?.hide()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}