package com.cyh128.hikari_novel.ui.main

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.cyh128.hikari_novel.data.model.BookshelfNovelInfo
import com.cyh128.hikari_novel.data.model.HomeBlock
import com.cyh128.hikari_novel.data.model.ListViewType
import com.cyh128.hikari_novel.data.model.NovelCover
import com.cyh128.hikari_novel.databinding.ItemNovelCoverGridBinding
import com.cyh128.hikari_novel.databinding.ItemNovelCoverLinerBinding

class NovelCoverListAdapter(
    private val list: List<NovelCover>,
    private val listViewType: ListViewType,
    private val onItemClick: (aid: String) -> Unit
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    inner class ItemNovelCoverLinerViewHolder(val binding: ItemNovelCoverLinerBinding) : RecyclerView.ViewHolder(binding.root)
    inner class ItemNovelCoverGridViewHolder(val binding: ItemNovelCoverGridBinding) : RecyclerView.ViewHolder(binding.root)

    override fun getItemViewType(position: Int) = listViewType.ordinal

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == ListViewType.Linear.ordinal) {
            ItemNovelCoverLinerViewHolder(
                ItemNovelCoverLinerBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            )
        } else {
            ItemNovelCoverGridViewHolder(
                ItemNovelCoverGridBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            )
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = list[position]

        if (holder is ItemNovelCoverLinerViewHolder) {
            holder.binding.apply {
                Glide.with(ivINovelCoverLiner)
                    .load(item.img)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(ivINovelCoverLiner)

                tvINovelCoverLiner.text = item.title
                root.setOnClickListener {
                    onItemClick(item.aid)
                }
            }
        } else if (holder is ItemNovelCoverGridViewHolder) {
            holder.binding.apply {
                Glide.with(ivINovelCoverGrid)
                    .load(item.img)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(ivINovelCoverGrid)

                tvINovelCoverGrid.text = item.title
                root.setOnClickListener {
                    onItemClick(item.aid)
                }
            }
        }
    }

    override fun getItemCount(): Int = list.size
}

class BookshelfListAdapter(
    private var list: List<BookshelfNovelInfo>,
    private val listViewType: ListViewType,
    private val onItemClick: (aid: String) -> Unit,
    private val onSelected: (count: Int) -> Unit,
    private val onMultiSelectModeChange: (Boolean) -> Unit = {} //多选模式状态变化回调
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var isMultiSelectMode = false

    inner class ItemNovelCoverLinerViewHolder(val binding: ItemNovelCoverLinerBinding) : RecyclerView.ViewHolder(binding.root)
    inner class ItemNovelCoverGridViewHolder(val binding: ItemNovelCoverGridBinding) : RecyclerView.ViewHolder(binding.root)

    override fun getItemViewType(position: Int) = listViewType.ordinal

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == ListViewType.Linear.ordinal) {
            ItemNovelCoverLinerViewHolder(
                ItemNovelCoverLinerBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            )
        } else {
            ItemNovelCoverGridViewHolder(
                ItemNovelCoverGridBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            )
        }
    }

    override fun getItemCount(): Int = list.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = list[position]

        if (holder is ItemNovelCoverLinerViewHolder) {
            holder.binding.apply {
                Glide.with(ivINovelCoverLiner)
                    .load(item.img)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(ivINovelCoverLiner)

                tvINovelCoverLiner.text = item.title

                if (isMultiSelectMode) {
                    setChecked(item.isSelected, this)
                    onSelected(getSelectedList().size)
                }
                else setChecked(false, this)

                root.setOnClickListener {
                    if (isMultiSelectMode) {
                        item.isSelected = !item.isSelected
                        setChecked(item.isSelected, this)
                        onSelected(getSelectedList().size)
                    } else {
                        onItemClick(item.aid)
                    }
                }

                root.setOnLongClickListener {
                    if (!isMultiSelectMode) {
                        item.isSelected = true
                        setChecked(true, this)
                        setMultiSelectMode(true)
                        onSelected(getSelectedList().size)
                    }
                    true
                }
            }
        } else if (holder is ItemNovelCoverGridViewHolder) {
            holder.binding.apply {
                Glide.with(ivINovelCoverGrid)
                    .load(item.img)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(ivINovelCoverGrid)

                tvINovelCoverGrid.text = item.title

                if (isMultiSelectMode) {
                    setChecked(item.isSelected, this)
                    onSelected(getSelectedList().size)
                }
                else setChecked(false, this)

                root.setOnClickListener {
                    if (isMultiSelectMode) {
                        item.isSelected = !item.isSelected
                        setChecked(item.isSelected, this)
                        onSelected(getSelectedList().size)
                    } else {
                        onItemClick(item.aid)
                    }
                }

                root.setOnLongClickListener {
                    if (!isMultiSelectMode) {
                        item.isSelected = true
                        setChecked(true, this)
                        setMultiSelectMode(true)
                        onSelected(getSelectedList().size)
                    }
                    true
                }
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

    private fun setChecked(value: Boolean, binding: ViewBinding) {
        if (listViewType == ListViewType.Linear) {
            binding as ItemNovelCoverLinerBinding

            if (value) {
                binding.ivINovelCoverLinerChecked.visibility = View.VISIBLE
                binding.llINovelCoverLinerChecked.visibility = View.VISIBLE
            } else {
                binding.ivINovelCoverLinerChecked.visibility = View.INVISIBLE
                binding.llINovelCoverLinerChecked.visibility = View.INVISIBLE
            }
        } else {
            binding as ItemNovelCoverGridBinding

            if (value) {
                binding.ivINovelCoverGridChecked.visibility = View.VISIBLE
                binding.llINovelCoverGridChecked.visibility = View.VISIBLE
            } else {
                binding.ivINovelCoverGridChecked.visibility = View.INVISIBLE
                binding.llINovelCoverGridChecked.visibility = View.INVISIBLE
            }
        }
    }
}

class HomeBlockAdapter(
    private val data: HomeBlock,
    private val listViewType: ListViewType,
    private val onItemClick: (aid: String) -> Unit
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    inner class ItemNovelCoverLinerViewHolder(val binding: ItemNovelCoverLinerBinding) : RecyclerView.ViewHolder(binding.root)
    inner class ItemNovelCoverGridViewHolder(val binding: ItemNovelCoverGridBinding) : RecyclerView.ViewHolder(binding.root)

    override fun getItemViewType(position: Int) = listViewType.ordinal

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == ListViewType.Linear.ordinal) {
            ItemNovelCoverLinerViewHolder(
                ItemNovelCoverLinerBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            )
        } else {
            ItemNovelCoverGridViewHolder(
                ItemNovelCoverGridBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            )
        }
    }

    override fun getItemCount(): Int = data.list.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = data.list[position]

        if (holder is ItemNovelCoverLinerViewHolder) {
            holder.binding.apply {
                Glide.with(ivINovelCoverLiner)
                    .load(item.img)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(ivINovelCoverLiner)

                tvINovelCoverLiner.text = item.title
                root.setOnClickListener {
                    onItemClick(item.aid)
                }
            }
        } else if (holder is ItemNovelCoverGridViewHolder) {
            holder.binding.apply {
                Glide.with(ivINovelCoverGrid)
                    .load(item.img)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(ivINovelCoverGrid)

                tvINovelCoverGrid.text = item.title
                root.setOnClickListener {
                    onItemClick(item.aid)
                }
            }
        }
    }
}