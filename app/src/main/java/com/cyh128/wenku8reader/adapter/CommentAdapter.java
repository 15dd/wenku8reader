package com.cyh128.wenku8reader.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.cyh128.wenku8reader.R;
import com.cyh128.wenku8reader.activity.CommentInCommentActivity;
import com.google.android.material.snackbar.Snackbar;

import java.util.List;

public class CommentAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private Context context;
    private List<List<String>> Comment;

    public CommentAdapter(Context context, List<List<String>> Comment) {
        this.context = context;
        this.Comment = Comment;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_comment, parent, false);
        return new ItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ItemViewHolder itemViewHolder = (ItemViewHolder) holder;
        int i = position;
        String viewData = Comment.get(i).get(2);
        String reply = viewData.substring(0, viewData.indexOf("/"));
        String view = viewData.substring(viewData.indexOf("/") + 1);
        itemViewHolder.user.setText(Comment.get(i).get(3));
        itemViewHolder.viewData.setText("回复:" + reply + " 查看:" + view);
        itemViewHolder.date.setText(Comment.get(i).get(4));
        itemViewHolder.comment.setText(Comment.get(i).get(5));
        itemViewHolder.itemView.setOnClickListener(v -> {
            if (viewData.substring(0, viewData.indexOf("/")).equals("0")) {
                Snackbar.make(itemViewHolder.user, "此评论没有回复", Snackbar.LENGTH_SHORT).show();
                return;
            }
            Intent intent = new Intent(context, CommentInCommentActivity.class);
            intent.putExtra("url", Comment.get(i).get(1));
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return Comment.size();
    }


    public static class ItemViewHolder extends RecyclerView.ViewHolder {
        TextView user, viewData, date, comment;

        public ItemViewHolder(@NonNull View itemView) {
            super(itemView);
            user = itemView.findViewById(R.id.text_item_comment_user);
            viewData = itemView.findViewById(R.id.text_item_comment_viewData);
            date = itemView.findViewById(R.id.text_item_comment_date);
            comment = itemView.findViewById(R.id.text_item_comment_comment);
        }
    }
}
