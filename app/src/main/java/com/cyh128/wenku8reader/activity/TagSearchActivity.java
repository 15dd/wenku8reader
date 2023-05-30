package com.cyh128.wenku8reader.activity;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
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
import com.cyh128.wenku8reader.fragment.SelectTipFragment;
import com.cyh128.wenku8reader.util.NavbarStatusbarInit;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

public class TagSearchActivity extends AppCompatActivity {
    private ViewPager2 viewPager2;
    private TabLayout tabLayout;
    private String tag;
    private androidx.appcompat.widget.Toolbar toolbar;
    private View bottomSheetView;
    private BottomSheetDialog bottomSheetDialog;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.tag_search_appbar_top_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        NavbarStatusbarInit.allTransparent(this);
        setContentView(R.layout.activity_tag_search);
        viewPager2 = findViewById(R.id.fragment_act_tag_search);
        tabLayout = findViewById(R.id.tabLayout_act_tag_search);
        toolbar = findViewById(R.id.toolbar_act_tag_search);
        tabLayout.setVisibility(View.GONE);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(v -> {
            // 退出当前页面
            finish();
        });

        bottomSheetDialog = new BottomSheetDialog(this);
        bottomSheetView = LayoutInflater.from(TagSearchActivity.this).inflate(R.layout.bottom_sheet_act_tag_search, null);
        bottomSheetDialog.setContentView(bottomSheetView);
        bottomSheetDialog.setDismissWithAnimation(true);

        FirstAdapter adapter = new FirstAdapter(this);//显示提示用户选择tag的fragment
        viewPager2.setAdapter(adapter);

        chipClickListener();
        toolbar.setOnMenuItemClickListener(item -> {
            if (item.getItemId() == R.id.btn_showTag) {
                //以下三行代码是为了每次在show()的时候，自动全部展开
                BottomSheetBehavior bottomSheetBehavior = BottomSheetBehavior.from((View)bottomSheetView.getParent());
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                bottomSheetBehavior.setSkipCollapsed(true);//收起时跳过折叠状态

                bottomSheetDialog.show();
                return true;
            }
            return false;
        });

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

    class FirstAdapter extends FragmentStateAdapter{ //第一次进入该activity时，显示selecttipfragment，提示用户选择tag

        public FirstAdapter(@NonNull FragmentActivity fragmentActivity) {
            super(fragmentActivity);
        }

        @NonNull
        @Override
        public Fragment createFragment(int position) {
            return new SelectTipFragment();
        }

        @Override
        public int getItemCount() {
            return 1;
        }
    }

