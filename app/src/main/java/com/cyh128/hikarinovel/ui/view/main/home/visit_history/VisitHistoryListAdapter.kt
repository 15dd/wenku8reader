package com.cyh128.hikarinovel.ui.view.main.home.visit_history

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.cyh128.hikarinovel.data.source.local.database.visit_history.VisitHistoryEntity
import com.cyh128.hikarinovel.databinding.ItemVisitHistoryBinding
import com.cyh128.hikarinovel.ui.view.detail.NovelInfoActivity

class VisitHistoryListAdapter(
    private val onItemDelete: (aid: String) -> Unit
) : RecyclerView.Adapter<VisitHistoryListAdapter.ViewHolder>() {
    private val list: MutableList<VisitHistoryEntity> = mutableListOf()

    inner class ViewHolder(
        val context: Context,
        val binding: ItemVisitHistoryBinding
    ) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            parent.context,
            ItemVisitHistoryBinding.inflate(
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
            tvIVisitHistoryTitle.text = item.title
            tvIVisitHistoryTime.text = item.time
            Glide.with(ivIVisitHistory)
                .load(item.img)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(ivIVisitHistory)
            bIVisitHistory.setOnClickListener {
                //notifyItemRemoved(holder.absoluteAdapterPosition) //获取当前item在adapter中的绝对位置，防止错位
//                notifyItemRangeChanged(position,itemCount-position)
                onItemDelete(item.aid)
            }
            root.setOnClickListener {
                holder.context.startActivity(
                    Intent(holder.context, NovelInfoActivity::class.java).putExtra("aid",item.aid)
                )
            }
        }
    }

    fun updateData(list: List<VisitHistoryEntity>?) {
        this.list.clear()
        this.list.addAll(list?.reversed() ?: mutableListOf())
    }
}