package com.cyh128.wenku8reader.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.cyh128.wenku8reader.classLibrary.BookcaseClass;
import com.cyh128.wenku8reader.R;
import com.cyh128.wenku8reader.adapter.BookCaseAdapter;
import com.cyh128.wenku8reader.util.Wenku8Spider;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.util.ArrayList;
import java.util.List;

public class BookCaseFragment extends Fragment {
    private RecyclerView list;
    public static List<BookcaseClass> bookcaseList = new ArrayList<>();
    private View view;
    private LinearLayoutManager layoutManager;
    private SwipeRefreshLayout swipeRefreshLayout;
    private BookCaseAdapter bookCaseAdapter;
    private androidx.appcompat.widget.Toolbar toolbar;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_bookcase, container, false);
        list = view.findViewById(R.id.frag_bookcase_booklist);
        layoutManager = new LinearLayoutManager(getContext());
        swipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayout_frag_bookcase);
        swipeRefreshLayout.setRefreshing(true);
        swipeRefreshLayout.setOnRefreshListener(() -> { //设置下拉刷新
            new setBookcase().start();
        });

        toolbar = view.findViewById(R.id.toolbar_bookcase);
        toolbar.setOnMenuItemClickListener(item -> {
            if (item.getItemId() == R.id.update) {
                swipeRefreshLayout.setRefreshing(true);
                new setBookcase().start();
            }
            return true;
        });

        new setBookcase().start();

        return view;
    }

    private class setBookcase extends Thread {
        @Override
        public void run() {

            try {
                /*
                https://blog.csdn.net/momoliaoliao/article/details/49559953
                特别注意：想更新列表中的数据必须向下面这么写，不能直接novelList = Wenku8Spider.getBookcase()
                否则无效，导致显示不出页面。如果是一次性数据的话，就不需要了
                 */
                bookcaseList.clear();
                bookcaseList.addAll(Wenku8Spider.getBookcase());

            } catch (Exception e) {
                getActivity().runOnUiThread(() -> {
                    swipeRefreshLayout.setRefreshing(false);
                    new MaterialAlertDialogBuilder(view.getContext())
                            .setTitle("网络超时")
                            .setMessage("连接超时，可能是服务器出错了、也可能是网络卡慢或者您正在连接VPN或代理服务器，请稍后再试")
                            .setIcon(R.drawable.timeout)
                            .setCancelable(false)
                            .setPositiveButton("明白", null)
                            .show();
                });
                return;
            }

            Message msg = new Message();
            firstLaunchHandler.sendMessage(msg);
        }

        private final Handler firstLaunchHandler = new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(Message msg) {
                bookCaseAdapter = new BookCaseAdapter(getActivity(), bookcaseList);
                list.setAdapter(bookCaseAdapter);
                list.setLayoutManager(layoutManager);

                bookCaseAdapter.notifyDataSetChanged();
                swipeRefreshLayout.setRefreshing(false);
                return false;
            }
        });
    }
}
