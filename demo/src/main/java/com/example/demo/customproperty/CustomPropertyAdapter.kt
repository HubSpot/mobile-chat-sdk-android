package com.example.demo.customproperty

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.demo.databinding.ListPropertyContentBinding

class CustomPropertyAdapter :
    ListAdapter<PropertyContent, CustomPropertyAdapter.CustomPropertyViewHolder>(PropertyDiffUtil()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CustomPropertyViewHolder {
        val binding = ListPropertyContentBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CustomPropertyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CustomPropertyViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class CustomPropertyViewHolder(val binding: ListPropertyContentBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(content: PropertyContent) {
            binding.textViewKey.text = content.key
            binding.textViewValue.text = content.value
        }
    }

    class PropertyDiffUtil : DiffUtil.ItemCallback<PropertyContent>() {
        override fun areItemsTheSame(oldItem: PropertyContent, newItem: PropertyContent): Boolean {
            return oldItem.key == newItem.key
        }

        override fun areContentsTheSame(oldItem: PropertyContent, newItem: PropertyContent): Boolean {
            return oldItem == newItem
        }

    }
}

data class PropertyContent(val key: String, val value: String)