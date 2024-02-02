package com.cyh128.wenku8reader.adapter

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.cyh128.wenku8reader.R
import com.cyh128.wenku8reader.activity.CommentInCommentActivity
import com.google.android.material.snackbar.Snackbar

class CommentAdapter(private val context: Context, private val Comment: List<List<String>>) :
    RecyclerView.Adapter<RecyclerView.ViewHolder?>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_comment, parent, false)
        return ItemViewHolder(view)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val itemViewHolder = holder as ItemViewHolder
        val viewData = Comment[position][2]
        val reply = viewData.substring(0, viewData.indexOf("/"))
        val view = viewData.substring(viewData.indexOf("/") + 1)
        itemViewHolder.user.text = Comment[position][3]
        itemViewHolder.viewData.text = "回复:$reply 查看:$view"
        itemViewHolder.date.text = Comment[position][4]
        itemViewHolder.comment.text = Comment[position][5]
        itemViewHolder.itemView.setOnClickListener { v: View? ->
            if (viewData.substring(0, viewData.indexOf("/")) == "0") {
                Snackbar.make(itemViewHolder.user, "此评论没有回复", Snackbar.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            val intent = Intent(context, CommentInCommentActivity::class.java)
            intent.putExtra("url", Comment[position][1])
            context.startActivity(intent)
        }
        itemViewHolder.itemView.setOnLongClickListener { v: View? ->
            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            val clipData = ClipData.newPlainText(null, Comment[position][5])
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
        var viewData: TextView
        var date: TextView
        var comment: TextView

        init {
            user = itemView.findViewById(R.id.text_item_comment_user)
            viewData = itemView.findViewById(R.id.text_item_comment_viewData)
            date = itemView.findViewById(R.id.text_item_comment_date)
            comment = itemView.findViewById(R.id.text_item_comment_comment)
        }
    }
}
