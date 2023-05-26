package com.cyh128.wenku8reader.activity;

import android.content.ContentValues;
import android.os.Bundle;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.cyh128.wenku8reader.util.VarTemp;
import com.cyh128.wenku8reader.R;
import com.cyh128.wenku8reader.util.NavbarStatusbarInit;
import com.google.android.material.slider.Slider;

public class SettingActivity extends AppCompatActivity {
    private Slider fontSizeSlider,lineSpacingSlider;
    private TextView textSizePreview,lineSpacingPreview;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        NavbarStatusbarInit.allTransparent(SettingActivity.this);

        fontSizeSlider = findViewById(R.id.slider_act_setting);
        fontSizeSlider.setValue(VarTemp.readerFontSize);//设置为之前字体大小的值
        textSizePreview = findViewById(R.id.text_act_setting_textSizePreview);
        textSizePreview.setTextSize(VarTemp.readerFontSize);

        lineSpacingSlider = findViewById(R.id.slider_act_setting_lineSpacing);
        lineSpacingSlider.setValue(VarTemp.readerLineSpacing);
        lineSpacingPreview = findViewById(R.id.text_act_setting_lineSpacingPreview);
        lineSpacingPreview.setLineSpacing(VarTemp.readerLineSpacing,1f);

        androidx.appcompat.widget.Toolbar toolbar = findViewById(R.id.toolbar_act_setting);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(v -> {
            // 退出当前页面
            finish();
        });

        fontSizeSlider.addOnChangeListener(new Slider.OnChangeListener() {
            @Override
            public void onValueChange(@NonNull Slider slider, float value, boolean fromUser) {
                runOnUiThread(() -> {
                    textSizePreview.setTextSize(slider.getValue());
                });
            }
        });
        lineSpacingSlider.addOnChangeListener(new Slider.OnChangeListener() {
            @Override
            public void onValueChange(@NonNull Slider slider, float value, boolean fromUser) {
                runOnUiThread(() -> {
                    lineSpacingPreview.setLineSpacing(slider.getValue(),1f);
                });
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        VarTemp.readerFontSize = fontSizeSlider.getValue();
        VarTemp.readerLineSpacing = lineSpacingSlider.getValue();

        ContentValues values = new ContentValues();
        values.put("_id", 1);
        values.put("fontSize", fontSizeSlider.getValue());
        values.put("lineSpacing",lineSpacingSlider.getValue());
        VarTemp.db.replace("setting", null, values);
    }
}
