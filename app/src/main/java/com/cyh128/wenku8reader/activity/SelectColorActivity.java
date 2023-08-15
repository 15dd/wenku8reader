package com.cyh128.wenku8reader.activity;

import android.app.UiModeManager;
import android.content.ContentValues;
import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.cyh128.wenku8reader.R;
import com.cyh128.wenku8reader.util.GlobalConfig;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.textfield.TextInputLayout;
import com.ricky.color_picker.Util;
import com.ricky.color_picker.api.OnColorSelectedListener;
import com.ricky.color_picker.ui.ColorWheelPickerView;

import java.util.regex.Pattern;

public class SelectColorActivity extends AppCompatActivity {
    private String colorWheelPickerSelectedColor;
    private boolean isNightMode;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_color);
        EditText textEditText = findViewById(R.id.editText_act_select_color_text);
        EditText backgroundEditText = findViewById(R.id.editText_act_select_color_bg);
        TextInputLayout textTextInputLayout = findViewById(R.id.textInputLayout_act_select_color_text);
        TextInputLayout backgroundInputLayout = findViewById(R.id.textInputLayout_act_select_color_bg);
        Button setTextColor = findViewById(R.id.button_act_select_color_setTextColor);
        Button setBgColor = findViewById(R.id.button_act_select_color_setBgColor);
        Button saveSetting = findViewById(R.id.button_act_select_color_saveSetting);
        Button discardSetting = findViewById(R.id.button_act_select_color_discardSaveSetting);
        View view1 = findViewById(R.id.layout_act_select_color_1);
        View tip = findViewById(R.id.view_act_select_color_tip);
        TextView colorTip = findViewById(R.id.text_act_select_color_colorTip);
        ColorWheelPickerView colorWheelPickerView = findViewById(R.id.colorWheelPickerView);

        view1.setVisibility(View.GONE);

        MaterialToolbar toolbar = findViewById(R.id.toolbar_act_select_color);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(v -> {
            // 退出当前页面
            finish();
        });

        UiModeManager uiModeManager = (UiModeManager) getSystemService(Context.UI_MODE_SERVICE);
        if (uiModeManager.getNightMode() == UiModeManager.MODE_NIGHT_YES) {
            backgroundEditText.setText(GlobalConfig.backgroundColorNight);
            textEditText.setText(GlobalConfig.textColorNight);
            toolbar.setTitle("选择颜色(深色主题)");
            findViewById(R.id.cardView_act_select_color_recommendDay).setVisibility(View.GONE);
            isNightMode = true;
        } else {
            backgroundEditText.setText(GlobalConfig.backgroundColorDay);
            textEditText.setText(GlobalConfig.textColorDay);
            toolbar.setTitle("选择颜色(浅色主题)");
            findViewById(R.id.cardView_act_select_color_recommendNight).setVisibility(View.GONE);
            isNightMode = false;
        }

        setTextColor.setOnClickListener(v -> textEditText.setText(colorWheelPickerSelectedColor));
        setBgColor.setOnClickListener(v -> backgroundEditText.setText(colorWheelPickerSelectedColor));

        colorWheelPickerView.setOnColorSelectedListener(new OnColorSelectedListener() {
            @Override
            public void onColorSelecting(int color) {}
            @Override
            public void onColorSelected(int color) {
                view1.setVisibility(View.VISIBLE);
                colorWheelPickerSelectedColor = "#" + Util.INSTANCE.color2Hex(color,255,true).substring(3);
                colorTip.setText(colorWheelPickerSelectedColor);
                tip.setBackgroundColor(color);
            }
        });

        saveSetting.setOnClickListener(v -> {
            boolean isMatch = Pattern.matches("^#([A-Fa-f0-9]{6}|[A-Fa-f0-9]{3})$", textEditText.getText().toString()) && Pattern.matches("^#([A-Fa-f0-9]{6}|[A-Fa-f0-9]{3})$", backgroundEditText.getText().toString());
            if (!isMatch) {
                textTextInputLayout.setError("格式不匹配");
                backgroundInputLayout.setError("格式不匹配");
                return;
            }
            textTextInputLayout.setError(null);
            backgroundInputLayout.setError(null);
            if (isNightMode) {
                GlobalConfig.textColorNight = textEditText.getText().toString();
                GlobalConfig.backgroundColorNight = backgroundEditText.getText().toString();
            } else {
                GlobalConfig.textColorDay = textEditText.getText().toString();
                GlobalConfig.backgroundColorDay = backgroundEditText.getText().toString();
            }
            ReadActivity.readActivity.setBackgroundAndTextColor();

            //数据库保存此设置
            ContentValues values = new ContentValues();
            values.put("_id", 1);
            values.put("fontSize", GlobalConfig.readerFontSize);
            values.put("lineSpacing", GlobalConfig.readerLineSpacing);
            values.put("bottomTextSize", GlobalConfig.readerBottomTextSize);
            values.put("isUpToDown", GlobalConfig.isUpToDown);
            values.put("canSwitchChapterByScroll", GlobalConfig.canSwitchChapterByScroll);
            values.put("backgroundColorDay", GlobalConfig.backgroundColorDay);
            values.put("backgroundColorNight", GlobalConfig.backgroundColorNight);
            values.put("textColorDay", GlobalConfig.textColorDay);
            values.put("textColorNight", GlobalConfig.textColorNight);
            GlobalConfig.db.replace("reader", null, values);

            SelectColorActivity.this.finish();
        });

        discardSetting.setOnClickListener(v -> SelectColorActivity.this.finish());
    }
}
