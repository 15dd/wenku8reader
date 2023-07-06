package com.cyh128.wenku8reader.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.cyh128.wenku8reader.R;
import com.cyh128.wenku8reader.adapter.CommentAdapter;
import com.cyh128.wenku8reader.util.Wenku8Spider;
import com.google.android.material.appbar.MaterialToolbar;

import java.util.ArrayList;
import java.util.List;

import me.jingbin.library.ByRecyclerView;

public class CommentActivity extends AppCompatActivity {
    private ByRecyclerView list;
    private List<List<String>> allComment = new ArrayList<>();
    private CommentAdapter commentAdapter;
    private int maxindex = 1;
    private int pageindex = 0;
    private String url;
    private View emptyView;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment);

        Intent intent = getIntent();
        url = intent.getStringExtra("url");
        list = findViewById(R.id.recyclerView_act_comment);
        emptyView = View.inflate(this, R.layout.empty_view, null);

        MaterialToolbar toolbar = findViewById(R.id.toolbar_act_comment);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(v -> {
            // 退出当前页面
            finish();
        });

        list.setLayoutManager(new LinearLayoutManager(this));
        commentAdapter = new CommentAdapter(this, allComment);
        list.setAdapter(commentAdapter);

        new Thread(() -> {
            List<List<String>> comment = getData();
            setPageData(true, comment);
            if (comment.size() == 0 || comment == null) {
                maxindex = 1;
            } else {
                maxindex = Integer.parseInt(comment.get(0).get(0));//设置总页数
            }
        }).start();

        list.setOnRefreshListener(new ByRecyclerView.OnRefreshListener() {
            @Override
            public void onRefresh() {
                pageindex = 0;
                allComment.clear();
                commentAdapter.notifyDataSetChanged();
                new Thread(() -> {
                    setPageData(true, getData());
                }).start();

                list.setRefreshing(false);
            }
        });
        list.setOnLoadMoreListener(new ByRecyclerView.OnLoadMoreListener() {
            @Override
            public void onLoadMore() {
                if (pageindex == maxindex) {
                    runOnUiThread(() -> {
                        list.loadMoreEnd();
                    });
                    return;
                }
                new Thread(() -> {
                    List<List<String>> comment = getData();
                    if (comment == null) {
                        runOnUiThread(() -> {
                            list.loadMoreFail();
                        });
                        return;
                    }
                    setPageData(true, comment);
                    runOnUiThread(() -> {
                        list.loadMoreComplete();
                    });
                }).start();
            }
        });
    }

    private void setPageData(boolean isFirstPage, List<List<String>> data) {
        if (list == null) {
            return;
        }
        if (isFirstPage) {
            // 第一页
            if (data != null && data.size() > 0) {
                // 有数据
                list.setStateViewEnabled(false);
                list.setLoadMoreEnabled(true);
                allComment.addAll(data);
                runOnUiThread(() -> {
                    commentAdapter.notifyItemChanged(commentAdapter.getItemCount(), commentAdapter.getItemCount() + 20);
                });
            } else {
                // 没数据，设置空布局
                runOnUiThread(() -> {
                    list.setStateView(emptyView);
                    list.setLoadMoreEnabled(false);
                });
            }
        } else {
            // 第二页
            if (data != null && data.size() > 0) {
                // 有数据，显示更多数据
                allComment.addAll(data);
                runOnUiThread(() -> {
                    commentAdapter.notifyItemChanged(commentAdapter.getItemCount(), commentAdapter.getItemCount() + 20);
                });
                list.loadMoreComplete();
            } else {
                // 没数据，显示加载到底
                list.loadMoreEnd();
            }
        }
    }

    private List<List<String>> getData() {
        try {
            return Wenku8Spider.getComment(url, ++pageindex);
        } catch (Exception e) {
            pageindex--;
            e.printStackTrace();
            return null;
        }
    }
}
