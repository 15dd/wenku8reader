package com.cyh128.hikari_novel.ui.detail.comment.reply

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.cyh128.hikari_novel.R
import com.cyh128.hikari_novel.data.model.Reply
import com.cyh128.hikari_novel.databinding.ItemReplyBinding
import com.cyh128.hikari_novel.util.ResourceUtil

class ReplyListAdapter(
    private val list: List<Reply>,
    private val onUsernameClick: (uid: String) -> Unit
) : RecyclerView.Adapter<ReplyListAdapter.ViewHolder>() {
    inner class ViewHolder(val binding: ItemReplyBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemReplyBinding.inflate(
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
            tvIReplyUsername.text = item.userName
            tvIReplyComment.text = item.content
            tvIReplyTime.text = item.time
            tvIReplyFloor.text = (position + 1).let {
                if (it == 1) return@let ResourceUtil.getString(R.string.content)
                else return@let "${it}${ResourceUtil.getString(R.string.floors)}"
            }
            tvIReplyUsername.setOnClickListener {
                onUsernameClick(item.uid)
            }
        }
    }
}