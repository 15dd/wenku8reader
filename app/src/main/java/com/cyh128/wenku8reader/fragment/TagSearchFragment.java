package com.cyh128.wenku8reader.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.cyh128.wenku8reader.R;
import com.cyh128.wenku8reader.adapter.BookListAdapter;
import com.cyh128.wenku8reader.classLibrary.BookListClass;
import com.cyh128.wenku8reader.util.Wenku8Spider;
import com.cyh128.wenku8reader.util.loginWenku8;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import me.jingbin.library.ByRecyclerView;

public class TagSearchFragment extends Fragment {
    private ByRecyclerView list;
    private int pageindex = 0;//上拉加载数据用，每上拉一次，索引值加1
    private int maxindex = 1;
    private List<BookListClass> novelList = new ArrayList<>();
    private View view, emptyView;
    private String sort, tag;
    private BookListAdapter bookListAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        view = inflater.inflate(R.layout.fragment_booklist, container, false);
        tag = getArguments().getString("tag");
        sort = getArguments().getString("sort");
        emptyView = inflater.inflate(R.layout.empty_view, container, false);
        list = view.findViewById(R.id.booklist);
        list.setLayoutManager(new LinearLayoutManager(view.getContext()));

        bookListAdapter = new BookListAdapter(view.getContext(), novelList);
        list.setAdapter(bookListAdapter);
        new Thread(() -> {
            List<BookListClass> bookListClasses = getData();
            setPageData(true, bookListClasses);
            maxindex = bookListClasses.get(0).totalPage;//设置总页数
        }).start();

        list.setOnRefreshListener(new ByRecyclerView.OnRefreshListener() {
            @Override
            public void onRefresh() {
                pageindex = 0;
                novelList.clear();
                bookListAdapter.notifyDataSetChanged();
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
                    list.loadMoreEnd();
                    return;
                }
                new Thread(() -> {
                    List<BookListClass> bookListClasses = getData();
                    if (bookListClasses == null) {
                        getActivity().runOnUiThread(() -> {
                            list.loadMoreFail();
                        });
                        return;
                    }
                    setPageData(true, bookListClasses);
                    getActivity().runOnUiThread(() -> {
                        list.loadMoreComplete();
                    });
                }).start();
            }
        });

        return view;
    }

    private void setPageData(boolean isFirstPage, List<BookListClass> data) {
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

    private List<BookListClass> getData() {
        try {
            String url = String.format("https://www.wenku8.net/modules/article/tags.php?t=%s&page=%d&v=%s", URLEncoder.encode(tag, "gbk"), ++pageindex, sort);
            return Wenku8Spider.parseNovelList(loginWenku8.getPageHtml(url));
        } catch (Exception e) {
            pageindex--;
            e.printStackTrace();
            return null;
        }
    }
}
