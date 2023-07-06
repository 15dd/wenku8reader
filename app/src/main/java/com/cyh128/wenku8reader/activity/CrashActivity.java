package com.cyh128.wenku8reader.activity;

import static com.developer.crashx.CrashActivity.getStackTraceFromIntent;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.cyh128.wenku8reader.R;

public class CrashActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crash);
        TextView details =  findViewById(R.id.errordetails);
        details.setText(getStackTraceFromIntent(getIntent()));
        Button report = findViewById(R.id.button_act_crash_report);
        report.setOnClickListener(v -> {
            Uri uri = Uri.parse("https://github.com/15dd/wenku8reader/issues");    //设置跳转的网站
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            startActivity(intent);
        });

        Button restart = findViewById(R.id.button_act_crash_restart);
        restart.setOnClickListener(v -> {
            final Intent intent = getPackageManager().getLaunchIntentForPackage(getPackageName());
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();
        });
    }
}
