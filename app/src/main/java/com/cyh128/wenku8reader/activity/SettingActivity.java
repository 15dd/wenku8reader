package com.cyh128.wenku8reader.activity;

import android.content.ContentValues;
import android.os.Bundle;
import android.widget.RadioButton;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.cyh128.wenku8reader.R;
import com.cyh128.wenku8reader.util.DatabaseHelper;
import com.cyh128.wenku8reader.util.GlobalConfig;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.materialswitch.MaterialSwitch;

public class SettingActivity extends AppCompatActivity {

    private MaterialSwitch checkUpdate;
    private RadioButton newReader;
    private RadioButton oldReader;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        checkUpdate = findViewById(R.id.switch_check_update);
        newReader = findViewById(R.id.radiobutton_new_reader);
        oldReader = findViewById(R.id.radiobutton_old_reader);

        if (GlobalConfig.readerMode == 1) {
            newReader.setChecked(true);
        } else {
            oldReader.setChecked(true);
        }
        checkUpdate.setChecked(GlobalConfig.checkUpdate);

        MaterialToolbar toolbar = findViewById(R.id.toolbar_act_setting);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(v -> {
            // 退出当前页面
            finish();
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        GlobalConfig.checkUpdate = checkUpdate.isChecked();
        GlobalConfig.readerMode = newReader.isChecked() ? 1 : 2;
        DatabaseHelper.SaveSetting();
    }
}
