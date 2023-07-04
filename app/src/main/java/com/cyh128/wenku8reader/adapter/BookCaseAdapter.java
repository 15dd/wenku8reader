package com.cyh128.wenku8reader.adapter;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.cyh128.wenku8reader.activity.ContentsActivity;
import com.cyh128.wenku8reader.classLibrary.BookcaseClass;
import com.cyh128.wenku8reader.R;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.util.List;


public class BookCaseAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    //https://blog.csdn.net/huweiliyi/article/details/105779329

    private Context context;
    private List<BookcaseClass> bookcase;
    private int mode;
    public final static int LINEAR = 1;
    public final static int GRID = 2;

    private RequestOptions options = new RequestOptions()
            .skipMemoryCache(true)
            .diskCacheStrategy(DiskCacheStrategy.ALL);

    public BookCaseAdapter(Context context, List<BookcaseClass> bookcase, int mode) {
        this.context = context;
        this.bookcase = bookcase;
        this.mode = mode;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //https://blog.csdn.net/kaolagirl/article/details/115769719 layout组件居中问题
        if (mode == LINEAR) {
            View view = LayoutInflater.from(context).inflate(R.layout.bookcase_list, parent, false);
            return new ItemViewHolder(view);
        } else if (mode == GRID) {
            View view = LayoutInflater.from(context).inflate(R.layout.bookcase_list_grid, parent, false);
            return new GridItemViewHolder(view);
        }
        Log.e("debug","null");
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof ItemViewHolder) {
            ItemViewHolder itemViewHolder = (ItemViewHolder) holder;
            BookcaseClass novel = bookcase.get(position);
            itemViewHolder.number.setText(String.valueOf(position + 1));
            itemViewHolder.title.setText(novel.title);
            itemViewHolder.author.setText("作者:" + novel.author);
            itemViewHolder.lastchapter.setText("最新章节:" + novel.lastchapter);
            Glide.with(context).load(novel.imgUrl).apply(options).placeholder(R.drawable.image_loading).into(itemViewHolder.cover);

            itemViewHolder.itemView.setOnClickListener(v -> {
                Log.d("debug", "url:" + novel.bookUrl);
                if (novel.bookUrl.trim().length() == 0) {//判断有没有对应页面的url
                    new MaterialAlertDialogBuilder(context)
                            .setCancelable(false)//禁止点击其他区域
                            .setTitle("警告")
                            .setMessage("无法获取该小说的详细信息,可能已下架")
                            .setPositiveButton("明白",null)
                            .show();
                    return;
                }

                Intent toContents = new Intent(context, ContentsActivity.class);
                toContents.putExtra("bookUrl", novel.bookUrl);
                context.startActivity(toContents);
            });
        } else if (holder instanceof GridItemViewHolder) {
            GridItemViewHolder gridItemViewHolder = (GridItemViewHolder) holder;
            BookcaseClass novel = bookcase.get(position);
            gridItemViewHolder.title.setText(novel.title);
            Glide.with(context).load(novel.imgUrl).apply(options).placeholder(R.drawable.image_loading).into(gridItemViewHolder.cover);

            gridItemViewHolder.itemView.setOnClickListener(v -> {
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
        }
    }

    @Override
    public int getItemCount() {
        return bookcase.size();
    }

    public class ItemViewHolder extends RecyclerView.ViewHolder {
        TextView number;
        TextView title;
        TextView author;
        TextView lastchapter;
        ImageView cover;

        public ItemViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.bookcase_booktitle);
            author = itemView.findViewById(R.id.bookcase_author);
            lastchapter = itemView.findViewById(R.id.bookcase_lastchapter);
            number = itemView.findViewById(R.id.text_bookcase_list_number);
            cover = itemView.findViewById(R.id.bookcase_cover);
        }
    }

    public class GridItemViewHolder extends RecyclerView.ViewHolder {
        TextView title;
        ImageView cover;

        public GridItemViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.bookcase_grid_booktitle);
            cover = itemView.findViewById(R.id.bookcase_grid_cover);
        }
    }
}


