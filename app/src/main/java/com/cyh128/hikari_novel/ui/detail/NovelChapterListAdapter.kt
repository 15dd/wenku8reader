package com.cyh128.hikari_novel.ui.detail

import android.animation.ObjectAnimator
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.cyh128.hikari_novel.data.model.Novel
import com.cyh128.hikari_novel.databinding.ItemChapterCcssBinding
import com.cyh128.hikari_novel.databinding.ItemChapterVcssBinding
import pokercc.android.expandablerecyclerview.ExpandableAdapter

class NovelChapterListAdapter(
    private val novel: Novel,
    private val onItemClick: (volume: Int, chapter: Int) -> Unit,
    private val onLongClick: (cid: String) -> Unit,
    private val onGroupItemChangeListener: (group: Int, binding: ItemChapterVcssBinding) -> Unit,
    private val onChildItemChangeListener: (group: Int, child: Int, binding: ItemChapterCcssBinding) -> Unit
) : ExpandableAdapter<ExpandableAdapter.ViewHolder>() {
    internal class GroupViewHolder(val binding: ItemChapterVcssBinding) : ViewHolder(binding.root)

    internal class ChildViewHolder(val binding: ItemChapterCcssBinding) : ViewHolder(binding.root)

    override fun getChildCount(groupPosition: Int): Int = novel.volume[groupPosition].chapters.size

    override fun getGroupCount(): Int = novel.volume.size

    override fun onCreateChildViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        return LayoutInflater.from(viewGroup.context)
            .let { ItemChapterCcssBinding.inflate(it, viewGroup, false) }
            .let(NovelChapterListAdapter::ChildViewHolder)
    }

    override fun onCreateGroupViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        return LayoutInflater.from(viewGroup.context)
            .let { ItemChapterVcssBinding.inflate(it, viewGroup, false) }
            .let(NovelChapterListAdapter::GroupViewHolder)
    }

    override fun onGroupViewHolderExpandChange(
        holder: ViewHolder,
        groupPosition: Int,
        animDuration: Long,
        expand: Boolean
    ) {
        if (expand) {
            ObjectAnimator.ofFloat(
                (holder as GroupViewHolder).binding.ivIChapterVcss,
                View.ROTATION,
                180f
            ).setDuration(100)
                .start()
        } else {
            ObjectAnimator.ofFloat(
                (holder as GroupViewHolder).binding.ivIChapterVcss,
                View.ROTATION,
                0f
            ).setDuration(100)
                .start()
        }
    }


    override fun onBindGroupViewHolder(
        holder: ViewHolder,
        groupPosition: Int,
        expand: Boolean,
        payloads: List<Any>
    ) {
        holder as GroupViewHolder
        holder.binding.tvIChapterVcss.text = novel.volume[groupPosition].volumeTitle

        if (expand) {
            ObjectAnimator.ofFloat(holder.binding.ivIChapterVcss, View.ROTATION, 180f)
                .setDuration(100)
                .start()
        } else {
            ObjectAnimator.ofFloat(holder.binding.ivIChapterVcss, View.ROTATION, 0f)
                .setDuration(100)
                .start()
        }

        onGroupItemChangeListener(groupPosition, holder.binding)
    }

    override fun onBindChildViewHolder(
        holder: ViewHolder,
        groupPosition: Int,
        childPosition: Int,
        payloads: List<Any>
    ) {
        holder as ChildViewHolder
        holder.binding.tvIChapterCcss.text =
            novel.volume[groupPosition].chapters[childPosition].chapterTitle
        holder.binding.root.setOnClickListener {
            onItemClick(groupPosition, childPosition)
        }
        holder.binding.root.setOnLongClickListener {
            onLongClick(
                novel.volume[groupPosition].chapters[childPosition].cid
            )
            true
        }

        onChildItemChangeListener(groupPosition ,childPosition, holder.binding)
    }
}