package com.cyh128.wenku8reader.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.cyh128.wenku8reader.R;
import com.cyh128.wenku8reader.activity.ContentsActivity;
import com.cyh128.wenku8reader.activity.SearchActivity;
import com.cyh128.wenku8reader.adapter.BookListAdapter;
import com.cyh128.wenku8reader.bean.BookListBean;
import com.cyh128.wenku8reader.util.GlobalConfig;
import com.cyh128.wenku8reader.util.Wenku8Spider;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.List;

import me.jingbin.library.ByRecyclerView;

public class SearchFragment extends Fragment {
    private ByRecyclerView list;
    public int pageindex = 0;//上拉加载数据用，每上拉一次，索引值加1
    public int maxindex = 1;
    private List<BookListBean> novelList = new ArrayList<>();
    private View view, emptyView;
    private String searchText;
    private BookListAdapter bookListAdapter;
    private RadioButton novelTitle,authorName;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        view = inflater.inflate(R.layout.fragment_booklist, container, false);
        emptyView = inflater.inflate(R.layout.empty_view, container, false);
        searchText = getArguments().getString("searchText");
        list = view.findViewById(R.id.booklist);
        novelTitle = getActivity().findViewById(R.id.radiobutton_act_search_noveltitle);
        authorName = getActivity().findViewById(R.id.radiobutton_act_search_authorname);

        list.setLayoutManager(new LinearLayoutManager(view.getContext()));

        bookListAdapter = new BookListAdapter(view.getContext(), novelList);
        list.setAdapter(bookListAdapter);

        new Thread(() -> {
            if (!GlobalConfig.isFiveSecondDone) {
                list.loadMoreFail();
                return;
            }

            List<BookListBean> bookListBeans = getData();
            if (bookListBeans.size() == 1) {
                String url = bookListBeans.get(0).bookUrl;
                Intent intent = new Intent(getActivity(), ContentsActivity.class);
                intent.putExtra("bookUrl", url);
                startActivity(intent);
                return;
            }
            if (bookListBeans.size() != 0) {
                maxindex = bookListBeans.get(0).totalPage;//设置总页数
            }
            setPageData(true, bookListBeans);
        }).start();

        list.setOnRefreshListener(() -> {
            if (!GlobalConfig.isFiveSecondDone) {
                Snackbar.make(view, "因网站限制，请等待5秒之后再重新尝试", Snackbar.LENGTH_SHORT)
                        .setAction("好的", v -> {return;})
                        .show();
                list.setRefreshing(false);
                return;
            }
            pageindex = 0;
            novelList.clear();
            bookListAdapter.notifyDataSetChanged();
            new Thread(() -> {
                setPageData(true, getData());
            }).start();

            list.setRefreshing(false);
        });
        list.setOnLoadMoreListener(() -> {
            if (pageindex == maxindex) {
                list.loadMoreEnd();
                return;
            }
            if (!GlobalConfig.isFiveSecondDone) {
                Snackbar.make(view, "因网站限制，请等待5秒之后再重新尝试", Snackbar.LENGTH_SHORT)
                        .setAction("好的", v -> {return;})
                        .show();
                list.loadMoreFail();
                return;
            }
            new Thread(() -> {
                List<BookListBean> bookListBeans = getData();
                if (bookListBeans == null) {
                    getActivity().runOnUiThread(() -> {
                        list.loadMoreFail();
                    });
                    return;
                }
                setPageData(true, bookListBeans);
                getActivity().runOnUiThread(() -> {
                    list.loadMoreComplete();
                });
            }).start();
        });

        return view;
    }

    private void setPageData(boolean isFirstPage, List<BookListBean> data) {
        if (list == null) {
            return;
        }
        if (isFirstPage) {
            // 第一页
            if (data != null && data.size() > 0) {
                // 有数据
                list.setStateViewEnabled(false);
                list.setLoadMoreEnabled(true);
                novelList.addAll(data);
                getActivity().runOnUiThread(() -> {
                    bookListAdapter.notifyItemChanged(bookListAdapter.getItemCount(), bookListAdapter.getItemCount() + 20);
                });
            } else {
                // 没数据，设置空布局
                getActivity().runOnUiThread(() -> {
                    list.setStateView(emptyView);
                    list.setLoadMoreEnabled(false);
                    bookListAdapter.notifyDataSetChanged();
                });
            }
        } else {
            // 第二页
            if (data != null && data.size() > 0) {
                // 有数据，显示更多数据
                novelList.addAll(data);
                getActivity().runOnUiThread(() -> {
                    bookListAdapter.notifyItemChanged(bookListAdapter.getItemCount(), bookListAdapter.getItemCount() + 20);
                });
                list.loadMoreComplete();
            }
        }
    }

    private List<BookListBean> getData() {
        try {
            new Thread(() -> {
                Looper.prepare();
                waitFiveSecond();
                Looper.loop();
            }).start();
            if (novelTitle.isChecked()) {
                System.out.println("checked");
            }
            String mode = novelTitle.isChecked() ? "articlename" : "author" ;

            return Wenku8Spider.searchNovel(mode, searchText, ++pageindex);
        } catch (Exception e) {
            pageindex--;
            e.printStackTrace();
            return null;
        }
    }

    private void waitFiveSecond() {
        GlobalConfig.isFiveSecondDone = false;
        SearchActivity.searchFlag = false; //下滑操作也会触发搜索小说的5秒等待机制，所以需要将搜索框的搜索也加入限制，即下滑操作或者搜索小说的五秒没过，不允许操作
        new CountDownTimer(5500, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
            }

            @Override
            public void onFinish() {
                GlobalConfig.isFiveSecondDone = true;
                SearchActivity.searchFlag = true;
            }
        }.start();
    }
}
