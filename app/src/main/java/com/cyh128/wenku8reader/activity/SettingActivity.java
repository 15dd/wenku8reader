package com.cyh128.wenku8reader.activity;

import android.content.ContentValues;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.cyh128.wenku8reader.R;
import com.cyh128.wenku8reader.util.VarTemp;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.materialswitch.MaterialSwitch;

public class SettingActivity extends AppCompatActivity {

    private MaterialSwitch checkUpdate;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        checkUpdate = findViewById(R.id.switch_check_update);

        checkUpdate.setChecked(VarTemp.checkUpdate);

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

        VarTemp.checkUpdate = checkUpdate.isChecked();

        ContentValues values = new ContentValues();
        values.put("_id", 1);
        values.put("fontSize", VarTemp.readerFontSize);
        values.put("lineSpacing",VarTemp.readerLineSpacing);
        values.put("checkUpdate", checkUpdate.isChecked());
        values.put("bookcaseViewType",VarTemp.bookcaseViewType);
        VarTemp.db.replace("setting", null, values);
    }
}
