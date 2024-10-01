package com.cyh128.hikarinovel.ui.view.detail

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.cyh128.hikarinovel.databinding.ItemTagBinding

class TagChipAdapter(
    val list: List<String>
): RecyclerView.Adapter<TagChipAdapter.ViewHolder>() {
    inner class ViewHolder(val binding: ItemTagBinding): RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
        ViewHolder(ItemTagBinding.inflate(LayoutInflater.from(parent.context),parent,false))

    override fun getItemCount(): Int = list.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.binding.cITag.apply {
            text = list[position]
        }
    }
}