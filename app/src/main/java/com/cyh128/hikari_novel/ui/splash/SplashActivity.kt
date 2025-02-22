package com.cyh128.hikari_novel.ui.splash

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.core.view.WindowCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.cyh128.hikari_novel.R
import com.cyh128.hikari_novel.base.BaseActivity
import com.cyh128.hikari_novel.data.model.Event
import com.cyh128.hikari_novel.databinding.ActivitySplashBinding
import com.cyh128.hikari_novel.ui.main.MainActivity
import com.cyh128.hikari_novel.util.startActivity
import com.drake.channel.receiveEvent
import com.trendyol.medusalib.navigator.MultipleStackNavigator
import com.trendyol.medusalib.navigator.NavigatorConfiguration
import com.trendyol.medusalib.navigator.transaction.NavigatorTransaction
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
@SuppressLint("CustomSplashScreen")
class SplashActivity : BaseActivity<ActivitySplashBinding>() {
    private val viewModel by lazy { ViewModelProvider(this)[SplashViewModel::class.java] }

    private val rootFragmentProvider: List<() -> Fragment> = listOf { LoggingInFragment() }

    val navigator = MultipleStackNavigator(
        supportFragmentManager,
        R.id.fcv_a_splash,
        rootFragmentProvider,
        navigatorConfiguration = NavigatorConfiguration(defaultNavigatorTransaction = NavigatorTransaction.ATTACH_DETACH)
    )

    override fun onSaveInstanceState(outState: Bundle) {
        navigator.onSaveInstanceState(outState)
        super.onSaveInstanceState(outState)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        navigator.initialize(savedInstanceState)

        WindowCompat.setDecorFitsSystemWindows(window, false)

        receiveEvent<Event>("event_splash_activity") { event ->
            when (event) {
                Event.LoadSuccessEvent -> {
                    startActivity<MainActivity> {
                        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                    }
                    overridePendingTransition(0, 0)
                }

                Event.AuthFailedEvent -> {
                    startActivity<LoginActivity> {
                        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                        putExtra("isShowTip", true)
                    }
                }

                Event.LogInFailureEvent -> {
                    startFragment<LoginErrorFragment> {
                        putString("msg", getString(R.string.login_failed_tip_2))
                    }
                }

                is Event.NetworkErrorEvent -> {
                    startFragment<LoginErrorFragment> {
                        putString("msg", event.msg)
                    }
                }

                else -> {}
            }
        }

        init()
    }

    private fun init() {
        if (viewModel.getIsFirstLaunch()) {
            startFragment<GuideFragment>()
            return
        }

        if (viewModel.isNotLoggedIn()) {
            startActivity<LoginActivity> {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
            }
        } else {
            Log.d("s_a", "${viewModel.getCookie()}")
            startFragment<LoggingInFragment>()
            lifecycleScope.launch {
                viewModel.setLoggingInText(getString(R.string.getting_bookshelf))
                //获取书架信息
                viewModel.getAllBookshelf()
            }
        }
    }

    inline fun <reified T : Fragment> startFragment(args: Bundle.() -> Unit = {}) {
        val fragment = T::class.java.getDeclaredConstructor().newInstance().apply {
            arguments = Bundle().apply(args)
        }
        navigator.start(fragment)
    }
}