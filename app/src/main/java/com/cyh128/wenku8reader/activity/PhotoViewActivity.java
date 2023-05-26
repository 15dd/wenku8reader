package com.cyh128.wenku8reader.activity;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.cyh128.wenku8reader.R;
import com.cyh128.wenku8reader.util.NavbarStatusbarInit;
import com.github.chrisbanes.photoview.PhotoView;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

public class PhotoViewActivity extends AppCompatActivity {
    private PhotoView photoView;
    private RequestOptions options = new RequestOptions()
            .placeholder(R.drawable.image_loading_small)
            .skipMemoryCache(true)
            .diskCacheStrategy(DiskCacheStrategy.ALL);

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_view);
        NavbarStatusbarInit.allTransparent(this);
        photoView = findViewById(R.id.photoview_act_photo_view);

        androidx.appcompat.widget.Toolbar toolbar = findViewById(R.id.toolbar_act_photo_view);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(v -> {
            // 退出当前页面
            finish();
        });

        Intent intent = getIntent();
        String url = intent.getStringExtra("url");
        try {
            Glide.with(PhotoViewActivity.this).load(url).apply(options).into(photoView);
        } catch (Exception e) {
            runOnUiThread(() -> new MaterialAlertDialogBuilder(PhotoViewActivity.this)
                    .setTitle("网络超时")
                    .setMessage("连接超时，可能是服务器出错了或者您正在连接VPN或代理服务器，请稍后再试")
                    .setIcon(R.drawable.timeout)
                    .setCancelable(false)
                    .setPositiveButton("明白", (dialog, which) -> finish())
                    .show());
        }
    }
}
