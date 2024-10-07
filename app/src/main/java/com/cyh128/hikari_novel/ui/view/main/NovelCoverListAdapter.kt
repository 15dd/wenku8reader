package com.cyh128.hikari_novel.ui.view.main

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.cyh128.hikari_novel.data.model.BookshelfNovelInfo
import com.cyh128.hikari_novel.data.model.HomeBlock
import com.cyh128.hikari_novel.data.model.NovelCover
import com.cyh128.hikari_novel.databinding.ItemNovelCoverBinding

class NovelCoverListAdapter(
    val list: List<NovelCover>,
    val onItemClick: (aid: String) -> Unit
) : RecyclerView.Adapter<NovelCoverListAdapter.ViewHolder>() {
    inner class ViewHolder(
        val binding: ItemNovelCoverBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemNovelCoverBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = list[position]
        holder.binding.apply {
            Glide.with(ivINovelCover)
                .load(item.img)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(ivINovelCover)

            tvINovelCover.text = item.title
            root.setOnClickListener {
                onItemClick(item.aid)
            }
        }
    }

    override fun getItemCount(): Int = list.size
}

class BookshelfListAdapter(
    var list: List<BookshelfNovelInfo>,
    val onItemClick: (aid: String) -> Unit
) : RecyclerView.Adapter<BookshelfListAdapter.ViewHolder>() {

    inner class ViewHolder(val binding: ItemNovelCoverBinding): RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemNovelCoverBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int = list.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = list[position]
        holder.binding.apply {
            Glide.with(ivINovelCover)
                .load(item.img)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(ivINovelCover)

            tvINovelCover.text = item.title
            root.setOnClickListener {
                onItemClick(item.aid)
            }
        }
    }
}

class HomeBlockAdapter(
    val data: HomeBlock,
    val onItemClick: (aid: String) -> Unit
) : RecyclerView.Adapter<HomeBlockAdapter.ViewHolder>() {
    inner class ViewHolder(
        val binding: ItemNovelCoverBinding
    ) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemNovelCoverBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }


    override fun getItemCount(): Int = data.list.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = data.list[position]
        holder.binding.apply {
            Glide.with(ivINovelCover)
                .load(item.img)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(ivINovelCover)

            tvINovelCover.text = item.title
            root.setOnClickListener {
                onItemClick(item.aid)
            }
        }
    }
}