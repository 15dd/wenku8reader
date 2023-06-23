package com.cyh128.wenku8reader.activity;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import com.cyh128.wenku8reader.fragment.TagSearchFragment;
import com.cyh128.wenku8reader.R;
import com.cyh128.wenku8reader.util.NavbarStatusbarInit;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

public class TagSearchActivity extends AppCompatActivity {
    private ViewPager2 viewPager2;
    private TabLayout tabLayout;
    private String tag;
    private androidx.appcompat.widget.Toolbar toolbar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        NavbarStatusbarInit.allTransparent(this);
        setContentView(R.layout.activity_tag_search);
        viewPager2 = findViewById(R.id.fragment_act_tag_search);
        tabLayout = findViewById(R.id.tabLayout_act_tag_search);
        toolbar = findViewById(R.id.toolbar_act_tag_search);
        tag = getIntent().getStringExtra("tag");
        toolbar.setTitle(tag);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(v -> {
            // 退出当前页面
            finish();
        });

        viewPageInit();
    }

    private void viewPageInit() {
        //https://blog.csdn.net/qq_45866344/article/details/115128445
        ViewPager2Adapter adapter = new ViewPager2Adapter(this);
        viewPager2.setAdapter(adapter);
        tabLayout.setVisibility(View.VISIBLE);
        TabLayoutMediator tabLayoutMediator = new TabLayoutMediator(tabLayout, viewPager2, new TabLayoutMediator.TabConfigurationStrategy() {
            @Override
            public void onConfigureTab(@NonNull TabLayout.Tab tab, int position) {
                switch (position) {
                    case 0:
                        tab.setText("按更新查看");
                        break;
                    case 1:
                        tab.setText("按热门查看");
                        break;
                    case 2:
                        tab.setText("只看已完结");
                        break;
                    case 3:
                        tab.setText("只看动画化");
                        break;
                }
            }
        });
        tabLayoutMediator.attach();
    }

    class ViewPager2Adapter extends FragmentStateAdapter {
        public ViewPager2Adapter(@NonNull FragmentActivity fragmentActivity) {
            super(fragmentActivity);
        }

        @NonNull
        @Override
        public Fragment createFragment(int position) {
            switch (position) {
                case 0 -> {
                    TagSearchFragment v0 = new TagSearchFragment();
                    Bundle bundle = new Bundle();
                    bundle.putString("tag", tag);
                    bundle.putString("sort", "0");
                    v0.setArguments(bundle);
                    return v0;
                }
                case 1 -> {
                    TagSearchFragment v1 = new TagSearchFragment();
                    Bundle bundle = new Bundle();
                    bundle.putString("tag", tag);
                    bundle.putString("sort", "1");
                    v1.setArguments(bundle);
                    return v1;
                }
                case 2 -> {
                    TagSearchFragment v2 = new TagSearchFragment();
                    Bundle bundle = new Bundle();
                    bundle.putString("tag", tag);
                    bundle.putString("sort", "2");
                    v2.setArguments(bundle);
                    return v2;
                }
                case 3 -> {
                    TagSearchFragment v3 = new TagSearchFragment();
                    Bundle bundle = new Bundle();
                    bundle.putString("tag", tag);
                    bundle.putString("sort", "3");
                    v3.setArguments(bundle);
                    return v3;
                }
            }
            return null;
        }

        @Override
        public int getItemCount() {
            return 4;
        }
    }
}
