package com.cyh128.hikari_novel.ui.view.splash

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.core.view.WindowCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.cyh128.hikari_novel.R
import com.cyh128.hikari_novel.base.BaseActivity
import com.cyh128.hikari_novel.data.model.Event
import com.cyh128.hikari_novel.databinding.ActivitySplashBinding
import com.cyh128.hikari_novel.ui.view.main.MainActivity
import com.cyh128.hikari_novel.util.ResourceUtil
import com.cyh128.hikari_novel.util.startActivity
import com.drake.channel.receiveEvent
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
@SuppressLint("CustomSplashScreen")
class SplashActivity : BaseActivity<ActivitySplashBinding>() {
    private val viewModel by lazy { ViewModelProvider(this)[SplashViewModel::class.java] }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        WindowCompat.setDecorFitsSystemWindows(window, false)

        receiveEvent<Event>("event_splash_activity") { event ->
            when (event) {
                Event.AuthFailedEvent -> {
                    startActivity<LoginActivity> {
                        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                        putExtra("isShowTip", true)
                    }
                }

                Event.LogInFailureEvent -> {
                    toErrorScreen(ResourceUtil.getString(R.string.login_failed_tip_2))
                }

                is Event.NetworkErrorEvent -> {
                    toErrorScreen(event.msg)
                }

                else -> {}
            }
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
        } else {
            Log.d("s_a","${viewModel.getCookie()}")
            toLoggingInScreen()
            lifecycleScope.launch {
                viewModel.setLoggingInText(getString(R.string.getting_bookshelf))
                //获取书架信息
                viewModel.refreshBookshelfList() //等待书架信息获取完成
                startActivity<MainActivity> {
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                }
            }
        }
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