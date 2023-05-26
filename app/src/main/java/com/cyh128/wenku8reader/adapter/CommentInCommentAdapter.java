package com.cyh128.wenku8reader.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.cyh128.wenku8reader.R;

import java.util.List;

public class CommentInCommentAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private Context context;
    private List<List<String>> Comment;
    public final int TYPE_ITEM = 1;
    public final int TYPE_FOOT = 2;
    public int loadState;
    public final int LOADING = 1;
    public final int LOADING_COMPLETE = 2;
    public final int LOADING_END = 3;
    public final int FIRST_PAGE = 4;

    public CommentInCommentAdapter(Context context, List<List<String>> Comment) {
        this.context = context;
        this.Comment = Comment;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == TYPE_ITEM) {
            View view = LayoutInflater.from(context).inflate(R.layout.item_comment_in_comment, parent, false);
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
            itemViewHolder.user.setText(Comment.get(position).get(0));
            itemViewHolder.date.setText(Comment.get(position).get(1));
            itemViewHolder.comment.setText(Comment.get(position).get(2));
            itemViewHolder.floor.setText(position + 1 + "楼");
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
        TextView user, date, comment, floor;

        public ItemViewHolder(@NonNull View itemView) {
            super(itemView);
            user = itemView.findViewById(R.id.text_item_comment_in_comment_user);
            date = itemView.findViewById(R.id.text_item_comment_in_comment_date);
            comment = itemView.findViewById(R.id.text_item_comment_in_comment_comment);
            floor = itemView.findViewById(R.id.text_item_comment_in_comment_floor);
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
