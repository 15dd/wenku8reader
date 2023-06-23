package com.cyh128.wenku8reader.adapter;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.cyh128.wenku8reader.activity.ContentsActivity;
import com.cyh128.wenku8reader.classLibrary.BookListClass;
import com.cyh128.wenku8reader.R;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.util.List;


public class BookListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    //https://blog.csdn.net/huweiliyi/article/details/105779329
    //https://blog.csdn.net/kaolagirl/article/details/117287350
    private Context context;
    private List<BookListClass> novelList;
    public final int TYPE_ITEM = 1;
    public final int TYPE_FOOT = 2;
    public int loadState;
    public final int LOADING = 1;
    public final int LOADING_COMPLETE = 2;
    public final int LOADING_END = 3;
    public final int FIRST_PAGE = 4;
    public final int WAIT_FIVE_SECOND = 5;
    public final int NONE = 6;

    private RequestOptions options = new RequestOptions()
            .skipMemoryCache(true)
            .diskCacheStrategy(DiskCacheStrategy.ALL);

    public BookListAdapter(Context context, List<BookListClass> novelList) {
        this.context = context;
        this.novelList = novelList;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //https://blog.csdn.net/kaolagirl/article/details/115769719 layout组件居中问题
        if (viewType == TYPE_ITEM) {
            View view = LayoutInflater.from(context).inflate(R.layout.book_list, parent, false);
            return new ItemViewHolder(view);
        } else if (viewType == TYPE_FOOT) {
            View view = LayoutInflater.from(context).inflate(R.layout.loadmore, parent, false);
            return new FootViewHolder(view);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof ItemViewHolder) {
            ItemViewHolder itemViewHolder = (ItemViewHolder) holder;
            BookListClass novel = novelList.get(position);
            itemViewHolder.title.setText(novel.title);
            itemViewHolder.author.setText(novel.author);
            itemViewHolder.other.setText(novel.other);
            if (novel.tags.trim().length() != 0) {
                itemViewHolder.tags.setText("标签:" + novel.tags);
            } else {
                itemViewHolder.tags.setText("标签:(暂无标签)");
            }
            Glide.with(context).load(novel.imgUrl).apply(options).placeholder(R.drawable.image_loading_cover).into(itemViewHolder.imageUrl);
            itemViewHolder.number.setText(String.valueOf(position + 1));

            itemViewHolder.itemView.setOnClickListener(v -> {//设置项目点击监听
                Log.d("debug", "url:" + novel.bookUrl);
                if (novel.bookUrl.trim().length() == 0) {//判断有没有对应页面的url
                    new MaterialAlertDialogBuilder(context)
                            .setCancelable(false)//禁止点击其他区域
                            .setTitle("警告")
                            .setMessage("无法获取该小说的详细信息,可能已下架")
                            .setPositiveButton("明白", null)
                            .show();
                    return;
                }

                Intent toContents = new Intent(context, ContentsActivity.class);
                toContents.putExtra("bookUrl", novel.bookUrl);
                context.startActivity(toContents);
            });
        } else if (holder instanceof FootViewHolder) {
            FootViewHolder footViewHolder = (FootViewHolder) holder;
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
                case WAIT_FIVE_SECOND:
                    footViewHolder.textView.setVisibility(View.VISIBLE);
                    footViewHolder.progressBar.setVisibility(View.VISIBLE);
                    footViewHolder.textView.setText("两次下滑间隔必须大于5秒,请稍等");
                    break;
                case NONE:
                    footViewHolder.progressBar.setVisibility(View.INVISIBLE);
                    footViewHolder.textView.setVisibility(View.VISIBLE);
                    footViewHolder.textView.setText("没有内容");
                default:
                    break;
            }
        }
    }

    @Override
    public int getItemCount() {
        return novelList.size() + 1;
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
        //notifyDataSetChanged();
    }

    public class ItemViewHolder extends RecyclerView.ViewHolder {
        TextView title;
        TextView author;
        TextView other;
        TextView tags;
        ImageView imageUrl;
        TextView number;

        public ItemViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.booktitle);
            author = itemView.findViewById(R.id.author);
            other = itemView.findViewById(R.id.other);
            tags = itemView.findViewById(R.id.tags);
            imageUrl = itemView.findViewById(R.id.bookcover);
            number = itemView.findViewById(R.id.number);
        }
    }

    public class FootViewHolder extends RecyclerView.ViewHolder {
        TextView textView;
        ProgressBar progressBar;

        public FootViewHolder(@NonNull View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.text_loadmore_tip);
            progressBar = itemView.findViewById(R.id.progress_loadmore_tip);
        }
    }
}








