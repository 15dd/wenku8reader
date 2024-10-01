package com.cyh128.hikarinovel.ui.view.main.home

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
import com.cyh128.hikarinovel.data.model.BookshelfNovelInfo
import com.cyh128.hikarinovel.data.model.HomeBlock
import com.cyh128.hikarinovel.data.model.NovelCover
import com.cyh128.hikarinovel.databinding.ItemNovelCoverBinding

class NovelCoverListAdapter(
    val list: List<NovelCover>,
    val onItemClick: (aid: String) -> Unit
) : RecyclerView.Adapter<NovelCoverListAdapter.ViewHolder>() {
    inner class ViewHolder(
        val binding: ItemNovelCoverBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemNovelCoverBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = list[position]
        holder.binding.apply {
            Glide.with(ivINovelCover)
                .load(item.img)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .addListener( object : RequestListener<Drawable> {
                    override fun onLoadFailed(
                        e: GlideException?,
                        model: Any?,
                        target: Target<Drawable>,
                        isFirstResource: Boolean
                    ): Boolean {
                        return false
                    }

                    override fun onResourceReady(
                        resource: Drawable,
                        model: Any,
                        target: Target<Drawable>?,
                        dataSource: DataSource,
                        isFirstResource: Boolean
                    ): Boolean {
                        cpiINovelCover.hide()
                        return false
                    }

                })
                .into(ivINovelCover)

            tvINovelCover.text = item.title
            root.setOnClickListener {
                onItemClick(item.aid)
            }
        }
    }

    override fun getItemCount(): Int = list.size
}

class BookshelfListAdapter(
    val list: List<BookshelfNovelInfo>,
    val onItemClick: (aid: String) -> Unit
) : RecyclerView.Adapter<BookshelfListAdapter.ViewHolder>() {

    inner class ViewHolder(val binding: ItemNovelCoverBinding): RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemNovelCoverBinding.inflate(
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
            Glide.with(ivINovelCover)
                .load(item.img)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .addListener( object : RequestListener<Drawable> {
                    override fun onLoadFailed(
                        e: GlideException?,
                        model: Any?,
                        target: Target<Drawable>,
                        isFirstResource: Boolean
                    ): Boolean {
                        return false
                    }

                    override fun onResourceReady(
                        resource: Drawable,
                        model: Any,
                        target: Target<Drawable>?,
                        dataSource: DataSource,
                        isFirstResource: Boolean
                    ): Boolean {
                        cpiINovelCover.hide()
                        return false
                    }

                })
                .into(ivINovelCover)

            tvINovelCover.text = item.title
            root.setOnClickListener {
                onItemClick(item.aid)
            }
        }
    }
}

class HomeBlockAdapter(
    val data: HomeBlock,
    val onItemClick: (aid: String) -> Unit
) : RecyclerView.Adapter<HomeBlockAdapter.ViewHolder>() {
    inner class ViewHolder(
        val binding: ItemNovelCoverBinding
    ) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemNovelCoverBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }


    override fun getItemCount(): Int = data.list.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = data.list[position]
        holder.binding.apply {
            Glide.with(ivINovelCover)
                .load(item.img)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .addListener( object : RequestListener<Drawable> {
                    override fun onLoadFailed(
                        e: GlideException?,
                        model: Any?,
                        target: Target<Drawable>,
                        isFirstResource: Boolean
                    ): Boolean {
                        return false
                    }

                    override fun onResourceReady(
                        resource: Drawable,
                        model: Any,
                        target: Target<Drawable>?,
                        dataSource: DataSource,
                        isFirstResource: Boolean
                    ): Boolean {
                        cpiINovelCover.hide()
                        return false
                    }

                })
                .into(ivINovelCover)

            tvINovelCover.text = item.title
            root.setOnClickListener {
                onItemClick(item.aid)
            }
        }
    }
}