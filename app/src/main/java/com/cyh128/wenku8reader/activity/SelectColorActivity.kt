package com.cyh128.wenku8reader.activity

import android.app.UiModeManager
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.cyh128.wenku8reader.R
import com.cyh128.wenku8reader.newReader.ReaderActivity
import com.cyh128.wenku8reader.util.DatabaseHelper
import com.cyh128.wenku8reader.util.GlobalConfig
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.textfield.TextInputLayout
import com.ricky.color_picker.Util.color2Hex
import com.ricky.color_picker.api.OnColorSelectedListener
import com.ricky.color_picker.ui.ColorWheelPickerView
import java.util.regex.Pattern

class SelectColorActivity : AppCompatActivity() {
    private var colorWheelPickerSelectedColor: String? = null
    private var isNightMode: Boolean = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_select_color)
        val textEditText: EditText = findViewById(R.id.editText_act_select_color_text)
        val backgroundEditText: EditText = findViewById(R.id.editText_act_select_color_bg)
        val textTextInputLayout: TextInputLayout =
            findViewById(R.id.textInputLayout_act_select_color_text)
        val backgroundInputLayout: TextInputLayout =
            findViewById(R.id.textInputLayout_act_select_color_bg)
        val setTextColor: Button = findViewById(R.id.button_act_select_color_setTextColor)
        val setBgColor: Button = findViewById(R.id.button_act_select_color_setBgColor)
        val saveSetting: Button = findViewById(R.id.button_act_select_color_saveSetting)
        val discardSetting: Button = findViewById(R.id.button_act_select_color_discardSaveSetting)
        val view1: View = findViewById(R.id.layout_act_select_color_1)
        val tip: View = findViewById(R.id.view_act_select_color_tip)
        val colorTip: TextView = findViewById(R.id.text_act_select_color_colorTip)
        val colorWheelPickerView: ColorWheelPickerView = findViewById(R.id.colorWheelPickerView)
        view1.visibility = View.GONE
        val toolbar: MaterialToolbar = findViewById(R.id.toolbar_act_select_color)
        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener { v: View? ->
            // 退出当前页面
            finish()
        }
        val uiModeManager: UiModeManager = getSystemService(UI_MODE_SERVICE) as UiModeManager
        isNightMode = if (uiModeManager.nightMode == UiModeManager.MODE_NIGHT_YES) {
            backgroundEditText.setText(GlobalConfig.backgroundColorNight)
            textEditText.setText(GlobalConfig.textColorNight)
            toolbar.title = "选择颜色(深色主题)"
            findViewById<View>(R.id.cardView_act_select_color_recommendDay).visibility = View.GONE
            true
        } else {
            backgroundEditText.setText(GlobalConfig.backgroundColorDay)
            textEditText.setText(GlobalConfig.textColorDay)
            toolbar.title = "选择颜色(浅色主题)"
            findViewById<View>(R.id.cardView_act_select_color_recommendNight).visibility = View.GONE
            false
        }
        setTextColor.setOnClickListener { v: View? ->
            textEditText.setText(
                colorWheelPickerSelectedColor
            )
        }
        setBgColor.setOnClickListener { v: View? ->
            backgroundEditText.setText(
                colorWheelPickerSelectedColor
            )
        }
        colorWheelPickerView.setOnColorSelectedListener(object : OnColorSelectedListener {
            override fun onColorSelecting(color: Int) {}
            override fun onColorSelected(color: Int) {
                view1.visibility = View.VISIBLE
                colorWheelPickerSelectedColor = "#" + color2Hex(color, 255, true).substring(3)
                colorTip.text = colorWheelPickerSelectedColor
                tip.setBackgroundColor(color)
            }
        })
        saveSetting.setOnClickListener { v: View? ->
            val isMatch: Boolean = Pattern.matches(
                "^#([A-Fa-f0-9]{6}|[A-Fa-f0-9]{3})$",
                textEditText.text.toString()
            ) && Pattern.matches(
                "^#([A-Fa-f0-9]{6}|[A-Fa-f0-9]{3})$",
                backgroundEditText.text.toString()
            )
            if (!isMatch) {
                textTextInputLayout.error = "格式不匹配"
                backgroundInputLayout.error = "格式不匹配"
                return@setOnClickListener
            }
            textTextInputLayout.error = null
            backgroundInputLayout.error = null
            if (isNightMode) {
                GlobalConfig.textColorNight = textEditText.text.toString()
                GlobalConfig.backgroundColorNight = backgroundEditText.text.toString()
            } else {
                GlobalConfig.textColorDay = textEditText.text.toString()
                GlobalConfig.backgroundColorDay = backgroundEditText.text.toString()
            }
            ReaderActivity.readerActivity!!.setBackgroundAndTextColor()

            //数据库保存此设置
            DatabaseHelper.SaveReaderSetting()
            finish()
        }
        discardSetting.setOnClickListener { v: View? -> finish() }
    }
}
