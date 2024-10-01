package com.cyh128.hikarinovel.ui.view.main

import android.os.Bundle
import android.widget.Toast
import androidx.core.view.WindowCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.cyh128.hikarinovel.R
import com.cyh128.hikarinovel.base.BaseActivity
import com.cyh128.hikarinovel.data.model.Event
import com.cyh128.hikarinovel.databinding.ActivityMainBinding
import com.cyh128.hikarinovel.ui.view.main.home.bookshelf.BookshelfFragment
import com.cyh128.hikarinovel.ui.view.main.home.home.HomeFragment
import com.cyh128.hikarinovel.ui.view.main.home.more.MoreFragment
import com.cyh128.hikarinovel.ui.view.main.home.visit_history.VisitHistoryFragment
import com.cyh128.hikarinovel.util.launchWithLifecycle
import com.cyh128.hikarinovel.util.openUrl
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock


@AndroidEntryPoint
class MainActivity : BaseActivity<ActivityMainBinding>() {
    private val viewModel by lazy { ViewModelProvider(this)[MainViewModel::class.java] }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        binding.vpAMain.adapter = object : FragmentStateAdapter(this) {
            override fun getItemCount(): Int = 4
            override fun createFragment(position: Int): Fragment = when(position) {
                0 -> HomeFragment()
                1 -> BookshelfFragment()
                2 -> VisitHistoryFragment()
                3 -> MoreFragment()
                else -> throw IllegalArgumentException()
            }
        }

        binding.vpAMain.isUserInputEnabled = false
        binding.bnvAMain.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.homeFragment -> {
                    viewModel.currentItem.value = 0
                    true
                }

                R.id.bookshelfFragment -> {
                    viewModel.currentItem.value = 1
                    true
                }

                R.id.visitHistoryFragment -> {
                    viewModel.currentItem.value = 2
                    true
                }

                R.id.moreFragment -> {
                    viewModel.currentItem.value = 3
                    true
                }

                else -> throw IllegalArgumentException()
            }
        }

        viewModel.currentItem.observe(this) {
            binding.vpAMain.setCurrentItem(it, false)
        }

        launchWithLifecycle {
            viewModel.eventFlow.collect { event ->
                when (event) {
                    is Event.NetWorkErrorEvent -> {
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
        }

        if (viewModel.isAutoUpdate) viewModel.checkUpdate()
    }

    private val onBackPressedMutex = Mutex()
    override fun onBackPressed() {
        lifecycleScope.launch {
            if (onBackPressedMutex.isLocked) {
                super.onBackPressed()
                return@launch
            }
            onBackPressedMutex.withLock {
                Toast.makeText(this@MainActivity,R.string.back_key_pressed_tip,Toast.LENGTH_SHORT).show()
                delay(2000)
            }
        }
    }
}