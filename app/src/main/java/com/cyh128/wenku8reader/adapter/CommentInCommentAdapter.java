package com.cyh128.wenku8reader.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.cyh128.wenku8reader.R;

import java.util.List;

public class CommentInCommentAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private Context context;
    private List<List<String>> Comment;


    public CommentInCommentAdapter(Context context, List<List<String>> Comment) {
        this.context = context;
        this.Comment = Comment;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_comment_in_comment, parent, false);
        return new ItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ItemViewHolder itemViewHolder = (ItemViewHolder) holder;
        itemViewHolder.user.setText(Comment.get(position).get(0));
        itemViewHolder.date.setText(Comment.get(position).get(1));
        itemViewHolder.comment.setText(Comment.get(position).get(2));
        itemViewHolder.floor.setText(position + 1 + "楼");
    }

    @Override
    public int getItemCount() {
        return Comment.size();
    }

    public static class ItemViewHolder extends RecyclerView.ViewHolder {
        TextView user, date, comment, floor;

        public ItemViewHolder(@NonNull View itemView) {
            super(itemView);
            user = itemView.findViewById(R.id.text_item_comment_in_comment_user);
            date = itemView.findViewById(R.id.text_item_comment_in_comment_date);
            comment = itemView.findViewById(R.id.text_item_comment_in_comment_comment);
            floor = itemView.findViewById(R.id.text_item_comment_in_comment_floor);
        }
    }
}
