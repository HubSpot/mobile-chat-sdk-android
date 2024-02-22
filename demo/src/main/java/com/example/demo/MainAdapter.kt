package com.example.demo

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.demo.databinding.ListMainContentBinding

class MainAdapter(private var datalist: List<Content>, val onContentClicked: (Content) -> Unit) :
    RecyclerView.Adapter<MainAdapter.MainViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MainViewHolder {
        val binding = ListMainContentBinding
            .inflate(LayoutInflater.from(parent.context), parent, false)
        return MainViewHolder(binding)
    }


    override fun onBindViewHolder(holder: MainViewHolder, position: Int) {
        holder.bind(datalist[position])
    }

    override fun getItemCount(): Int {
        return datalist.size
    }

    inner class MainViewHolder(val binding: ListMainContentBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(content: Content) {
            binding.textViewMain.text = content.data
            binding.root.setOnClickListener { onContentClicked(content) }
        }
    }
}

data class Content(val data: String)
