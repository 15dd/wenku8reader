package com.cyh128.wenku8reader.fragment;

import static android.app.Activity.RESULT_OK;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.cyh128.wenku8reader.classLibrary.BookListClass;
import com.cyh128.wenku8reader.R;
import com.cyh128.wenku8reader.adapter.BookListAdapter;
import com.cyh128.wenku8reader.util.Wenku8Spider;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.util.ArrayList;
import java.util.List;

public class BookListFragment extends Fragment {
    private RecyclerView list;
    public int pageindex = 0;//上拉加载数据用，每上拉一次，索引值加1
    public int maxindex = 1;
    private List<BookListClass> novelList = new ArrayList<>();
    private View view;
    private LinearLayoutManager layoutManager;
    private SwipeRefreshLayout swipeRefreshLayout;
    private boolean canLoadmore = true;
    private String bookType;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_booklist, container, false);
        bookType = getArguments().getString("type");
        list = view.findViewById(R.id.booklist);
        swipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayout_frag_booklist);
        swipeRefreshLayout.setRefreshing(true);
        layoutManager = new LinearLayoutManager(view.getContext());
        loadMoreListener();
        swipeRefreshListenter();
        new addBook().start();

        return view;
    }

    private void swipeRefreshListenter() {
        swipeRefreshLayout.setOnRefreshListener(() -> {
            pageindex = 0;
            novelList = new ArrayList<>();
            bookListAdapter = null;
            new addBook().start();
        });
    }

    private void loadMoreListener() {
        list.addOnScrollListener(new RecyclerView.OnScrollListener() {
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
        bookListAdapter.setLoadState(bookListAdapter.LOADING);
        if (pageindex < maxindex) {
            new addBook().start();
        } else {
            bookListAdapter.setLoadState(bookListAdapter.LOADING_END);
        }
    }

    private BookListAdapter bookListAdapter;

    private class addBook extends Thread {

        @Override
        public void run() {
            Looper.prepare();
            try {
                novelList.addAll(Wenku8Spider.getNovelByType(bookType, ++pageindex));
            } catch (Exception e) {
                Log.e("debug", "booklistFragment error");
                getActivity().runOnUiThread(() -> {
                    swipeRefreshLayout.setRefreshing(false);
                    new MaterialAlertDialogBuilder(view.getContext())
                            .setTitle("网络超时")
                            .setMessage("连接超时，可能是服务器出错了、也可能是网络卡慢或者您正在连接VPN或代理服务器，请稍后再试")
                            .setIcon(R.drawable.timeout)
                            .setCancelable(false)
                            .setPositiveButton("明白", null)
                            .show();
                    canLoadmore = true;
                    if (bookListAdapter != null) {
                        bookListAdapter.notifyItemRemoved(bookListAdapter.getItemCount());
                    }
                    --pageindex;
                });
                return;
            }

            Message msg = new Message();
            msg.what = RESULT_OK;
            if (bookListAdapter == null) {//第一次添加
                maxindex = novelList.get(0).totalPage;//设置总页数
                Log.d("debug", String.valueOf(maxindex));

                firstLaunchHandler.sendMessage(msg);
            } else {
                addBookHandler.sendMessage(msg);
            }

            canLoadmore = true;
        }

        private final Handler addBookHandler = new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(Message msg) {
                if (msg.what == RESULT_OK) {
                    bookListAdapter.setLoadState(bookListAdapter.LOADING_COMPLETE);
                    bookListAdapter.notifyDataSetChanged();
                    return true;
                }
                return false;
            }
        });

        private final Handler firstLaunchHandler = new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(Message msg) {
                if (msg.what == RESULT_OK) {
                    bookListAdapter = new BookListAdapter(getContext(), novelList);
                    list.setAdapter(bookListAdapter);
                    list.setLayoutManager(layoutManager);
                    bookListAdapter.setLoadState(bookListAdapter.FIRST_PAGE);
                    bookListAdapter.notifyDataSetChanged();

                    swipeRefreshLayout.setRefreshing(false);
                    return true;
                }
                return false;
            }
        });
    }
}
