package com.cyh128.wenku8reader.fragment;

import static android.app.Activity.RESULT_OK;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
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

import com.cyh128.wenku8reader.R;
import com.cyh128.wenku8reader.activity.ContentsActivity;
import com.cyh128.wenku8reader.adapter.BookListAdapter;
import com.cyh128.wenku8reader.classLibrary.BookListClass;
import com.cyh128.wenku8reader.util.Wenku8Spider;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class SearchFragment extends Fragment {
    private RecyclerView list;
    public int pageindex = 0;//上拉加载数据用，每上拉一次，索引值加1
    public int maxindex = 1;
    private List<BookListClass> novelList = new ArrayList<>();
    private View view;
    private LinearLayoutManager layoutManager;
    private String searchText;
    private boolean searchFlag = true;
    private boolean canLoadmore = true;
    private BookListAdapter bookListAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_booklist, container, false);
        searchText = getArguments().getString("searchText");
        list = view.findViewById(R.id.booklist);
        layoutManager = new LinearLayoutManager(getContext());
        bookListAdapter = new BookListAdapter(getContext(), novelList);

        new first().start(); //第一次添加
        waitFiveSecond(); //等待5秒
        loadMoreListener();

        return view;
    }

    public void loadMoreListener() {
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
        if (pageindex < maxindex && searchFlag) { //当可以继续搜索(过了5秒)，并且可以继续加载下一页时
            canLoadmore = false;
            bookListAdapter.setLoadState(bookListAdapter.LOADING);
            new addBook().start();
            waitFiveSecond();
        } else if (pageindex < maxindex && !searchFlag) { //当无法搜索(没过5秒)，但可以继续加载下一页时
            bookListAdapter.setLoadState(bookListAdapter.WAIT_FIVE_SECOND);
        } else { //当加载不了下一页时(已经到了最后一页)时
            bookListAdapter.setLoadState(bookListAdapter.LOADING_END);
        }
    }

    private void waitFiveSecond() {
        searchFlag = false;
        new CountDownTimer(5000, 1000) { //开始5秒倒计时
            @Override
            public void onTick(long millisUntilFinished) {
                Log.w("debug", millisUntilFinished + "ms");
                Log.w("debug", String.valueOf(searchFlag));
            }

            @Override
            public void onFinish() {
                Log.w("debug", "CountDownTimer Finish");
                //bookListAdapter.notifyItemRemoved(bookListAdapter.getItemCount());
                searchFlag = true;//5秒到，设置为可以搜索
                bookListAdapter.notifyItemRemoved(bookListAdapter.getItemCount());//隐藏提示
            }
        }.start();
    }

    private class addBook extends Thread {

        @Override
        public void run() {
            Looper.prepare();

            try {
                novelList.addAll(Wenku8Spider.searchNovel("articlename", searchText, ++pageindex));
            } catch (Exception e) {
                new MaterialAlertDialogBuilder(view.getContext())
                        .setTitle("网络超时")
                        .setMessage("连接超时，可能是服务器出错了、也可能是网络卡慢或者您正在连接VPN或代理服务器，请稍后再试")
                        .setIcon(R.drawable.timeout)
                        .setCancelable(false)
                        .setPositiveButton("明白", (dialog, which) -> getActivity().finish())
                        .show();
                return;
            }

            if (novelList.size() == 0) {
                bookListAdapter.setLoadState(bookListAdapter.LOADING_END);
                bookListAdapter.notifyDataSetChanged();
                return;
            }
            Message msg = new Message();
            msg.what = RESULT_OK;
            addBookHandler.sendMessage(msg);

            canLoadmore = true;
        }

        private final Handler addBookHandler = new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(Message msg) {
                if (msg.what == RESULT_OK) {
                    bookListAdapter.setLoadState(bookListAdapter.LOADING_COMPLETE);
                    return true;
                } else if ((int) (novelList.size() / 20) == maxindex - 1) { //判断是否最后一页
                    bookListAdapter.setLoadState(bookListAdapter.LOADING_END);
                    return false;
                }
                return false;
            }
        });
    }

    private class first extends Thread {

        @Override
        public void run() {
            Looper.prepare();
            try {
                List<BookListClass> listClassList = Wenku8Spider.searchNovel("articlename", searchText, ++pageindex);
                if (listClassList != null && listClassList.size() == 1){
                    Intent intent = new Intent(getActivity(), ContentsActivity.class);
                    intent.putExtra("bookUrl",listClassList.get(0).bookUrl);
                    startActivity(intent);
                    return;
                } else if (listClassList != null) {
                    novelList.addAll(listClassList);
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            Message msg = new Message();
            msg.what = RESULT_OK;
            firstLaunchHandler.sendMessage(msg);

        }

        private final Handler firstLaunchHandler = new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(Message msg) {
                if (msg.what == RESULT_OK) {
                    list.setAdapter(bookListAdapter);
                    list.setLayoutManager(layoutManager);

                    if (novelList.size() == 0) {
                        bookListAdapter.setLoadState(bookListAdapter.NONE);
                        return false;
                    }
                    maxindex = novelList.get(0).totalPage;//判断novelList有item时(size()>0)，设置总页数
                    Log.d("debug", String.valueOf(maxindex));

                    if ((int) (novelList.size() / 20) == maxindex - 1) { //判断是否最后一页
                        bookListAdapter.setLoadState(bookListAdapter.LOADING_END);
                        return false;
                    }

                    bookListAdapter.setLoadState(bookListAdapter.FIRST_PAGE);
                    return true;
                }
                return false;
            }
        });
    }
}
