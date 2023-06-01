package com.cyh128.wenku8reader.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.cyh128.wenku8reader.R;
import com.cyh128.wenku8reader.activity.ContentsActivity;
import com.cyh128.wenku8reader.activity.SearchActivity;
import com.cyh128.wenku8reader.adapter.BookListAdapter;
import com.cyh128.wenku8reader.classLibrary.BookListClass;
import com.cyh128.wenku8reader.util.Wenku8Spider;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.util.ArrayList;
import java.util.List;

public class SearchFragment extends Fragment {
    private RecyclerView list;
    public int pageindex = 0;//上拉加载数据用，每上拉一次，索引值加1
    public int maxindex = 1;
    private List<BookListClass> novelList = new ArrayList<>();
    private View view;
    private LinearLayoutManager layoutManager;
    private boolean canLoadmore = true;
    private boolean isFiveSecondDone = true;
    private String searchText;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_search, container, false);
        searchText = getArguments().getString("searchText");
        list = view.findViewById(R.id.fragment_search_booklist);
        layoutManager = new LinearLayoutManager(view.getContext());
        loadMoreListener();
        waitFiveSecond();
        new addBook().start();

        return view;
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
        if (pageindex < maxindex && isFiveSecondDone) {
            waitFiveSecond();
            new addBook().start();
            return;
        } else if (pageindex < maxindex && !isFiveSecondDone) {
            bookListAdapter.setLoadState(bookListAdapter.WAIT_FIVE_SECOND);
            canLoadmore = true;
        } else {
            bookListAdapter.setLoadState(bookListAdapter.LOADING_END);
        }
        bookListAdapter.notifyItemChanged(bookListAdapter.getItemCount() - 1);
    }

    private void waitFiveSecond() {
        isFiveSecondDone = false;
        SearchActivity.searchFlag = false; //下滑操作也会触发搜索小说的5秒等待机制，所以需要将搜索框的搜索也加入限制，即下滑操作或者搜索小说的五秒没过，不允许操作
        new CountDownTimer(5500, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
            }

            @Override
            public void onFinish() {
                isFiveSecondDone = true;
                SearchActivity.searchFlag = true;
                if (bookListAdapter != null) {
                    if (bookListAdapter.loadState != bookListAdapter.LOADING_END) {
                        bookListAdapter.notifyItemRemoved(bookListAdapter.getItemCount());
                    }
                }
            }
        }.start();
    }

    private BookListAdapter bookListAdapter;
    private class addBook extends Thread {

        @Override
        public void run() {
            Looper.prepare();
            try {
                List<BookListClass> listClassList = Wenku8Spider.searchNovel("articlename", searchText, ++pageindex);
                if (listClassList.size() == 0) {
                    Message msg = new Message();
                    noContentHandler.sendMessage(msg);
                    return;
                } else if (listClassList.size() == 1) {
                    String url = listClassList.get(0).bookUrl;
                    Intent intent = new Intent(getActivity(), ContentsActivity.class);
                    intent.putExtra("bookUrl", url);
                    startActivity(intent);
                    return;
                }
                novelList.addAll(listClassList);
            } catch (Exception e) {
                getActivity().runOnUiThread(() -> {
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
            if (bookListAdapter == null) {//第一次添加
                maxindex = novelList.get(0).totalPage;//设置总页数

                firstLaunchHandler.sendMessage(msg);
            } else {
                addBookHandler.sendMessage(msg);
            }

            canLoadmore = true;
        }

        private final Handler addBookHandler = new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(Message msg) {
                bookListAdapter.setLoadState(bookListAdapter.LOADING_COMPLETE);
                bookListAdapter.notifyItemChanged(bookListAdapter.getItemCount(), bookListAdapter.getItemCount() + 20);
                return true;
            }
        });

        private final Handler firstLaunchHandler = new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(Message msg) {
                bookListAdapter = new BookListAdapter(getContext(), novelList);
                list.setAdapter(bookListAdapter);
                list.setLayoutManager(layoutManager);
                if (maxindex == 1) {
                    bookListAdapter.setLoadState(bookListAdapter.LOADING_END);
                    bookListAdapter.notifyDataSetChanged();
                } else {
                    bookListAdapter.setLoadState(bookListAdapter.LOADING_COMPLETE);
                    bookListAdapter.notifyDataSetChanged();
                }

                return false;
            }
        });

        private final Handler noContentHandler = new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(Message msg) {
                bookListAdapter = new BookListAdapter(getContext(), novelList);
                list.setAdapter(bookListAdapter);
                list.setLayoutManager(layoutManager);
                bookListAdapter.setLoadState(bookListAdapter.NONE);
                bookListAdapter.notifyDataSetChanged();
                return false;
            }
        });
    }
}
