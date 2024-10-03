package com.cyh128.hikarinovel.ui.view.main.main.home.search

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.cyh128.hikarinovel.databinding.ItemSearchHistoryBinding

class SearchHistoryChipAdapter(
    val list: MutableList<String>,
    val onItemClick: (text: String) -> Unit
) : RecyclerView.Adapter<SearchHistoryChipAdapter.ViewHolder>() {

    inner class ViewHolder(
        val context: Context,
        val binding: ItemSearchHistoryBinding
    ) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
        ViewHolder(
            parent.context,
            ItemSearchHistoryBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )

    override fun getItemCount(): Int = list.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val tempText = list[position]
        holder.binding.cISearchHistory.apply {
            text = tempText
            setOnClickListener {
                onItemClick(tempText)
            }
        }
    }
}