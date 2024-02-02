package com.cyh128.wenku8reader.oldReader

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.cyh128.wenku8reader.R
import com.cyh128.wenku8reader.activity.PhotoViewActivity

class ReaderAdapter(private val context: Context, private val imgUrl: List<String>?) :
    RecyclerView.Adapter<RecyclerView.ViewHolder?>() {
    private val options: RequestOptions = RequestOptions()
        .skipMemoryCache(true) // 内存不缓存
        .diskCacheStrategy(DiskCacheStrategy.NONE) // 磁盘缓存所有图

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): RecyclerView.ViewHolder {
        val view: View =
            LayoutInflater.from(context).inflate(R.layout.item_reader_image, parent, false)
        return ImageViewHolder(view)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val imagePosition: Int = position
        val imageViewHolder: ImageViewHolder = holder as ImageViewHolder
        Glide.with(context)
            .load(imgUrl!![imagePosition])
            .placeholder(R.drawable.image_loading)
            .override(1000)
            .apply(options)
            .into(imageViewHolder.imageView)
        imageViewHolder.itemView.setOnClickListener { v: View? ->
            val intent: Intent = Intent(context, PhotoViewActivity::class.java)
            intent.putExtra("url", imgUrl[imagePosition])
            context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
        if (imgUrl!!.isNotEmpty()) {
            return imgUrl.size
        }
        return 0
    }

    inner class ImageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var imageView: ImageView

        init {
            imageView = itemView.findViewById(R.id.image_item_reader_image)
        }
    }
}
