package com.cyh128.wenku8reader.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.cyh128.wenku8reader.activity.CommentInCommentActivity;
import com.cyh128.wenku8reader.R;
import com.google.android.material.snackbar.Snackbar;

import java.util.List;

public class CommentAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private Context context;
    private List<List<String>> Comment;
    public final int TYPE_ITEM = 1;
    public final int TYPE_FOOT = 2;
    public int loadState;
    public final int LOADING = 1;
    public final int LOADING_COMPLETE = 2;
    public final int LOADING_END = 3;
    public final int FIRST_PAGE = 4;

    public CommentAdapter(Context context, List<List<String>> Comment) {
        this.context = context;
        this.Comment = Comment;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == TYPE_ITEM) {
            View view = LayoutInflater.from(context).inflate(R.layout.item_comment, parent, false);
            return new ItemViewHolder(view);
        } else if (viewType == TYPE_FOOT) {
            View view = LayoutInflater.from(context).inflate(R.layout.loadmore, parent, false);
            return new FootViewHolder(view);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof ItemViewHolder itemViewHolder) {
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

        } else if (holder instanceof FootViewHolder footViewHolder) {
            switch (loadState) {
                case LOADING:
                    footViewHolder.textView.setVisibility(View.VISIBLE);
                    footViewHolder.progressBar.setVisibility(View.VISIBLE);
                    footViewHolder.textView.setText("加载中");
                    break;
                case LOADING_COMPLETE:
                    footViewHolder.textView.setVisibility(View.VISIBLE);
                    footViewHolder.progressBar.setVisibility(View.VISIBLE);
                    footViewHolder.progressBar.setProgress(100);
                    footViewHolder.textView.setText("加载完成");
                    break;
                case LOADING_END:
                    footViewHolder.textView.setVisibility(View.VISIBLE);
                    footViewHolder.progressBar.setVisibility(View.INVISIBLE);
                    footViewHolder.textView.setText("已经到底了");
                    break;
                case FIRST_PAGE:
                    footViewHolder.progressBar.setVisibility(View.INVISIBLE);
                    footViewHolder.textView.setVisibility(View.INVISIBLE);
                    break;
                default:
                    break;
            }
        }
    }

    @Override
    public int getItemCount() {
        return Comment.size() + 1;
    }

    @Override
    public int getItemViewType(int position) {
        if (position + 1 == getItemCount()) {
            return TYPE_FOOT;
        } else {
            return TYPE_ITEM;
        }
    }

    public void setLoadState(int loadState) {
        this.loadState = loadState;
        notifyDataSetChanged();
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

    public static class FootViewHolder extends RecyclerView.ViewHolder {
        TextView textView;
        ProgressBar progressBar;

        public FootViewHolder(@NonNull View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.text_loadmore_tip);
            progressBar = itemView.findViewById(R.id.progress_loadmore_tip);
        }
    }
}
