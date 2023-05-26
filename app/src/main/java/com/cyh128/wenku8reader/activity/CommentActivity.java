package com.cyh128.wenku8reader.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.cyh128.wenku8reader.adapter.CommentAdapter;
import com.cyh128.wenku8reader.util.NavbarStatusbarInit;
import com.cyh128.wenku8reader.util.Wenku8Spider;
import com.cyh128.wenku8reader.R;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.progressindicator.LinearProgressIndicator;

import java.util.ArrayList;
import java.util.List;

public class CommentActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private List<List<String>> allComment = new ArrayList<>();
    private LinearProgressIndicator linearProgressIndicator;
    private LinearLayoutManager layoutManager;
    private boolean canLoadmore = true;
    private CommentAdapter commentAdapter;
    private int lastPage;
    private int pageindex = 0;
    private String url;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment);
        NavbarStatusbarInit.allTransparent(CommentActivity.this);
        Intent intent = getIntent();
        url = intent.getStringExtra("url");

        recyclerView = findViewById(R.id.recyclerView_act_comment);
        linearProgressIndicator = findViewById(R.id.progress_act_comment);
        layoutManager = new LinearLayoutManager(CommentActivity.this);
        loadMoreListener();
        new addData().start();

        androidx.appcompat.widget.Toolbar toolbar = findViewById(R.id.toolbar_act_comment);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(v -> {
            // 退出当前页面
            finish();
        });
    }

    private void loadMoreListener() {
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            //https://blog.csdn.net/xiayiye5/article/details/121302424
            private boolean isSLidingUpward = false;

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);

                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    int lastItemPosition = layoutManager.findLastCompletelyVisibleItemPosition();
                    int itemCount = layoutManager.getItemCount();
                    if (lastItemPosition == (itemCount - 1) && isSLidingUpward) {
                        if (canLoadmore) {
                            canLoadmore = false;//防止在加载过程中多次加载
                            loadingMore();
                        }
                    }
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                isSLidingUpward = dy > 0;
            }

        });
    }

    private void loadingMore() {
        commentAdapter.setLoadState(commentAdapter.LOADING);
        if (pageindex < lastPage) {
            new addData().start();
        } else {
            commentAdapter.setLoadState(commentAdapter.LOADING_END);
        }
    }

    public class addData extends Thread {
        @Override
        public void run() {
            Looper.prepare();
            try {
                allComment.addAll(Wenku8Spider.getComment(url, ++pageindex));
                Message message = new Message();
                if (commentAdapter == null) {
                    first.sendMessage(message);
                    return;
                }
                addHandler.sendMessage(message);
            } catch (Exception e) {
                runOnUiThread(() -> new MaterialAlertDialogBuilder(CommentActivity.this)
                        .setTitle("错误")
                        .setMessage("可能的原因:\n1 -> 该书没有评论\n2 -> 连接超时，可能是服务器出错了或者您正在连接VPN或代理服务器，请稍后再试")
                        .setIcon(R.drawable.warning)
                        .setCancelable(false)
                        .setPositiveButton("明白", (dialog, which) -> finish())
                        .show());
            }
        }

        private Handler addHandler = new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(@NonNull Message msg) {
                commentAdapter.setLoadState(commentAdapter.LOADING_COMPLETE);
                commentAdapter.notifyDataSetChanged();
                canLoadmore = true;
                return false;
            }
        });

        private Handler first = new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(@NonNull Message msg) {
                commentAdapter = new CommentAdapter(CommentActivity.this, allComment);
                commentAdapter.setLoadState(commentAdapter.FIRST_PAGE);
                lastPage = Integer.parseInt(allComment.get(0).get(0));
                recyclerView.setAdapter(commentAdapter);
                recyclerView.setLayoutManager(layoutManager);
                linearProgressIndicator.hide();
                return false;
            }
        });
    }
}
