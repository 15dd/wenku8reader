package com.cyh128.wenku8reader

import android.app.Application
import com.cyh128.wenku8reader.activity.CrashActivity
import com.cyh128.wenku8reader.util.GlobalConfig
import com.developer.crashx.config.CrashConfig
import com.google.android.material.color.DynamicColors

class App : Application() {
    override fun onCreate() {
        super.onCreate()
        DynamicColors.applyToActivitiesIfAvailable(this) //添加md动态颜色
        CrashConfig.Builder.create()
            .errorActivity(CrashActivity::class.java)
            .apply()
    }

    override fun onTerminate() {
        super.onTerminate()
        GlobalConfig.db.close()
    }
}
