package com.cyh128.hikari_novel.ui.detail.user_bookshelf

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.cyh128.hikari_novel.data.model.SimpleNovelCover
import com.cyh128.hikari_novel.databinding.ItemSimpleNovelCoverBinding

class UserBookshelfListAdapter(
    val list: List<SimpleNovelCover>,
    val onItemClick: (aid: String) -> Unit
): RecyclerView.Adapter<UserBookshelfListAdapter.ViewHolder>() {
    inner class ViewHolder(val binding: ItemSimpleNovelCoverBinding): RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
        ViewHolder(
            ItemSimpleNovelCoverBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )

    override fun getItemCount(): Int = list.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = list[position]
        holder.binding.apply {
            tvISimpleNovelCover.text = item.title

            root.setOnClickListener {
                onItemClick(item.aid)
            }
        }
    }
}