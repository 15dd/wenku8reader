package com.cyh128.wenku8reader.activity

import android.os.Bundle
import android.view.View
import android.widget.RadioButton
import android.widget.RadioGroup
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

        findViewById<RadioGroup>(R.id.rg_a_setting_domain).setOnCheckedChangeListener { group, checkedId ->
            when(checkedId) {
                R.id.radiobutton_new_reader_cc -> GlobalConfig.domain = "www.wenku8.cc"
                R.id.radiobutton_old_reader_net -> GlobalConfig.domain = "www.wenku8.net"
            }
        }

        if (GlobalConfig.domain == "www.wenku8.cc")
            findViewById<RadioButton>(R.id.radiobutton_new_reader_cc).isChecked = true
        else
            findViewById<RadioButton>(R.id.radiobutton_old_reader_net).isChecked = true
    }

    override fun onDestroy() {
        super.onDestroy()
        GlobalConfig.checkUpdate = checkUpdate.isChecked
        GlobalConfig.readerMode = if (newReader.isChecked) 1 else 2
        DatabaseHelper.SaveSetting()
    }
}
