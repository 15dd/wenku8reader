package com.cyh128.wenku8reader.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import com.cyh128.wenku8reader.classLibrary.ContentsCcssClass;
import com.cyh128.wenku8reader.classLibrary.ContentsVcssClass;
import com.cyh128.wenku8reader.R;

import java.util.List;

public class ContentsListAdapter extends BaseExpandableListAdapter {
    //https://blog.csdn.net/qq_43611861/article/details/118419424
    private Context context;
    private List<ContentsVcssClass> fatherList;
    private List<List<ContentsCcssClass>> childList;

    public ContentsListAdapter(Context context, List<ContentsVcssClass> fatherList, List<List<ContentsCcssClass>> childList) {
        this.context = context;
        this.fatherList = fatherList;
        this.childList = childList;
    }

    @Override
    public int getGroupCount() {
        return fatherList.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {//https://www.bbsmax.com/A/E35ppYlA5v/
        return childList.get(groupPosition).size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return fatherList.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return childList.get(groupPosition).get(childPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return groupPosition + childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        convertView = LayoutInflater.from(context).inflate(R.layout.item_expandable_list_father, parent, false);
        TextView title = (TextView) convertView.findViewById(R.id.text_item_expandable_list_father);
        title.setText(fatherList.get(groupPosition).vcss);
        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        convertView = LayoutInflater.from(context).inflate(R.layout.item_expandable_list_child, parent, false);
        TextView childTitle = convertView.findViewById(R.id.text_item_expandable_list_child);
        childTitle.setText(childList.get(groupPosition).get(childPosition).ccss);
        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }
}
