package com.cyh128.wenku8reader.adapter

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.cyh128.wenku8reader.R

class CommentInCommentAdapter(
    private val context: Context,
    private val Comment: List<List<String>>
) : RecyclerView.Adapter<RecyclerView.ViewHolder?>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view =
            LayoutInflater.from(context).inflate(R.layout.item_comment_in_comment, parent, false)
        return ItemViewHolder(view)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val itemViewHolder = holder as ItemViewHolder
        itemViewHolder.user.text = Comment[position][0]
        itemViewHolder.date.text = Comment[position][1]
        itemViewHolder.comment.text = Comment[position][2]
        itemViewHolder.floor.text = (position + 1).let {
            if (it == 1) return@let "详细内容"
            else return@let "${it}楼"
        }
        itemViewHolder.itemView.setOnLongClickListener { v: View? ->
            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            val clipData = ClipData.newPlainText(null, Comment[position][2])
            clipboard.setPrimaryClip(clipData)
            Toast.makeText(context, "已复制评论", Toast.LENGTH_SHORT).show()
            true
        }
    }

    override fun getItemCount(): Int {
        return Comment.size
    }

    class ItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var user: TextView
        var date: TextView
        var comment: TextView
        var floor: TextView
        var avatar: ImageView? = null

        init {
            user = itemView.findViewById(R.id.text_item_comment_in_comment_user)
            date = itemView.findViewById(R.id.text_item_comment_in_comment_date)
            comment = itemView.findViewById(R.id.text_item_comment_in_comment_comment)
            floor = itemView.findViewById(R.id.text_item_comment_in_comment_floor)
        }
    }
}
