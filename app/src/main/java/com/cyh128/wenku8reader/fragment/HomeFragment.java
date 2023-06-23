package com.cyh128.wenku8reader.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import com.cyh128.wenku8reader.activity.SearchActivity;
import com.cyh128.wenku8reader.activity.TagSearchActivity;
import com.cyh128.wenku8reader.R;
import com.cyh128.wenku8reader.activity.TagSelectActivity;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

public class HomeFragment extends Fragment {
    //private BookListFragment toplist, lastupdate, allvote, postdate, articlelist ,dayvisit,dayvote;
    private View view;
    private ViewPager2 viewPager2;
    private TabLayout tabLayout;
    private androidx.appcompat.widget.Toolbar toolbar;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_home, container, false);
        viewPager2 = view.findViewById(R.id.fragment_home_content);
//        viewPager2.setUserInputEnabled(false); //true:滑动，false：禁止滑动
        tabLayout = view.findViewById(R.id.tabLayout_home);
        tabLayout.setTabMode(TabLayout.MODE_SCROLLABLE);
        toolbar = view.findViewById(R.id.toolbar_home);
        toolbar.setOnMenuItemClickListener(item -> {
            if (item.getItemId() == R.id.btn_search) {
                Intent intent = new Intent(getActivity(), SearchActivity.class);
                startActivity(intent);
                return true;
            } else if (item.getItemId() == R.id.btn_tagSearch) {
                Intent intent = new Intent(getActivity(), TagSelectActivity.class);
                startActivity(intent);
                return true;
            }
            return false;
        });

        viewPageInit();
        return view;
    }


    private void viewPageInit() {
        //https://blog.csdn.net/qq_45866344/article/details/115128445
        ViewPager2Adapter adapter = new ViewPager2Adapter(this);
        viewPager2.setAdapter(adapter);
        TabLayoutMediator tabLayoutMediator = new TabLayoutMediator(tabLayout, viewPager2, new TabLayoutMediator.TabConfigurationStrategy() {
            @Override
            public void onConfigureTab(@NonNull TabLayout.Tab tab, int position) {
                switch (position) {
                    case 0:
                        tab.setText("总排行榜");
                        break;
                    case 1:
                        tab.setText("总推荐榜");
                        break;
                    case 2:
                        tab.setText("日排行榜");
                        break;
                    case 3:
                        tab.setText("日推荐榜");
                        break;
                    case 4:
                        tab.setText("最近更新");
                        break;
                    case 5:
                        tab.setText("最近入库");
                        break;
                    case 6:
                        tab.setText("全部");
                }
            }
        });
        tabLayoutMediator.attach();
    }

    class ViewPager2Adapter extends FragmentStateAdapter {
        public ViewPager2Adapter(@NonNull Fragment fragment) {
            super(fragment);
        }

        @NonNull
        @Override
        public Fragment createFragment(int position) {
            switch (position) {
                case 0 -> {
                    BookListFragment toplist = new BookListFragment();
                    Bundle bundle = new Bundle();
                    bundle.putString("type", "toplist");
                    toplist.setArguments(bundle);
                    return toplist;
                }
                case 1 -> {
                    BookListFragment allvote = new BookListFragment();
                    Bundle bundle = new Bundle();
                    bundle.putString("type", "allvote");
                    allvote.setArguments(bundle);
                    return allvote;
                }
                case 2 -> {
                    BookListFragment dayvisit = new BookListFragment();
                    Bundle bundle = new Bundle();
                    bundle.putString("type", "dayvisit");
                    dayvisit.setArguments(bundle);
                    return dayvisit;
                }
                case 3 -> {
                    BookListFragment dayvote = new BookListFragment();
                    Bundle bundle = new Bundle();
                    bundle.putString("type", "dayvote");
                    dayvote.setArguments(bundle);
                    return dayvote;
                }
                case 4 -> {
                    BookListFragment lastupdate = new BookListFragment();
                    Bundle bundle = new Bundle();
                    bundle.putString("type", "lastupdate");
                    lastupdate.setArguments(bundle);
                    return lastupdate;
                }
                case 5 -> {
                    BookListFragment postdate = new BookListFragment();
                    Bundle bundle = new Bundle();
                    bundle.putString("type", "postdate");
                    postdate.setArguments(bundle);
                    return postdate;
                }
                case 6 -> {
                    BookListFragment articlelist = new BookListFragment();
                    Bundle bundle = new Bundle();
                    bundle.putString("type", "articlelist");
                    articlelist.setArguments(bundle);
                    return articlelist;
                }
            }
            return null;
        }

        @Override
        public int getItemCount() {
            return 7;
        }
    }

}
