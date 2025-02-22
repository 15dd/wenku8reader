package com.cyh128.hikari_novel.ui.detail

import android.os.Bundle
import android.text.Html
import android.view.Menu
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.marginLeft
import androidx.core.view.marginRight
import androidx.core.view.marginTop
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.cyh128.hikari_novel.R
import com.cyh128.hikari_novel.base.BaseActivity
import com.cyh128.hikari_novel.data.model.Event
import com.cyh128.hikari_novel.data.model.ReadParcel
import com.cyh128.hikari_novel.data.model.ReaderOrientation
import com.cyh128.hikari_novel.databinding.ActivityNovelInfoBinding
import com.cyh128.hikari_novel.ui.detail.comment.CommentActivity
import com.cyh128.hikari_novel.ui.main.bookshelf.BookshelfContentFragment
import com.cyh128.hikari_novel.ui.main.home.search.SearchActivity
import com.cyh128.hikari_novel.ui.other.PhotoViewActivity
import com.cyh128.hikari_novel.util.launchWithLifecycle
import com.cyh128.hikari_novel.util.openUrl
import com.cyh128.hikari_novel.util.setMargin
import com.cyh128.hikari_novel.util.startActivity
import com.drake.channel.receiveEvent
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.last
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


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
            when (event) {
                Event.LoadSuccessEvent -> {
                    binding.lpiANovelInfo.hide()
                    binding.fabFNovelInfo.visibility = View.VISIBLE
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
                    viewModel.getLatestReadHistory().take(1).last()?.let { data ->
                        startActivity<com.cyh128.hikari_novel.ui.read.vertical.ReadActivity> {
                            putExtra(
                                "data",
                                ReadParcel(viewModel.novel, data.volume, data.chapter, true)
                            )
                        }
                    }
                } else {
                    viewModel.getLatestReadHistory().take(1).last()?.let { data ->
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