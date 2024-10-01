package com.cyh128.hikarinovel.ui.view.other

import android.os.Bundle
import com.cyh128.hikarinovel.base.BaseActivity
import com.cyh128.hikarinovel.databinding.ActivityCrashBinding
import com.cyh128.hikarinovel.util.openUrl
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
    }
}