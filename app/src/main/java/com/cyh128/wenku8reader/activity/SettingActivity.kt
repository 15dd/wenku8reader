package com.cyh128.wenku8reader.activity

import android.os.Bundle
import android.view.View
import android.widget.RadioButton
import androidx.appcompat.app.AppCompatActivity
import com.cyh128.wenku8reader.R
import com.cyh128.wenku8reader.util.DatabaseHelper
import com.cyh128.wenku8reader.util.GlobalConfig
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.materialswitch.MaterialSwitch

class SettingActivity : AppCompatActivity() {
    private lateinit var checkUpdate: MaterialSwitch
    private lateinit var newReader: RadioButton
    private lateinit var oldReader: RadioButton
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_setting)
        checkUpdate = findViewById(R.id.switch_check_update)
        newReader = findViewById(R.id.radiobutton_new_reader)
        oldReader = findViewById(R.id.radiobutton_old_reader)
        if (GlobalConfig.readerMode == 1) {
            newReader.isChecked = true
        } else {
            oldReader.isChecked = true
        }
        checkUpdate.isChecked = GlobalConfig.checkUpdate
        val toolbar = findViewById<MaterialToolbar>(R.id.toolbar_act_setting)
        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener { v: View? ->
            // 退出当前页面
            finish()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        GlobalConfig.checkUpdate = checkUpdate.isChecked
        GlobalConfig.readerMode = if (newReader.isChecked) 1 else 2
        DatabaseHelper.SaveSetting()
    }
}