    private void chipClickListener() {

        bottomSheetView.findViewById(R.id.chip_tag_xy).setOnClickListener(v -> {
            tag = "校园";
            viewPageInit();
            toolbar.setTitle("校园");
            bottomSheetDialog.dismiss();
        });
        bottomSheetView.findViewById(R.id.chip_tag_qc).setOnClickListener(v -> {
            tag = "青春";
            viewPageInit();
            toolbar.setTitle("青春");
            bottomSheetDialog.dismiss();
        });
        bottomSheetView.findViewById(R.id.chip_tag_la).setOnClickListener(v -> {
            tag = "恋爱";
            viewPageInit();
            toolbar.setTitle("恋爱");
            bottomSheetDialog.dismiss();
        });
        bottomSheetView.findViewById(R.id.chip_tag_zy).setOnClickListener(v -> {
            tag = "治愈";
            viewPageInit();
            toolbar.setTitle("治愈");
            bottomSheetDialog.dismiss();
        });
        bottomSheetView.findViewById(R.id.chip_tag_qx).setOnClickListener(v -> {
            tag = "群像";
            viewPageInit();
            toolbar.setTitle("群像");
            bottomSheetDialog.dismiss();
        });
        bottomSheetView.findViewById(R.id.chip_tag_jj).setOnClickListener(v -> {
            tag = "竞技";
            viewPageInit();
            toolbar.setTitle("竞技");
            bottomSheetDialog.dismiss();
        });
        bottomSheetView.findViewById(R.id.chip_tag_yy).setOnClickListener(v -> {
            tag = "音乐";
            viewPageInit();
            toolbar.setTitle("音乐");
            bottomSheetDialog.dismiss();
        });
        bottomSheetView.findViewById(R.id.chip_tag_ms).setOnClickListener(v -> {
            tag = "美食";
            viewPageInit();
            toolbar.setTitle("美食");
            bottomSheetDialog.dismiss();
        });
        bottomSheetView.findViewById(R.id.chip_tag_lx).setOnClickListener(v -> {
            tag = "旅行";
            viewPageInit();
            toolbar.setTitle("旅行");
            bottomSheetDialog.dismiss();
        });
        bottomSheetView.findViewById(R.id.chip_tag_hlx).setOnClickListener(v -> {
            tag = "欢乐向";
            viewPageInit();
            toolbar.setTitle("欢乐向");
            bottomSheetDialog.dismiss();
        });
        bottomSheetView.findViewById(R.id.chip_tag_jy).setOnClickListener(v -> {
            tag = "经营";
            viewPageInit();
            toolbar.setTitle("经营");
            bottomSheetDialog.dismiss();
        });
        bottomSheetView.findViewById(R.id.chip_tag_zc).setOnClickListener(v -> {
            tag = "职场";
            viewPageInit();
            toolbar.setTitle("职场");
            bottomSheetDialog.dismiss();
        });
        bottomSheetView.findViewById(R.id.chip_tag_dz).setOnClickListener(v -> {
            tag = "斗智";
            viewPageInit();
            toolbar.setTitle("斗智");
            bottomSheetDialog.dismiss();
        });
        bottomSheetView.findViewById(R.id.chip_tag_nd).setOnClickListener(v -> {
            tag = "脑洞";
            viewPageInit();
            toolbar.setTitle("脑洞");
            bottomSheetDialog.dismiss();
        });
        bottomSheetView.findViewById(R.id.chip_tag_zwh).setOnClickListener(v -> {
            tag = "宅文化";
            viewPageInit();
            toolbar.setTitle("宅文化");
            bottomSheetDialog.dismiss();
        });
        bottomSheetView.findViewById(R.id.chip_tag_cy).setOnClickListener(v -> {
            tag = "穿越";
            viewPageInit();
            toolbar.setTitle("穿越");
            bottomSheetDialog.dismiss();
        });
        bottomSheetView.findViewById(R.id.chip_tag_qh).setOnClickListener(v -> {
            tag = "奇幻";
            viewPageInit();
            toolbar.setTitle("奇幻");
            bottomSheetDialog.dismiss();
        });
        bottomSheetView.findViewById(R.id.chip_tag_mf).setOnClickListener(v -> {
            tag = "魔法";
            viewPageInit();
            toolbar.setTitle("魔法");
            bottomSheetDialog.dismiss();
        });
        bottomSheetView.findViewById(R.id.chip_tag_yn).setOnClickListener(v -> {
            tag = "异能";
            viewPageInit();
            toolbar.setTitle("异能");
            bottomSheetDialog.dismiss();
        });
        bottomSheetView.findViewById(R.id.chip_tag_zd).setOnClickListener(v -> {
            tag = "战斗";
            viewPageInit();
            toolbar.setTitle("战斗");
            bottomSheetDialog.dismiss();
        });
        bottomSheetView.findViewById(R.id.chip_tag_kh).setOnClickListener(v -> {
            tag = "科幻";
            viewPageInit();
            toolbar.setTitle("科幻");
            bottomSheetDialog.dismiss();
        });
        bottomSheetView.findViewById(R.id.chip_tag_jz).setOnClickListener(v -> {
            tag = "机战";
            viewPageInit();
            toolbar.setTitle("机战");
            bottomSheetDialog.dismiss();
        });
        bottomSheetView.findViewById(R.id.chip_tag_zz).setOnClickListener(v -> {
            tag = "战争";
            viewPageInit();
            toolbar.setTitle("战争");
            bottomSheetDialog.dismiss();
        });
        bottomSheetView.findViewById(R.id.chip_tag_mx).setOnClickListener(v -> {
            tag = "冒险";
            viewPageInit();
            toolbar.setTitle("冒险");
            bottomSheetDialog.dismiss();
        });
        bottomSheetView.findViewById(R.id.chip_tag_lat).setOnClickListener(v -> {
            tag = "龙傲天";
            viewPageInit();
            toolbar.setTitle("龙傲天");
            bottomSheetDialog.dismiss();
        });
        bottomSheetView.findViewById(R.id.chip_tag_xy2).setOnClickListener(v -> {
            tag = "悬疑";
            viewPageInit();
            toolbar.setTitle("悬疑");
            bottomSheetDialog.dismiss();
        });
        bottomSheetView.findViewById(R.id.chip_tag_fz).setOnClickListener(v -> {
            tag = "犯罪";
            viewPageInit();
            toolbar.setTitle("犯罪");
            bottomSheetDialog.dismiss();
        });
        bottomSheetView.findViewById(R.id.chip_tag_fc).setOnClickListener(v -> {
            tag = "复仇";
            viewPageInit();
            toolbar.setTitle("复仇");
            bottomSheetDialog.dismiss();
        });
        bottomSheetView.findViewById(R.id.chip_tag_ha).setOnClickListener(v -> {
            tag = "黑暗";
            viewPageInit();
            toolbar.setTitle("黑暗");
            bottomSheetDialog.dismiss();
        });
        bottomSheetView.findViewById(R.id.chip_tag_lq).setOnClickListener(v -> {
            tag = "猎奇";
            viewPageInit();
            toolbar.setTitle("猎奇");
            bottomSheetDialog.dismiss();
        });
        bottomSheetView.findViewById(R.id.chip_tag_js).setOnClickListener(v -> {
            tag = "惊悚";
            viewPageInit();
            toolbar.setTitle("惊悚");
            bottomSheetDialog.dismiss();
        });
        bottomSheetView.findViewById(R.id.chip_tag_jd).setOnClickListener(v -> {
            tag = "间谍";
            viewPageInit();
            toolbar.setTitle("间谍");
            bottomSheetDialog.dismiss();
        });
        bottomSheetView.findViewById(R.id.chip_tag_mr).setOnClickListener(v -> {
            tag = "末日";
            viewPageInit();
            toolbar.setTitle("末日");
            bottomSheetDialog.dismiss();
        });
        bottomSheetView.findViewById(R.id.chip_tag_yx).setOnClickListener(v -> {
            tag = "游戏";
            viewPageInit();
            toolbar.setTitle("游戏");
            bottomSheetDialog.dismiss();
        });
        bottomSheetView.findViewById(R.id.chip_tag_dts).setOnClickListener(v -> {
            tag = "大逃杀";
            viewPageInit();
            toolbar.setTitle("大逃杀");
            bottomSheetDialog.dismiss();
        });
        bottomSheetView.findViewById(R.id.chip_tag_qmzm).setOnClickListener(v -> {
            tag = "青梅竹马";
            viewPageInit();
            toolbar.setTitle("青梅竹马");
            bottomSheetDialog.dismiss();
        });
        bottomSheetView.findViewById(R.id.chip_tag_mm).setOnClickListener(v -> {
            tag = "妹妹";
            viewPageInit();
            toolbar.setTitle("妹妹");
            bottomSheetDialog.dismiss();
        });
        bottomSheetView.findViewById(R.id.chip_tag_ne).setOnClickListener(v -> {
            tag = "女儿";
            viewPageInit();
            toolbar.setTitle("女儿");
            bottomSheetDialog.dismiss();
        });
        bottomSheetView.findViewById(R.id.chip_tag_jk).setOnClickListener(v -> {
            tag = "JK";
            viewPageInit();
            toolbar.setTitle("JK");
            bottomSheetDialog.dismiss();
        });
        bottomSheetView.findViewById(R.id.chip_tag_jc).setOnClickListener(v -> {
            tag = "JC";
            viewPageInit();
            toolbar.setTitle("JC");
            bottomSheetDialog.dismiss();
        });
        bottomSheetView.findViewById(R.id.chip_tag_dxj).setOnClickListener(v -> {
            tag = "大小姐";
            viewPageInit();
            toolbar.setTitle("大小姐");
            bottomSheetDialog.dismiss();
        });
        bottomSheetView.findViewById(R.id.chip_tag_xz).setOnClickListener(v -> {
            tag = "性转";
            viewPageInit();
            toolbar.setTitle("性转");
            bottomSheetDialog.dismiss();
        });
        bottomSheetView.findViewById(R.id.chip_tag_wn).setOnClickListener(v -> {
            tag = "伪娘";
            viewPageInit();
            toolbar.setTitle("伪娘");
            bottomSheetDialog.dismiss();
        });
        bottomSheetView.findViewById(R.id.chip_tag_rw).setOnClickListener(v -> {
            tag = "人外";
            viewPageInit();
            toolbar.setTitle("人外");
            bottomSheetDialog.dismiss();
        });
        bottomSheetView.findViewById(R.id.chip_tag_hg).setOnClickListener(v -> {
            tag = "后宫";
            viewPageInit();
            toolbar.setTitle("后宫");
            bottomSheetDialog.dismiss();
        });
        bottomSheetView.findViewById(R.id.chip_tag_bh).setOnClickListener(v -> {
            tag = "百合";
            viewPageInit();
            toolbar.setTitle("百合");
            bottomSheetDialog.dismiss();
        });
        bottomSheetView.findViewById(R.id.chip_tag_dm).setOnClickListener(v -> {
            tag = "耽美";
            viewPageInit();
            toolbar.setTitle("耽美");
            bottomSheetDialog.dismiss();
        });
        bottomSheetView.findViewById(R.id.chip_tag_ntr).setOnClickListener(v -> {
            tag = "NTR";
            viewPageInit();
            toolbar.setTitle("NTR");
            bottomSheetDialog.dismiss();
        });
        bottomSheetView.findViewById(R.id.chip_tag_nxsj).setOnClickListener(v -> {
            tag = "女性视角";
            viewPageInit();
            toolbar.setTitle("女性视角");
            bottomSheetDialog.dismiss();
        });
    }
}
