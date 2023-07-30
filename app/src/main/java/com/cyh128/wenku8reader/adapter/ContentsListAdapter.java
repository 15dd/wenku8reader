package com.cyh128.wenku8reader.adapter;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.cyh128.wenku8reader.R;
import com.cyh128.wenku8reader.activity.ContentsActivity;
import com.cyh128.wenku8reader.activity.ReadActivity;
import com.cyh128.wenku8reader.bean.ContentsCcssBean;
import com.cyh128.wenku8reader.bean.ContentsVcssBean;

import java.util.List;

import chen.you.expandable.ExpandableRecyclerView;

public class ContentsListAdapter extends ExpandableRecyclerView.ExpandableAdapter<ContentsListAdapter.GroupViewHolder,ContentsListAdapter.ChildViewHolder> {
    //https://blog.csdn.net/qq_43611861/article/details/118419424
    private Context context;
    private List<ContentsVcssBean> fatherList;
    private List<List<ContentsCcssBean>> childList;

    public ContentsListAdapter(Context context, List<ContentsVcssBean> fatherList, List<List<ContentsCcssBean>> childList) {
        this.context = context;
        this.fatherList = fatherList;
        this.childList = childList;
    }

    @Override
    public int getGroupCount() {
        return fatherList.size();
    }

    @Override
    public int getChildCount(int groupPos) {
        return childList.get(groupPos).size();
    }

    @NonNull
    @Override
    public GroupViewHolder onCreateGroupViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_chapter_group, parent, false);
        return new GroupViewHolder(view);
    }

    @NonNull
    @Override
    public ChildViewHolder onCreateChildViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_chapter_child, parent, false);
        return new ChildViewHolder(view);
    }

    @Override
    public void onBindGroupViewHolder(@NonNull GroupViewHolder vh, int groupPos, boolean isExpanded) {
        ContentsVcssBean vcssBean = fatherList.get(groupPos);
        vh.vcss.setText(vcssBean.vcss);
    }

    @Override
    public void onBindChildViewHolder(@NonNull ChildViewHolder vh, int groupPos, int childPos, boolean isLastChild) {
        ContentsCcssBean ccssBean = childList.get(groupPos).get(childPos);
        vh.ccss.setText(ccssBean.ccss);
        vh.itemView.setOnClickListener(v -> {
            Intent toContent = new Intent(context, ReadActivity.class);
            ContentsActivity.vcssPosition = groupPos;
            ContentsActivity.ccssPosition = childPos;
            context.startActivity(toContent);
        });
    }

    @Override
    public void onGroupStateChanged(@NonNull GroupViewHolder vh, int groupPos, boolean isExpanded) {
        super.onGroupStateChanged(vh, groupPos, isExpanded);
        if (isExpanded) {
            ObjectAnimator.ofFloat(vh.arrow, View.ROTATION, 180f)
                    .setDuration(100)
                    .start();
        } else {
            ObjectAnimator.ofFloat(vh.arrow, View.ROTATION, 0f)
                    .setDuration(100)
                    .start();
        }
    }

    class GroupViewHolder extends ExpandableRecyclerView.GroupViewHolder {
        TextView vcss;
        ImageView arrow;
        public GroupViewHolder(@NonNull View itemView) {
            super(itemView);
            vcss = itemView.findViewById(R.id.text_item_chapter_group);
            arrow = itemView.findViewById(R.id.image_item_chapter_group);
        }
    }

    class ChildViewHolder extends ExpandableRecyclerView.ChildViewHolder {
        TextView ccss;
        public ChildViewHolder(@NonNull View itemView) {
            super(itemView);
            ccss = itemView.findViewById(R.id.text_item_chapter_child);
        }
    }
}