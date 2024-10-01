package com.cyh128.wenku8reader.adapter

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.cyh128.wenku8reader.R
import com.cyh128.wenku8reader.activity.ContentsActivity
import com.cyh128.wenku8reader.bean.BookcaseBean
import com.cyh128.wenku8reader.util.GlobalConfig
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class BookCaseAdapter(//https://blog.csdn.net/huweiliyi/article/details/105779329
    private val context: Context, private val bookcase: List<BookcaseBean>, private val mode: Int
) : RecyclerView.Adapter<ViewHolder?>() {
    private val options = RequestOptions()
        .skipMemoryCache(true)
        .diskCacheStrategy(DiskCacheStrategy.ALL)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        //https://blog.csdn.net/kaolagirl/article/details/115769719 layout组件居中问题
        var item: ViewHolder? = null
        if (mode == LINEAR) {
            val view =
                LayoutInflater.from(context).inflate(R.layout.item_bookcase_list, parent, false)
            item = ItemViewHolder(view)
        } else if (mode == GRID) {
            val view = LayoutInflater.from(context)
                .inflate(R.layout.item_bookcase_list_grid, parent, false)
            item = GridItemViewHolder(view)
        }
        return item!!
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        if (holder is ItemViewHolder) {
            val itemViewHolder = holder
            val novel = bookcase[position]
            itemViewHolder.number.text = (position + 1).toString()
            itemViewHolder.title.text = novel.title
            itemViewHolder.author.text = "作者:" + novel.author
            itemViewHolder.lastchapter.text = "最新章节:" + novel.lastchapter
            Glide.with(context)
                .load(novel.imgUrl)
                .apply(options)
                .placeholder(R.drawable.image_loading)
                .centerCrop()
                .into(itemViewHolder.cover)
            itemViewHolder.itemView.setOnClickListener { v: View? ->
                Log.d("debug", "url:" + novel.bookUrl)
                if (novel.bookUrl.trim { it <= ' ' }.isEmpty()) { //判断有没有对应页面的url
                    MaterialAlertDialogBuilder(context)
                        .setCancelable(false) //禁止点击其他区域
                        .setTitle("警告")
                        .setMessage("无法获取该小说的详细信息,可能已下架")
                        .setPositiveButton("明白", null)
                        .show()
                    return@setOnClickListener
                }
                val url = String.format(
                    "https://${GlobalConfig.domain}/book/%s.htm",
                    novel.bookUrl.substring(
                        novel.bookUrl.indexOf("aid=") + 4,
                        novel.bookUrl.indexOf("&")
                    )
                )
                val toContents = Intent(context, ContentsActivity::class.java)
                toContents.putExtra("bookUrl", url)
                context.startActivity(toContents)
            }
        } else if (holder is GridItemViewHolder) {
            val gridItemViewHolder = holder
            val novel = bookcase[position]
            gridItemViewHolder.title.text = novel.title
            Glide.with(context)
                .load(novel.imgUrl)
                .apply(options)
                .placeholder(R.drawable.image_loading)
                .centerCrop()
                .into(gridItemViewHolder.cover)
            gridItemViewHolder.itemView.setOnClickListener { v: View? ->
                Log.d("debug", "url:" + novel.bookUrl)
                if (novel.bookUrl.trim { it <= ' ' }.isEmpty()) { //判断有没有对应页面的url
                    MaterialAlertDialogBuilder(context)
                        .setCancelable(false) //禁止点击其他区域
                        .setTitle("警告")
                        .setMessage("无法获取该小说的详细信息,可能已下架")
                        .setPositiveButton("明白", null)
                        .show()
                    return@setOnClickListener
                }
                val url = String.format(
                    "https://${GlobalConfig.domain}/book/%s.htm",
                    novel.bookUrl.substring(
                        novel.bookUrl.indexOf("aid=") + 4,
                        novel.bookUrl.indexOf("&")
                    )
                )
                val toContents = Intent(context, ContentsActivity::class.java)
                toContents.putExtra("bookUrl", url)
                context.startActivity(toContents)
            }
        }
    }

    override fun getItemCount(): Int {
        return bookcase.size
    }

    inner class ItemViewHolder(itemView: View) : ViewHolder(itemView) {
        var number: TextView
        var title: TextView
        var author: TextView
        var lastchapter: TextView
        var cover: ImageView

        init {
            title = itemView.findViewById(R.id.bookcase_booktitle)
            author = itemView.findViewById(R.id.bookcase_author)
            lastchapter = itemView.findViewById(R.id.bookcase_lastchapter)
            number = itemView.findViewById(R.id.text_bookcase_list_number)
            cover = itemView.findViewById(R.id.bookcase_cover)
        }
    }

    inner class GridItemViewHolder(itemView: View) : ViewHolder(itemView) {
        var title: TextView
        var cover: ImageView

        init {
            title = itemView.findViewById(R.id.bookcase_grid_booktitle)
            cover = itemView.findViewById(R.id.bookcase_grid_cover)
        }
    }

    companion object {
        const val LINEAR = 1
        const val GRID = 2
    }
}
