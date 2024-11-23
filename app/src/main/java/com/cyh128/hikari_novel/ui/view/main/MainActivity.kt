package com.cyh128.hikari_novel.ui.view.main

import android.os.Bundle
import android.widget.Toast
import androidx.core.view.WindowCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.cyh128.hikari_novel.R
import com.cyh128.hikari_novel.base.BaseActivity
import com.cyh128.hikari_novel.data.model.DefaultTab
import com.cyh128.hikari_novel.data.model.Event
import com.cyh128.hikari_novel.databinding.ActivityMainBinding
import com.cyh128.hikari_novel.ui.view.main.bookshelf.BookshelfFragment
import com.cyh128.hikari_novel.ui.view.main.home.HomeFragment
import com.cyh128.hikari_novel.ui.view.main.more.MoreFragment
import com.cyh128.hikari_novel.ui.view.main.visit_history.VisitHistoryFragment
import com.cyh128.hikari_novel.util.Constants
import com.cyh128.hikari_novel.util.openUrl
import com.drake.channel.receiveEvent
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.trendyol.medusalib.navigator.MultipleStackNavigator
import com.trendyol.medusalib.navigator.Navigator
import com.trendyol.medusalib.navigator.NavigatorConfiguration
import com.trendyol.medusalib.navigator.transaction.NavigatorTransaction
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

@AndroidEntryPoint
class MainActivity : BaseActivity<ActivityMainBinding>() {
    private val viewModel by lazy { ViewModelProvider(this)[MainViewModel::class.java] }

    private val rootFragmentProvider: List<() -> Fragment> = listOf (
        { HomeFragment() },
        { BookshelfFragment() },
        { VisitHistoryFragment() },
        { MoreFragment() }
    )

    private val navigatorListener = object : Navigator.NavigatorListener {
        override fun onTabChanged(tabIndex: Int) {
            when(tabIndex) {
                0 -> binding.bnvAMain.selectedItemId = R.id.homeFragment
                1 -> binding.bnvAMain.selectedItemId = R.id.bookshelfFragment
                2 -> binding.bnvAMain.selectedItemId = R.id.visitHistoryFragment
                3 -> binding.bnvAMain.selectedItemId = R.id.moreFragment
            }
        }
    }

    private val navigator = MultipleStackNavigator(
        supportFragmentManager,
        R.id.fcv_a_main,
        rootFragmentProvider,
        navigatorListener,
        NavigatorConfiguration(0,true, NavigatorTransaction.SHOW_HIDE)
    )

    override fun onSaveInstanceState(outState: Bundle) {
        navigator.onSaveInstanceState(outState)
        super.onSaveInstanceState(outState)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        navigator.initialize(savedInstanceState)

        WindowCompat.setDecorFitsSystemWindows(window, false)

        receiveEvent<Event>("event_main_activity") { event ->
            when (event) {
                is Event.NetworkErrorEvent -> {
                    MaterialAlertDialogBuilder(this@MainActivity)
                        .setIcon(R.drawable.ic_error)
                        .setTitle(R.string.check_update_failure)
                        .setMessage(event.msg)
                        .setCancelable(false)
                        .setPositiveButton(R.string.ok) { _, _ -> }
                        .show()
                }

                Event.HaveAvailableUpdateEvent -> {
                    MaterialAlertDialogBuilder(this@MainActivity)
                        .setIcon(R.drawable.ic_release_alert)
                        .setTitle(R.string.update_available)
                        .setMessage(R.string.update_available_tip)
                        .setCancelable(false)
                        .setPositiveButton(R.string.go_to_download) { _, _ ->
                            openUrl("https://github.com/15dd/wenku8reader/releases")
                        }
                        .setNegativeButton(R.string.cancel) { _, _ -> }
                        .show()
                }

                else -> {}
            }
        }

        binding.bnvAMain.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.homeFragment -> {
                    navigator.switchTab(0)
                    true
                }

                R.id.bookshelfFragment -> {
                    navigator.switchTab(1)
                    true
                }

                R.id.visitHistoryFragment -> {
                    navigator.switchTab(2)
                    true
                }

                R.id.moreFragment -> {
                    navigator.switchTab(3)
                    true
                }

                else -> throw IllegalArgumentException()
            }
        }

        //冷启动tab初始化
        when(viewModel.defaultTab) {
            DefaultTab.Home -> navigator.switchTab(0)
            DefaultTab.Bookshelf -> navigator.switchTab(1)
            DefaultTab.History -> navigator.switchTab(2)
            DefaultTab.More -> navigator.switchTab(3)
        }

        //检查软件更新
        if (viewModel.isAutoUpdate && !Constants.isUpdateChecked) {
            viewModel.checkUpdate()
            Constants.isUpdateChecked = true //防止activity重创以后再次检查更新
        }
    }

    //二次确认退出
    private val onBackPressedMutex = Mutex()
    override fun onBackPressed() {
        lifecycleScope.launch {
            if (onBackPressedMutex.isLocked) {
                super.onBackPressed()
                return@launch
            }
            onBackPressedMutex.withLock {
                Toast.makeText(this@MainActivity, R.string.back_key_pressed_tip, Toast.LENGTH_SHORT)
                    .show()
                delay(2000)
            }
        }
    }
}