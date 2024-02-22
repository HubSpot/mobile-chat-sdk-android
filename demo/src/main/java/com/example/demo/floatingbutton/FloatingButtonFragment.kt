package com.example.demo.floatingbutton

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.demo.Content
import com.example.demo.MainAdapter
import com.example.demo.R
import com.example.demo.databinding.FragmentFloatingButtonBinding
import com.example.demo.extention.navigateTo
import com.example.demo.util.DividerItemDecorator

// This class is used for showing the Multiple Floating Buttons with the list
// Click on the listitems open individual Floating action buttons

class FloatingButtonFragment : Fragment() {
    private var _binding: FragmentFloatingButtonBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFloatingButtonBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val contentList = listOf(
            Content(getString(R.string.regular_floating_button)),
            Content(getString(R.string.mini_floating_button)),
            Content(getString(R.string.extended_floating_button)),
        )
        binding.recyclerViewButton.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerViewButton.adapter = MainAdapter(contentList, ::onFloatingButtonClicked)
        binding.recyclerViewButton.addItemDecoration(
            DividerItemDecorator(ContextCompat.getDrawable(requireContext(), R.drawable.divider))
        )
    }

    private fun onFloatingButtonClicked(content: Content) {
        if (content.data.contentEquals(getString(R.string.regular_floating_button))) {
            navigateTo(FloatingButtonFragmentDirections.actionGoToRegularFab())
        } else if (content.data.contentEquals(getString(R.string.mini_floating_button))) {
            navigateTo(FloatingButtonFragmentDirections.actionGoToMiniFab())
        } else {
            navigateTo(FloatingButtonFragmentDirections.actionGoToExtendedFab())
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}