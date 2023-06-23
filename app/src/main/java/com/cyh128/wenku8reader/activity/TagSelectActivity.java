package com.cyh128.wenku8reader.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.cyh128.wenku8reader.R;
import com.cyh128.wenku8reader.util.NavbarStatusbarInit;
import com.google.android.material.appbar.MaterialToolbar;

public class TagSelectActivity extends AppCompatActivity {
    private String tag;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        NavbarStatusbarInit.allTransparent(this);
        setContentView(R.layout.activity_tag_select);
        MaterialToolbar toolbar = findViewById(R.id.toolbar_act_tag_select);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(v -> {
            // 退出当前页面
            finish();
        });
        chipClickListener();
    }

    private void chipClickListener() {
        findViewById(R.id.chip_tag_xy).setOnClickListener(v -> {
            tag = "校园";
            Intent intent = new Intent(this, TagSearchActivity.class);
            intent.putExtra("tag",tag);
            startActivity(intent);
        });
        findViewById(R.id.chip_tag_qc).setOnClickListener(v -> {
            tag = "青春";
            Intent intent = new Intent(this, TagSearchActivity.class);
            intent.putExtra("tag",tag);
            startActivity(intent);
        });
        findViewById(R.id.chip_tag_la).setOnClickListener(v -> {
            tag = "恋爱";
            Intent intent = new Intent(this, TagSearchActivity.class);
            intent.putExtra("tag",tag);
            startActivity(intent);
        });
        findViewById(R.id.chip_tag_zy).setOnClickListener(v -> {
            tag = "治愈";
            Intent intent = new Intent(this, TagSearchActivity.class);
            intent.putExtra("tag",tag);
            startActivity(intent);
        });
        findViewById(R.id.chip_tag_qx).setOnClickListener(v -> {
            tag = "群像";
            Intent intent = new Intent(this, TagSearchActivity.class);
            intent.putExtra("tag",tag);
            startActivity(intent);
        });
        findViewById(R.id.chip_tag_jj).setOnClickListener(v -> {
            tag = "竞技";
            Intent intent = new Intent(this, TagSearchActivity.class);
            intent.putExtra("tag",tag);
            startActivity(intent);
        });
        findViewById(R.id.chip_tag_yy).setOnClickListener(v -> {
            tag = "音乐";
            Intent intent = new Intent(this, TagSearchActivity.class);
            intent.putExtra("tag",tag);
            startActivity(intent);
        });
        findViewById(R.id.chip_tag_ms).setOnClickListener(v -> {
            tag = "美食";
            Intent intent = new Intent(this, TagSearchActivity.class);
            intent.putExtra("tag",tag);
            startActivity(intent);
        });
        findViewById(R.id.chip_tag_lx).setOnClickListener(v -> {
            tag = "旅行";
            Intent intent = new Intent(this, TagSearchActivity.class);
            intent.putExtra("tag",tag);
            startActivity(intent);
        });
        findViewById(R.id.chip_tag_hlx).setOnClickListener(v -> {
            tag = "欢乐向";
            Intent intent = new Intent(this, TagSearchActivity.class);
            intent.putExtra("tag",tag);
            startActivity(intent);
        });
        findViewById(R.id.chip_tag_jy).setOnClickListener(v -> {
            tag = "经营";
            Intent intent = new Intent(this, TagSearchActivity.class);
            intent.putExtra("tag",tag);
            startActivity(intent);
        });
        findViewById(R.id.chip_tag_zc).setOnClickListener(v -> {
            tag = "职场";
            Intent intent = new Intent(this, TagSearchActivity.class);
            intent.putExtra("tag",tag);
            startActivity(intent);
        });
        findViewById(R.id.chip_tag_dz).setOnClickListener(v -> {
            tag = "斗智";
            Intent intent = new Intent(this, TagSearchActivity.class);
            intent.putExtra("tag",tag);
            startActivity(intent);
        });
        findViewById(R.id.chip_tag_nd).setOnClickListener(v -> {
            tag = "脑洞";
            Intent intent = new Intent(this, TagSearchActivity.class);
            intent.putExtra("tag",tag);
            startActivity(intent);
        });
        findViewById(R.id.chip_tag_zwh).setOnClickListener(v -> {
            tag = "宅文化";
            Intent intent = new Intent(this, TagSearchActivity.class);
            intent.putExtra("tag",tag);
            startActivity(intent);
        });
        findViewById(R.id.chip_tag_cy).setOnClickListener(v -> {
            tag = "穿越";
            Intent intent = new Intent(this, TagSearchActivity.class);
            intent.putExtra("tag",tag);
            startActivity(intent);
        });
        findViewById(R.id.chip_tag_qh).setOnClickListener(v -> {
            tag = "奇幻";
            Intent intent = new Intent(this, TagSearchActivity.class);
            intent.putExtra("tag",tag);
            startActivity(intent);
        });
        findViewById(R.id.chip_tag_mf).setOnClickListener(v -> {
            tag = "魔法";
            Intent intent = new Intent(this, TagSearchActivity.class);
            intent.putExtra("tag",tag);
            startActivity(intent);
        });
        findViewById(R.id.chip_tag_yn).setOnClickListener(v -> {
            tag = "异能";
            Intent intent = new Intent(this, TagSearchActivity.class);
            intent.putExtra("tag",tag);
            startActivity(intent);
        });
        findViewById(R.id.chip_tag_zd).setOnClickListener(v -> {
            tag = "战斗";
            Intent intent = new Intent(this, TagSearchActivity.class);
            intent.putExtra("tag",tag);
            startActivity(intent);
        });
        findViewById(R.id.chip_tag_kh).setOnClickListener(v -> {
            tag = "科幻";
            Intent intent = new Intent(this, TagSearchActivity.class);
            intent.putExtra("tag",tag);
            startActivity(intent);
        });
        findViewById(R.id.chip_tag_jz).setOnClickListener(v -> {
            tag = "机战";
            Intent intent = new Intent(this, TagSearchActivity.class);
            intent.putExtra("tag",tag);
            startActivity(intent);
        });
        findViewById(R.id.chip_tag_zz).setOnClickListener(v -> {
            tag = "战争";
            Intent intent = new Intent(this, TagSearchActivity.class);
            intent.putExtra("tag",tag);
            startActivity(intent);
        });
        findViewById(R.id.chip_tag_mx).setOnClickListener(v -> {
            tag = "冒险";
            Intent intent = new Intent(this, TagSearchActivity.class);
            intent.putExtra("tag",tag);
            startActivity(intent);
        });
        findViewById(R.id.chip_tag_lat).setOnClickListener(v -> {
            tag = "龙傲天";
            Intent intent = new Intent(this, TagSearchActivity.class);
            intent.putExtra("tag",tag);
            startActivity(intent);
        });
        findViewById(R.id.chip_tag_xy2).setOnClickListener(v -> {
            tag = "悬疑";
            Intent intent = new Intent(this, TagSearchActivity.class);
            intent.putExtra("tag",tag);
            startActivity(intent);
        });
        findViewById(R.id.chip_tag_fz).setOnClickListener(v -> {
            tag = "犯罪";
            Intent intent = new Intent(this, TagSearchActivity.class);
            intent.putExtra("tag",tag);
            startActivity(intent);
        });
        findViewById(R.id.chip_tag_fc).setOnClickListener(v -> {
            tag = "复仇";
            Intent intent = new Intent(this, TagSearchActivity.class);
            intent.putExtra("tag",tag);
            startActivity(intent);
        });
        findViewById(R.id.chip_tag_ha).setOnClickListener(v -> {
            tag = "黑暗";
            Intent intent = new Intent(this, TagSearchActivity.class);
            intent.putExtra("tag",tag);
            startActivity(intent);
        });
        findViewById(R.id.chip_tag_lq).setOnClickListener(v -> {
            tag = "猎奇";
            Intent intent = new Intent(this, TagSearchActivity.class);
            intent.putExtra("tag",tag);
            startActivity(intent);
        });
        findViewById(R.id.chip_tag_js).setOnClickListener(v -> {
            tag = "惊悚";
            Intent intent = new Intent(this, TagSearchActivity.class);
            intent.putExtra("tag",tag);
            startActivity(intent);
        });
        findViewById(R.id.chip_tag_jd).setOnClickListener(v -> {
            tag = "间谍";
            Intent intent = new Intent(this, TagSearchActivity.class);
            intent.putExtra("tag",tag);
            startActivity(intent);
        });
        findViewById(R.id.chip_tag_mr).setOnClickListener(v -> {
            tag = "末日";
            Intent intent = new Intent(this, TagSearchActivity.class);
            intent.putExtra("tag",tag);
            startActivity(intent);
        });
        findViewById(R.id.chip_tag_yx).setOnClickListener(v -> {
            tag = "游戏";
            Intent intent = new Intent(this, TagSearchActivity.class);
            intent.putExtra("tag",tag);
            startActivity(intent);
        });
        findViewById(R.id.chip_tag_dts).setOnClickListener(v -> {
            tag = "大逃杀";
            Intent intent = new Intent(this, TagSearchActivity.class);
            intent.putExtra("tag",tag);
            startActivity(intent);
        });
        findViewById(R.id.chip_tag_qmzm).setOnClickListener(v -> {
            tag = "青梅竹马";
            Intent intent = new Intent(this, TagSearchActivity.class);
            intent.putExtra("tag",tag);
            startActivity(intent);
        });
        findViewById(R.id.chip_tag_mm).setOnClickListener(v -> {
            tag = "妹妹";
            Intent intent = new Intent(this, TagSearchActivity.class);
            intent.putExtra("tag",tag);
            startActivity(intent);
        });
        findViewById(R.id.chip_tag_ne).setOnClickListener(v -> {
            tag = "女儿";
            Intent intent = new Intent(this, TagSearchActivity.class);
            intent.putExtra("tag",tag);
            startActivity(intent);
        });
        findViewById(R.id.chip_tag_jk).setOnClickListener(v -> {
            tag = "JK";
            Intent intent = new Intent(this, TagSearchActivity.class);
            intent.putExtra("tag",tag);
            startActivity(intent);
        });
        findViewById(R.id.chip_tag_jc).setOnClickListener(v -> {
            tag = "JC";
            Intent intent = new Intent(this, TagSearchActivity.class);
            intent.putExtra("tag",tag);
            startActivity(intent);
        });
        findViewById(R.id.chip_tag_dxj).setOnClickListener(v -> {
            tag = "大小姐";
            Intent intent = new Intent(this, TagSearchActivity.class);
            intent.putExtra("tag",tag);
            startActivity(intent);
        });
        findViewById(R.id.chip_tag_xz).setOnClickListener(v -> {
            tag = "性转";
            Intent intent = new Intent(this, TagSearchActivity.class);
            intent.putExtra("tag",tag);
            startActivity(intent);
        });
        findViewById(R.id.chip_tag_wn).setOnClickListener(v -> {
            tag = "伪娘";
            Intent intent = new Intent(this, TagSearchActivity.class);
            intent.putExtra("tag",tag);
            startActivity(intent);
        });
        findViewById(R.id.chip_tag_rw).setOnClickListener(v -> {
            tag = "人外";
            Intent intent = new Intent(this, TagSearchActivity.class);
            intent.putExtra("tag",tag);
            startActivity(intent);
        });
        findViewById(R.id.chip_tag_hg).setOnClickListener(v -> {
            tag = "后宫";
            Intent intent = new Intent(this, TagSearchActivity.class);
            intent.putExtra("tag",tag);
            startActivity(intent);
        });
        findViewById(R.id.chip_tag_bh).setOnClickListener(v -> {
            tag = "百合";
            Intent intent = new Intent(this, TagSearchActivity.class);
            intent.putExtra("tag",tag);
            startActivity(intent);
        });
        findViewById(R.id.chip_tag_dm).setOnClickListener(v -> {
            tag = "耽美";
            Intent intent = new Intent(this, TagSearchActivity.class);
            intent.putExtra("tag",tag);
            startActivity(intent);
        });
        findViewById(R.id.chip_tag_ntr).setOnClickListener(v -> {
            tag = "NTR";
            Intent intent = new Intent(this, TagSearchActivity.class);
            intent.putExtra("tag",tag);
            startActivity(intent);
        });
        findViewById(R.id.chip_tag_nxsj).setOnClickListener(v -> {
            tag = "女性视角";
            Intent intent = new Intent(this, TagSearchActivity.class);
            intent.putExtra("tag",tag);
            startActivity(intent);
        });
    }
}
