package com.cyh128.wenku8reader.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.cyh128.wenku8reader.activity.PhotoViewActivity;
import com.cyh128.wenku8reader.R;

import java.util.List;

public class ReaderAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private Context context;
    private List<String> imgUrl;
    private RequestOptions options = new RequestOptions()
            .skipMemoryCache(true)// 内存不缓存
            .diskCacheStrategy(DiskCacheStrategy.NONE);// 磁盘缓存所有图

    public ReaderAdapter(Context context, List<String> imgUrl) {
        this.context = context;
        this.imgUrl = imgUrl;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_reader_image, parent, false);
        return new ImageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        int imagePosition = position;
        ImageViewHolder imageViewHolder = (ImageViewHolder) holder;
        Glide.with(context)
                .load(imgUrl.get(imagePosition))
                .placeholder(R.drawable.image_loading)
                .override(1000)
                .apply(options)
                .into(imageViewHolder.imageView);
        imageViewHolder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, PhotoViewActivity.class);
            intent.putExtra("url", imgUrl.get(imagePosition));
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        if (!imgUrl.isEmpty()) {
            return imgUrl.size();
        }
        return 0;
    }

    public class ImageViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;

        public ImageViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.image_item_reader_image);
        }
    }
}
