package com.cyh128.hikari_novel.ui.view.detail

import android.animation.ObjectAnimator
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.cyh128.hikari_novel.R
import com.cyh128.hikari_novel.data.model.Novel
import com.cyh128.hikari_novel.databinding.ItemChapterCcssBinding
import com.cyh128.hikari_novel.databinding.ItemChapterVcssBinding
import com.cyh128.hikari_novel.util.ResourceUtil
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import pokercc.android.expandablerecyclerview.ExpandableAdapter

class NovelChapterListAdapter(
    private var viewModel: NovelInfoViewModel,
    private val novel: Novel,
    private val onItemClick: (volume: Int, chapter: Int) -> Unit,
    private val onLongClick: (cid: String) -> Unit
) : ExpandableAdapter<ExpandableAdapter.ViewHolder>() {
    internal class GroupViewHolder(val binding: ItemChapterVcssBinding) : ViewHolder(binding.root) {
        val scope = CoroutineScope(Dispatchers.Main + SupervisorJob())

        fun clean() {
            scope.cancel() // 取消所有协程
        }
    }

    internal class ChildViewHolder(val binding: ItemChapterCcssBinding) : ViewHolder(binding.root) {
        val scope = CoroutineScope(Dispatchers.Main + SupervisorJob())

        fun clean() {
            scope.cancel() // 取消所有协程
        }
    }

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

        holder.scope.launch(Dispatchers.IO) {
            viewModel.getReadHistoryByVolume(groupPosition).collect {
                withContext(Dispatchers.Main) {
                    if (it.isNullOrEmpty()) {
                        holder.binding.tvIChapterVcssCompleted.text = ResourceUtil.getString(R.string.unread)
                        holder.binding.tvIChapterVcss.isEnabled = true
                        holder.binding.tvIChapterVcssCompleted.isEnabled = true
                    } else if (it.size == novel.volume[groupPosition].chapters.size) {
                        var isAllRead = false
                        it.forEach { entity ->
                            isAllRead = entity.progressPercent == 100
                        }
                        if (isAllRead) {
                            holder.binding.tvIChapterVcssCompleted.text = ResourceUtil.getString(R.string.completed_reading)
                            holder.binding.tvIChapterVcss.isEnabled = false
                            holder.binding.tvIChapterVcssCompleted.isEnabled = false
                        } else {
                            holder.binding.tvIChapterVcssCompleted.text = ResourceUtil.getString(R.string.partly_completed_reading)
                            holder.binding.tvIChapterVcss.isEnabled = true
                            holder.binding.tvIChapterVcssCompleted.isEnabled = true
                        }
                    } else {
                        holder.binding.tvIChapterVcssCompleted.text = ResourceUtil.getString(R.string.partly_completed_reading)
                        holder.binding.tvIChapterVcss.isEnabled = true
                        holder.binding.tvIChapterVcssCompleted.isEnabled = true
                    }
                }
            }
        }
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

        holder.scope.launch(Dispatchers.IO) {
            viewModel.getReadHistoryByCid(novel.volume[groupPosition].chapters[childPosition].cid).collect {
                withContext(Dispatchers.Main) {
                    if (it == null) {
                        holder.binding.tvIChapterCcssCompleted.text = ResourceUtil.getString(R.string.unread)
                        holder.binding.tvIChapterCcss.isEnabled = true
                        holder.binding.tvIChapterCcssCompleted.isEnabled = true

                        holder.binding.tvIChapterCcssLatest.text = null
                        return@withContext
                    } else if (it.progressPercent == 100) {
                        holder.binding.tvIChapterCcssCompleted.text = ResourceUtil.getString(R.string.completed_reading)
                        holder.binding.tvIChapterCcss.isEnabled = false
                        holder.binding.tvIChapterCcssCompleted.isEnabled = false
                    } else if (it.progressPercent != 100) {
                        holder.binding.tvIChapterCcssCompleted.text = "${it.progressPercent}%"
                        holder.binding.tvIChapterCcss.isEnabled = true
                        holder.binding.tvIChapterCcssCompleted.isEnabled = true
                    }

                    if (it.isLatest) holder.binding.tvIChapterCcssLatest.text = ResourceUtil.getString(R.string.last_read)
                    else holder.binding.tvIChapterCcssLatest.text = null
                }
            }
        }
    }

    override fun onViewDetachedFromWindow(holder: ViewHolder) {
        //终止协程，防止内存泄漏
        when (holder) {
            is GroupViewHolder -> holder.clean()
            is ChildViewHolder -> holder.clean()
        }
        super.onViewDetachedFromWindow(holder)
    }

}