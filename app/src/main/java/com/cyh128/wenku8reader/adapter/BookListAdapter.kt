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
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.cyh128.wenku8reader.R
import com.cyh128.wenku8reader.activity.ContentsActivity
import com.cyh128.wenku8reader.bean.BookListBean

class BookListAdapter(//https://blog.csdn.net/huweiliyi/article/details/105779329
    //https://blog.csdn.net/kaolagirl/article/details/117287350
    private val context: Context, private val novelList: List<BookListBean>
) : RecyclerView.Adapter<RecyclerView.ViewHolder?>() {
    private val options = RequestOptions()
        .skipMemoryCache(true)
        .diskCacheStrategy(DiskCacheStrategy.ALL)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        //https://blog.csdn.net/kaolagirl/article/details/115769719 layout组件居中问题
        val view = LayoutInflater.from(context).inflate(R.layout.item_book_list, parent, false)
        return ItemViewHolder(view)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val itemViewHolder = holder as ItemViewHolder
        val novel = novelList[position]
        itemViewHolder.title.text = novel.title
        itemViewHolder.author.text = novel.author
        itemViewHolder.other.text = novel.other
        if (novel.tags!!.trim { it <= ' ' }.isNotEmpty()) {
            itemViewHolder.tags.text = "标签:" + novel.tags
        } else {
            itemViewHolder.tags.text = "标签:(暂无标签)"
        }
        Glide.with(context)
            .load(novel.imgUrl)
            .apply(options)
            .placeholder(R.drawable.image_loading)
            .centerCrop()
            .into(itemViewHolder.imageUrl)
        itemViewHolder.number.text = (position + 1).toString()
        itemViewHolder.itemView.setOnClickListener { v: View? ->  //设置项目点击监听
            Log.d("debug", "BookListAdapter url:" + novel.bookUrl)
            val toContents = Intent(context, ContentsActivity::class.java)
            toContents.putExtra("bookUrl", novel.bookUrl)
            context.startActivity(toContents)
        }
    }

    override fun getItemCount(): Int {
        return novelList.size
    }

    inner class ItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var title: TextView
        var author: TextView
        var other: TextView
        var tags: TextView
        var imageUrl: ImageView
        var number: TextView

        init {
            title = itemView.findViewById(R.id.booktitle)
            author = itemView.findViewById(R.id.author)
            other = itemView.findViewById(R.id.other)
            tags = itemView.findViewById(R.id.tags)
            imageUrl = itemView.findViewById(R.id.bookcover)
            number = itemView.findViewById(R.id.number)
        }
    }
}
