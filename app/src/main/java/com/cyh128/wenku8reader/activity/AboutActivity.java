package com.cyh128.wenku8reader.activity;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.cyh128.wenku8reader.R;
import com.cyh128.wenku8reader.util.CheckUpdate;
import com.google.android.material.appbar.MaterialToolbar;

public class AboutActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        CardView checkUpdate = findViewById(R.id.cardView_act_about_checkUpdate);
        CardView goToGithub = findViewById(R.id.cardView_act_about_goToGithub);
        TextView version = findViewById(R.id.text_act_about_version);

        MaterialToolbar toolbar = findViewById(R.id.toolbar_act_about);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(v -> {
            // 退出当前页面
            finish();
        });

        try {
            PackageInfo packageInfo = getApplicationContext()
                    .getPackageManager()
                    .getPackageInfo(getPackageName(), 0);
            version.setText(packageInfo.versionName);
        } catch (Exception e) {
            version.setVisibility(View.INVISIBLE);
        }

        checkUpdate.setOnClickListener(v -> {
            Toast.makeText(AboutActivity.this,"正在检查更新",Toast.LENGTH_SHORT).show();
            new Thread(() -> {
                try {
                    CheckUpdate.checkUpdate(AboutActivity.this, CheckUpdate.Mode.WITH_TIP);
                } catch (Exception e) {
                    e.printStackTrace();
                    runOnUiThread(() -> Toast.makeText(AboutActivity.this, "检查更新失败", Toast.LENGTH_SHORT).show());
                }
            }).start();
        });

        goToGithub.setOnClickListener(v -> {
            Uri uri = Uri.parse("https://github.com/15dd/wenku8reader");    //设置跳转的网站
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            startActivity(intent);
        });
    }
}
