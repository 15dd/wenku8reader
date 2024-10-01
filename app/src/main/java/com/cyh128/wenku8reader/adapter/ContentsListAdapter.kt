package com.cyh128.wenku8reader.adapter

import android.animation.ObjectAnimator
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import chen.you.expandable.ExpandableRecyclerView
import chen.you.expandable.ExpandableRecyclerView.ExpandableAdapter
import com.cyh128.wenku8reader.R
import com.cyh128.wenku8reader.activity.ContentsActivity
import com.cyh128.wenku8reader.bean.ContentsCcssBean
import com.cyh128.wenku8reader.bean.ContentsVcssBean
import com.cyh128.wenku8reader.newReader.ReaderActivity
import com.cyh128.wenku8reader.util.GlobalConfig

class ContentsListAdapter(//https://blog.csdn.net/qq_43611861/article/details/118419424
    private val context: Context,
    private val fatherList: List<ContentsVcssBean>,
    private val childList: List<List<ContentsCcssBean>>
) : ExpandableAdapter<ContentsListAdapter.GroupViewHolder?, ContentsListAdapter.ChildViewHolder?>() {
    override fun getGroupCount(): Int {
        return fatherList.size
    }

    override fun getChildCount(groupPos: Int): Int {
        return childList[groupPos].size
    }

    override fun onCreateGroupViewHolder(parent: ViewGroup, viewType: Int): GroupViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_chapter_group, parent, false)
        return GroupViewHolder(view)
    }

    override fun onCreateChildViewHolder(parent: ViewGroup, viewType: Int): ChildViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_chapter_child, parent, false)
        return ChildViewHolder(view)
    }

    override fun onBindGroupViewHolder(vh: GroupViewHolder, groupPos: Int, isExpanded: Boolean) {
        val vcssBean = fatherList[groupPos]
        vh.vcss.text = vcssBean.vcss
    }

    override fun onBindChildViewHolder(
        vh: ChildViewHolder,
        groupPos: Int,
        childPos: Int,
        isLastChild: Boolean
    ) {
        val ccssBean = childList[groupPos][childPos]
        vh.ccss.text = ccssBean.ccss
        vh.itemView.setOnClickListener { v: View? ->
            val toContent: Intent = if (GlobalConfig.readerMode == 1) {
                Intent(context, ReaderActivity::class.java)
            } else {
                Intent(context, com.cyh128.wenku8reader.oldReader.ReaderActivity::class.java)
            }
            ContentsActivity.vcssPosition = groupPos
            ContentsActivity.ccssPosition = childPos
            context.startActivity(toContent)
        }
    }

    override fun onGroupStateChanged(vh: GroupViewHolder, groupPos: Int, isExpanded: Boolean) {
        super.onGroupStateChanged(vh, groupPos, isExpanded)
        if (isExpanded) {
            ObjectAnimator.ofFloat(vh.arrow, View.ROTATION, 180f)
                .setDuration(100)
                .start()
        } else {
            ObjectAnimator.ofFloat(vh.arrow, View.ROTATION, 0f)
                .setDuration(100)
                .start()
        }
    }

    inner class GroupViewHolder(itemView: View) : ExpandableRecyclerView.GroupViewHolder(itemView) {
        var vcss: TextView
        var arrow: ImageView

        init {
            vcss = itemView.findViewById(R.id.text_item_chapter_group)
            arrow = itemView.findViewById(R.id.image_item_chapter_group)
        }
    }

    inner class ChildViewHolder(itemView: View) : ExpandableRecyclerView.ChildViewHolder(itemView) {
        var ccss: TextView

        init {
            ccss = itemView.findViewById(R.id.text_item_chapter_child)
        }
    }
}