package com.cyh128.wenku8reader.fragment;

import android.content.ContentValues;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.cyh128.wenku8reader.R;
import com.cyh128.wenku8reader.adapter.BookCaseAdapter;
import com.cyh128.wenku8reader.bean.BookcaseBean;
import com.cyh128.wenku8reader.util.GlobalConfig;
import com.cyh128.wenku8reader.util.Wenku8Spider;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.util.ArrayList;
import java.util.List;

import me.jingbin.library.ByRecyclerView;

public class BookCaseFragment extends Fragment {
    private ByRecyclerView list;
    public static List<BookcaseBean> bookcaseList = new ArrayList<>();
    private View view;
    private BookCaseAdapter bookCaseAdapter;
    private androidx.appcompat.widget.Toolbar toolbar;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        view = inflater.inflate(R.layout.fragment_bookcase, container, false);
        list = view.findViewById(R.id.frag_bookcase_booklist);
        list.setLoadMoreEnabled(false);

        toolbar = view.findViewById(R.id.toolbar_bookcase);
        toolbar.setOnMenuItemClickListener(item -> {
            if (item.getItemId() == R.id.update) {
                new setBookcase().start();
            } else if (item.getItemId() == R.id.viewType) {
                GlobalConfig.bookcaseViewType = !GlobalConfig.bookcaseViewType ;
                changeLayout(GlobalConfig.bookcaseViewType);//更改视图类型
                //保存视图类型的设置，使下次启动自动使用当前视图类型================================
                ContentValues values = new ContentValues();
                values.put("_id", 1);
                values.put("checkUpdate", GlobalConfig.checkUpdate);
                values.put("bookcaseViewType", GlobalConfig.bookcaseViewType);
                GlobalConfig.db.replace("setting", null, values);
                //=========================================================================
            }
            return true;
        });

        list.setOnRefreshListener(new ByRecyclerView.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new setBookcase().start();
            }
        });

        new setBookcase().start();

        return view;
    }

    private void changeLayout(boolean isChange) {
        //布局切换方法
        //如果isChange == true 就调用瀑布流模式,反之调用列表模式
        //https://www.jianshu.com/p/f773ffb3d7e4
        if (isChange) {
            //瀑布流设置
            GridLayoutManager layoutManager = new GridLayoutManager(getContext(),3);
            list.setLayoutManager(layoutManager);
            bookCaseAdapter = new BookCaseAdapter(view.getContext(), bookcaseList, BookCaseAdapter.GRID);
            list.setAdapter(bookCaseAdapter);
            bookCaseAdapter.notifyDataSetChanged();
        } else {
            //列表模式
            LinearLayoutManager layoutManager = new LinearLayoutManager(view.getContext());
            layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
            list.setLayoutManager(layoutManager);
            bookCaseAdapter = new BookCaseAdapter(view.getContext(), bookcaseList, BookCaseAdapter.LINEAR);
            list.setAdapter(bookCaseAdapter);
            bookCaseAdapter.notifyDataSetChanged();
        }
    }

    private class setBookcase extends Thread {
        @Override
        public void run() {

            try {
                /*
                https://blog.csdn.net/momoliaoliao/article/details/49559953
                特别注意：想更新列表中的数据必须向下面这么写，不能直接novelList = Wenku8Spider.getBookcase()
                否则无效，导致显示不出页面。如果是一次性数据的话，就不需要这么做了
                 */
                bookcaseList.clear();
                bookcaseList.addAll(Wenku8Spider.getBookcase());

            } catch (Exception e) {
                getActivity().runOnUiThread(() -> {
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
                changeLayout(GlobalConfig.bookcaseViewType);
                toolbar.setTitle("书架(共" + bookCaseAdapter.getItemCount() + "本)");
                list.setRefreshing(false);
                return false;
            }
        });
    }
}
