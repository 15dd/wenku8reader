package com.cyh128.hikarinovel.ui.view.read.vertical

import android.content.Context
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.cyh128.hikarinovel.databinding.ItemVerticalReadImageBinding

class ReadAdapter(
    private val context: Context,
    private val imgUrl: List<String>?,
    val onImageClick: (imageUrl: String) -> Unit
) : RecyclerView.Adapter<ReadAdapter.ViewHolder>() {
    inner class ViewHolder(val binding: ItemVerticalReadImageBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
        ViewHolder(
            ItemVerticalReadImageBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val p = position

        Glide.with(holder.binding.ivIVReadImage)
            .load(imgUrl!![p])
            .diskCacheStrategy(DiskCacheStrategy.ALL)
            .addListener(object : RequestListener<Drawable> {
                override fun onLoadFailed(
                    e: GlideException?,
                    model: Any?,
                    target: Target<Drawable>,
                    isFirstResource: Boolean
                ): Boolean {
                    holder.binding.cpiIVReadImage.hide()
                    holder.binding.tvIVReadImage.text = "插图#${p + 1} 加载失败"
                    return true
                }

                override fun onResourceReady(
                    resource: Drawable,
                    model: Any,
                    target: Target<Drawable>?,
                    dataSource: DataSource,
                    isFirstResource: Boolean
                ): Boolean {
                    holder.binding.cpiIVReadImage.hide()
                    return false
                }
            })
            .into(holder.binding.ivIVReadImage)

        holder.binding.apply {
            ivIVReadImage.setOnClickListener {
                onImageClick(imgUrl[p])
            }
            tvIVReadImage.text = "插图#${p + 1}"
        }
    }

    override fun getItemCount(): Int {
        if (imgUrl!!.isNotEmpty()) {
            return imgUrl.size
        }
        return 0
    }
}
