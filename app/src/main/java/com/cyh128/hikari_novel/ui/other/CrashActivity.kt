package com.cyh128.hikari_novel.ui.other

import android.os.Bundle
import com.cyh128.hikari_novel.base.BaseActivity
import com.cyh128.hikari_novel.databinding.ActivityCrashBinding
import com.cyh128.hikari_novel.ui.main.more.more.setting.SettingActivity
import com.cyh128.hikari_novel.util.openUrl
import com.cyh128.hikari_novel.util.startActivity
import com.developer.crashx.CrashActivity

class CrashActivity: BaseActivity<ActivityCrashBinding>() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding.tvACrashErrorMsg.text = CrashActivity.getAllErrorDetailsFromIntent(this,intent)
        binding.bACrashRestart.setOnClickListener {
            CrashActivity.restartApplication(this,CrashActivity.getConfigFromIntent(intent))
        }
        binding.bACrashReport.setOnClickListener {
            openUrl("https://github.com/15dd/wenku8reader/issues")
        }
        binding.bACrashSetting.setOnClickListener {
            startActivity<SettingActivity>()
        }
    }
}