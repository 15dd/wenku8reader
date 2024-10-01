package com.cyh128.hikarinovel.ui.view.detail.comment

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.cyh128.hikarinovel.data.model.Comment
import com.cyh128.hikarinovel.databinding.ItemCommentBinding

class CommentListAdapter(
    val list: List<Comment>,
    val onItemClick: (replyUrl: String) -> Unit
) : RecyclerView.Adapter<CommentListAdapter.ViewHolder>() {
    inner class ViewHolder(val binding: ItemCommentBinding) : RecyclerView.ViewHolder(binding.root)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemCommentBinding.inflate(
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
            tvICommentUsername.text = item.userName
            tvICommentViewCount.text = item.viewCount
            tvICommentReplyCount.text = item.replyCount
            tvICommentComment.text = item.content
            tvICommentTime.text = item.time
            root.setOnClickListener {
                onItemClick(item.replyUrl)
            }
        }
    }

}