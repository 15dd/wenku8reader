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
    private var chapterAdapter: NovelChapterListAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()

        super.onCreate(savedInstanceState)

        ViewCompat.setOnApplyWindowInsetsListener(binding.nsvANovelInfo) { v, insets ->
            val navigationBars = insets.getInsets(WindowInsetsCompat.Type.navigationBars())
            v.setPadding(
                navigationBars.left,
                navigationBars.top,
                navigationBars.right,
                navigationBars.bottom
            )
            insets
        }//edgeToEdge
        ViewCompat.setOnApplyWindowInsetsListener(binding.fabANovelInfo) { v, insets ->
            val navigationBars = insets.getInsets(WindowInsetsCompat.Type.navigationBars())
            v.setMargin(v.marginLeft, v.marginTop, v.marginRight, navigationBars.bottom)
            insets
        }//edgeToEdge

        viewModel.aid = intent.getStringExtra("aid")!!

        setSupportActionBar(binding.tbANovelInfo)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.tbANovelInfo.setNavigationOnClickListener { finish() }

        receiveEvent<Event>("event_novel_info_activity") { event ->
            when (event) {
                Event.LoadSuccessEvent -> {
                    setInfoView()
                }

                Event.AddToBookshelfFailure -> {
                    MaterialAlertDialogBuilder(this@NovelInfoActivity)
                        .setIcon(R.drawable.ic_error)
                        .setTitle(R.string.add_novel_error)
                        .setMessage(R.string.add_novel_error_msg)
                        .setCancelable(false)
                        .setPositiveButton(R.string.ok) { _, _ -> }
                        .show()
                }

                Event.InBookshelfEvent -> {
                    setRemoveNovelButton()
                    binding.bANovelInfoBookshelf.isEnabled = true
                }

                Event.NotInBookshelfEvent -> {
                    setAddNovelButton()
                    binding.bANovelInfoBookshelf.isEnabled = true
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

                is Event.VoteSuccessEvent -> {
                    MaterialAlertDialogBuilder(this@NovelInfoActivity)
                        .setIcon(R.drawable.ic_recommend)
                        .setTitle(R.string.vote)
                        .setMessage(event.msg)
                        .setCancelable(false)
                        .setPositiveButton(R.string.ok) { _, _ -> }
                        .show()
                    binding.bANovelInfoVote.isEnabled = true
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

    override fun onDestroy() {
        super.onDestroy()

        //防止内存泄漏
        binding.ervANovelInfo.adapter = null
        binding.ervANovelInfo.setOnApplyWindowInsetsListener(null)
        binding.fabANovelInfo.setOnApplyWindowInsetsListener(null)
    }

    private fun setInfoView() { //设置小说信息
        binding.apply {
            viewModel.novelInfo.also { nv ->
                tvANovelInfoTitle.text = nv.title
                tvANovelInfoAuthor.apply {
                    text = nv.author
                    setOnClickListener {
                        startActivity<SearchActivity> {
                            putExtra("author", nv.author)
                        }
                    }
                }

                nv.status.also { status ->
                    tvANovelInfoStatus.text = status
                    if (status == "已完结") ivANovelInfoStatus.setImageDrawable(
                        AppCompatResources.getDrawable(
                            this@NovelInfoActivity,
                            R.drawable.ic_done_all
                        )
                    )
                }
                tvANovelInfoFinUpdate.text = nv.finUpdate
                tvANovelInfoHeat.text = nv.heat
                tvANovelInfoTrending.text = nv.trending

                if (nv.isAnimated) {
                    tvANovelInfoIsAnimated.text = getString(R.string.animated)
                    ivANovelInfoAnim.setImageDrawable(
                        AppCompatResources.getDrawable(
                            this@NovelInfoActivity,
                            R.drawable.ic_live_tv
                        )
                    )
                } else {
                    tvANovelInfoIsAnimated.text = getString(R.string.not_animated)
                    ivANovelInfoAnim.setImageDrawable(
                        AppCompatResources.getDrawable(
                            this@NovelInfoActivity,
                            R.drawable.ic_tv_off
                        )
                    )
                }


                Glide.with(ivANovelInfo)
                    .load(nv.imgUrl)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(ivANovelInfo)

                etvANovelInfo.setContent(
                    Html.fromHtml(nv.introduce, Html.FROM_HTML_MODE_COMPACT).toString()
                )
                rvANovelInfo.apply {
                    layoutManager =
                        LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
                    adapter = TagChipAdapter(nv.tag)
                }

            }

            chapterAdapter = NovelChapterListAdapter(
                novel = viewModel.novel,
                onItemClick = { volumePos: Int, chapterPos: Int ->
                    if (viewModel.readOrientation == ReaderOrientation.Vertical) {
                        startActivity<com.cyh128.hikari_novel.ui.read.vertical.ReadActivity> {
                            putExtra(
                                "data",
                                ReadParcel(viewModel.novel, volumePos, chapterPos, false)
                            )
                        }
                    } else {
                        startActivity<com.cyh128.hikari_novel.ui.read.horizontal.ReadActivity> {
                            putExtra(
                                "data",
                                ReadParcel(viewModel.novel, volumePos, chapterPos, false)
                            )
                        }
                    }
                },
                onLongClick = { cid ->
                    //长按事件
                    MaterialAlertDialogBuilder(this@NovelInfoActivity)
                        .setTitle(R.string.delete)
                        .setMessage(R.string.delete_reading_history_tip)
                        .setIcon(R.drawable.ic_delete)
                        .setNegativeButton(R.string.no, null)
                        .setPositiveButton(R.string.yes) { _, _ ->
                            viewModel.deleteReadHistory(cid)
                        }
                        .show()
                },
                onGroupItemChangeListener = { group, binding ->
                    lifecycleScope.launch(Dispatchers.IO) {
                        viewModel.getReadHistoryByVolume(group).collect {
                            withContext(Dispatchers.Main) {
                                if (it.isNullOrEmpty()) {
                                    binding.tvIChapterVcssCompleted.text = getString(R.string.unread)
                                    binding.tvIChapterVcss.isEnabled = true
                                    binding.tvIChapterVcssCompleted.isEnabled = true
                                } else if (it.size == viewModel.novel.volume[group].chapters.size) {
                                    var isAllRead = false
                                    it.forEach { entity ->
                                        isAllRead = entity.progressPercent == 100
                                    }
                                    if (isAllRead) {
                                        binding.tvIChapterVcssCompleted.text =
                                            getString(R.string.completed_reading)
                                        binding.tvIChapterVcss.isEnabled = false
                                        binding.tvIChapterVcssCompleted.isEnabled = false
                                    } else {
                                        binding.tvIChapterVcssCompleted.text =
                                            getString(R.string.partly_completed_reading)
                                        binding.tvIChapterVcss.isEnabled = true
                                        binding.tvIChapterVcssCompleted.isEnabled = true
                                    }
                                } else {
                                    binding.tvIChapterVcssCompleted.text = getString(R.string.partly_completed_reading)
                                    binding.tvIChapterVcss.isEnabled = true
                                    binding.tvIChapterVcssCompleted.isEnabled = true
                                }
                            }
                        }
                    }
                },
                onChildItemChangeListener = { group, child, binding ->
                    lifecycleScope.launch(Dispatchers.IO) {
                        viewModel.getReadHistoryByCid(viewModel.novel.volume[group].chapters[child].cid)
                            .collect {
                                withContext(Dispatchers.Main) {
                                    if (it == null) {
                                        binding.tvIChapterCcssCompleted.text = getString(R.string.unread)
                                        binding.tvIChapterCcss.isEnabled = true
                                        binding.tvIChapterCcssCompleted.isEnabled = true

                                        binding.tvIChapterCcssLatest.text = null
                                        return@withContext
                                    } else if (it.progressPercent == 100) {
                                        binding.tvIChapterCcssCompleted.text = getString(R.string.completed_reading)
                                        binding.tvIChapterCcss.isEnabled = false
                                        binding.tvIChapterCcssCompleted.isEnabled = false
                                    } else {
                                        binding.tvIChapterCcssCompleted.text = "${it.progressPercent}%"
                                        binding.tvIChapterCcss.isEnabled = true
                                        binding.tvIChapterCcssCompleted.isEnabled = true
                                    }

                                    if (it.isLatest) binding.tvIChapterCcssLatest.text = getString(R.string.last_read)
                                    else binding.tvIChapterCcssLatest.text = null
                                }
                            }
                    }
                }
            )

            ervANovelInfo.apply {
                layoutManager = LinearLayoutManager(context)
                adapter = chapterAdapter
            }

            lpiANovelInfo.hide()
            nsvANovelInfo.visibility = View.VISIBLE

            //以下代码必须放在网络数据请求完成之后，不然会导致fab提前显示
            launchWithLifecycle {
                viewModel.getLatestReadHistory().collect {
                    if (it == null) fabANovelInfo.visibility = View.INVISIBLE
                    else fabANovelInfo.visibility = View.VISIBLE
                }
            }
        }
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

        binding.bANovelInfoComment.setOnClickListener {
            startActivity<CommentActivity> {
                putExtra("aid", viewModel.aid)
            }
        }

        binding.bANovelInfoBookshelf.setOnClickListener {
            it.isEnabled = false
            viewModel.addOrRemoveBook()
        }

        binding.bANovelInfoVote.setOnClickListener {
            viewModel.voteNovel()
            binding.bANovelInfoVote.isEnabled = false
        }

        binding.ivANovelInfo.setOnClickListener {
            startActivity<com.cyh128.hikari_novel.ui.other.PhotoViewActivity> {
                putExtra("url", viewModel.novelInfo.imgUrl)
            }
        }
        binding.fabANovelInfo.setOnClickListener {
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

    private fun setRemoveNovelButton() {
        binding.bANovelInfoBookshelf.setIconResource(R.drawable.ic_baseline_favorite)
        binding.bANovelInfoBookshelf.text = getString(R.string.added_in_bookshelf)
    }

    private fun setAddNovelButton() {
        binding.bANovelInfoBookshelf.setIconResource(R.drawable.ic_outline_favorite)
        binding.bANovelInfoBookshelf.text = getString(R.string.add_to_bookshelf)
    }
}