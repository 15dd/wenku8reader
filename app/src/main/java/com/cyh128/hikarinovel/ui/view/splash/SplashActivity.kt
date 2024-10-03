package com.cyh128.hikarinovel.ui.view.splash

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import com.cyh128.hikarinovel.R
import com.cyh128.hikarinovel.base.BaseActivity
import com.cyh128.hikarinovel.data.model.Event
import com.cyh128.hikarinovel.data.model.Language
import com.cyh128.hikarinovel.databinding.ActivitySplashBinding
import com.cyh128.hikarinovel.ui.view.main.MainActivity
import com.cyh128.hikarinovel.util.launchWithLifecycle
import com.cyh128.hikarinovel.util.startActivity
import com.yariksoffice.lingver.Lingver
import dagger.hilt.android.AndroidEntryPoint
import java.util.Locale

@AndroidEntryPoint
@SuppressLint("CustomSplashScreen")
class SplashActivity : BaseActivity<ActivitySplashBinding>() {
    private val viewModel by lazy { ViewModelProvider(this)[SplashViewModel::class.java] }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        launchWithLifecycle {
            viewModel.eventFlow.collect { event ->
                when (event) {
                    Event.LogInSuccessEvent -> {
                        viewModel.setLoggingInText(getString(R.string.getting_bookshelf))
                        //获取书架信息
                        viewModel.refreshBookshelfList() //等待书架信息获取完成
                        startActivity<MainActivity> {
                            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                            addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                        }
                        overridePendingTransition(0, 0)
                    }

                    Event.LogInFailureEvent -> {
                        startActivity<LoginActivity> {
                            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                            addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                            putExtra("isShowTip", true)
                        }
                    }

                    is Event.NetWorkErrorEvent -> {
                        toErrorScreen(event.msg)
                    }

                    else -> {}
                }
            }
        }

        when (viewModel.getLanguage()) {
            Language.FOLLOW_SYSTEM -> {
                Lingver.getInstance().setFollowSystemLocale(this)
                if (Lingver.getInstance().getLanguage() != "zh") Lingver.getInstance().setLocale(this, Locale.SIMPLIFIED_CHINESE)
            }
            Language.ZH_CN -> Lingver.getInstance().setLocale(this, Locale.SIMPLIFIED_CHINESE)
            Language.ZH_TW -> Lingver.getInstance().setLocale(this, Locale.TRADITIONAL_CHINESE)
        }

        init()
    }

    private fun init() {
        if (viewModel.getIsFirstLaunch()) {
            toGuideScreen()
            return
        }

        if (viewModel.isNotLoggedIn()) {
            startActivity<LoginActivity> {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
            }
            overridePendingTransition(0, 0)
            return
        }

        toLoggingInScreen()
        viewModel.login()
    }

    private fun toErrorScreen(msg: String?) {
        supportFragmentManager.beginTransaction().replace(
            R.id.fcv_a_splash,
            LoginErrorFragment().apply { arguments = Bundle().apply { putString("msg", msg) } }
        ).commit()
    }

    fun toLoggingInScreen() {
        supportFragmentManager.beginTransaction().replace(
            R.id.fcv_a_splash,
            LoggingInFragment()
        ).commit()
    }

    private fun toGuideScreen() {
        supportFragmentManager.beginTransaction().replace(
            R.id.fcv_a_splash,
            GuideFragment()
        ).commit()
    }
}