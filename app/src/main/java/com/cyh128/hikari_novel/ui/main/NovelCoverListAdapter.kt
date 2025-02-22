package com.cyh128.hikari_novel.ui.main

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
    private val list: List<NovelCover>,
    private val onItemClick: (aid: String) -> Unit
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
    private var list: List<BookshelfNovelInfo>,
    private val onItemClick: (aid: String) -> Unit,
    private val onMultiSelectModeChange: (Boolean) -> Unit = {} //多选模式状态变化回调
) : RecyclerView.Adapter<BookshelfListAdapter.ViewHolder>() {

    private var isMultiSelectMode = false

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

            if (isMultiSelectMode) root.isChecked = item.isSelected
            else root.isChecked = false

            root.setOnClickListener {
                if (isMultiSelectMode) {
                    item.isSelected = !item.isSelected
                    root.isChecked = item.isSelected
                } else {
                    onItemClick(item.aid)
                }
            }

            root.setOnLongClickListener {
                if (!isMultiSelectMode) {
                    isMultiSelectMode = true
                    item.isSelected = true
                    root.isChecked = true
                    onMultiSelectModeChange(true)
                    notifyDataSetChanged()
                }
                true
            }
        }
    }

    //全选或取消全选
    fun setSelectAll(select: Boolean) {
        list.forEach { it.isSelected = select }
        notifyDataSetChanged()
    }

    //进入或退出多选模式
    fun setMultiSelectMode(enabled: Boolean) {
        isMultiSelectMode = enabled
        if (!enabled) {
            //退出多选模式时，清空所有选中状态
            list.forEach { it.isSelected = false }
        }
        notifyDataSetChanged()
        onMultiSelectModeChange(enabled)
    }

    fun getSelectedList(): List<BookshelfNovelInfo> = list.filter { it.isSelected }

    fun getMultiSelectMode() = isMultiSelectMode
}

class HomeBlockAdapter(
    private val data: HomeBlock,
    private val onItemClick: (aid: String) -> Unit
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