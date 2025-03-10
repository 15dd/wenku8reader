package com.cyh128.hikari_novel.ui.detail

import android.os.Bundle
import android.view.Menu
import android.view.View
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.cyh128.hikari_novel.R
import com.cyh128.hikari_novel.base.BaseActivity
import com.cyh128.hikari_novel.data.model.Event
import com.cyh128.hikari_novel.data.model.ReadParcel
import com.cyh128.hikari_novel.data.model.ReaderOrientation
import com.cyh128.hikari_novel.databinding.ActivityNovelInfoBinding
import com.cyh128.hikari_novel.util.openUrl
import com.cyh128.hikari_novel.util.startActivity
import com.drake.channel.receiveEvent
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch


@AndroidEntryPoint
class NovelInfoActivity : BaseActivity<ActivityNovelInfoBinding>() {
    private val viewModel by lazy { ViewModelProvider(this)[NovelInfoViewModel::class.java] }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel.aid = intent.getStringExtra("aid")!!

        setSupportActionBar(binding.tbANovelInfo)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.tbANovelInfo.setNavigationOnClickListener { finish() }

        receiveEvent<Event>("event_novel_info_activity") { event ->
            //防止在重复创建此Activity时，已入栈的的同类型Activity接收到Event
            if (!lifecycle.currentState.isAtLeast(Lifecycle.State.STARTED)) return@receiveEvent

            when (event) {
                Event.LoadSuccessEvent -> {
                    binding.lpiANovelInfo.hide()
                    viewModel.getLatestReadHistoryFlow().onEach {
                        binding.fabFNovelInfo.visibility = if (it == null) View.INVISIBLE else View.VISIBLE
                    }.launchIn(lifecycleScope)
                    toNovelInfoContentScreen()
                }

                is Event.NetworkErrorEvent -> {
                    MaterialAlertDialogBuilder(this@NovelInfoActivity)
                        .setTitle(R.string.network_error)
                        .setIcon(R.drawable.ic_error)
                        .setMessage(event.msg)
                        .setCancelable(false)
                        .setPositiveButton(R.string.ok) { _, _ -> }
                        .show()
                }

                else -> {}
            }
        }

        initListener()
        viewModel.loadNovelAndChapter()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_act_novel_info, menu)
        return true
    }

    private fun initListener() {
        binding.tbANovelInfo.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.menu_open_in_browser -> {
                    openUrl(viewModel.novelUrl)
                    true
                }

                R.id.menu_clear_all_reading_history -> {
                    MaterialAlertDialogBuilder(this@NovelInfoActivity)
                        .setTitle(R.string.delete)
                        .setMessage(R.string.delete_all_reading_history_tip)
                        .setIcon(R.drawable.ic_delete_forever)
                        .setNegativeButton(R.string.no, null)
                        .setPositiveButton(R.string.yes) { _, _ ->
                            viewModel.deleteAllReadHistory()
                        }
                        .show()
                    true
                }

                else -> false
            }
        }

        binding.fabFNovelInfo.setOnClickListener {
            lifecycleScope.launch {
                if (viewModel.readOrientation == ReaderOrientation.Vertical) {
                    viewModel.getLatestReadHistoryFlow().first()?.let { data ->
                        startActivity<com.cyh128.hikari_novel.ui.read.vertical.ReadActivity> {
                            putExtra(
                                "data",
                                ReadParcel(viewModel.novel, data.volume, data.chapter, true)
                            )
                        }
                    }
                } else {
                    viewModel.getLatestReadHistoryFlow().first()?.let { data ->
                        startActivity<com.cyh128.hikari_novel.ui.read.horizontal.ReadActivity> {
                            putExtra(
                                "data",
                                ReadParcel(viewModel.novel, data.volume, data.chapter, true)
                            )
                        }
                    }
                }
            }
        }
    }

    private fun toNovelInfoContentScreen() {
        supportFragmentManager.beginTransaction().replace(
            R.id.fcv_a_novel_info,
            NovelInfoContentFragment()
        ).commit()
    }
}