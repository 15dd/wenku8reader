package com.cyh128.hikarinovel.ui.view.detail

import android.os.Bundle
import android.text.Html
import android.view.Menu
import android.view.View
import androidx.appcompat.content.res.AppCompatResources
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.cyh128.hikarinovel.R
import com.cyh128.hikarinovel.base.BaseActivity
import com.cyh128.hikarinovel.data.model.Event
import com.cyh128.hikarinovel.data.model.ReadParcel
import com.cyh128.hikarinovel.data.model.ReaderOrientation
import com.cyh128.hikarinovel.databinding.ActivityNovelInfoBinding
import com.cyh128.hikarinovel.ui.view.detail.comment.CommentActivity
import com.cyh128.hikarinovel.ui.view.main.home.home.search.SearchActivity
import com.cyh128.hikarinovel.ui.view.other.PhotoViewActivity
import com.cyh128.hikarinovel.util.launchWithLifecycle
import com.cyh128.hikarinovel.util.openUrl
import com.cyh128.hikarinovel.util.startActivity
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.last
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.launch

@AndroidEntryPoint
class NovelInfoActivity : BaseActivity<ActivityNovelInfoBinding>() {
    private val viewModel by lazy { ViewModelProvider(this)[NovelInfoViewModel::class.java] }
    private var chapterAdapter: NovelChapterListAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.aid = intent.getStringExtra("aid")!!

        setSupportActionBar(binding.tbANovelInfo)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.tbANovelInfo.setNavigationOnClickListener { finish() }

        launchWithLifecycle {
            viewModel.eventFlow.collect { event ->
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

                    is Event.NetWorkErrorEvent -> {
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
        chapterAdapter = null
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
                viewModelStoreOwner = this@NovelInfoActivity,
                novel = viewModel.novel,
                onItemClick = { volumePos: Int, chapterPos: Int ->
                    if (viewModel.readOrientation == ReaderOrientation.Vertical) {
                        startActivity<com.cyh128.hikarinovel.ui.view.read.vertical.ReadActivity> {
                            putExtra(
                                "data",
                                ReadParcel(viewModel.novel, volumePos, chapterPos, false)
                            )
                        }
                    } else {
                        startActivity<com.cyh128.hikarinovel.ui.view.read.horizontal.ReadActivity> {
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
            startActivity<PhotoViewActivity> {
                putExtra("url", viewModel.novelInfo.imgUrl)
            }
        }
        binding.fabANovelInfo.setOnClickListener {
            lifecycleScope.launch {
                if (viewModel.readOrientation == ReaderOrientation.Vertical) {
                    viewModel.getLatestReadHistory().take(1).last()?.let { data ->
                        startActivity<com.cyh128.hikarinovel.ui.view.read.vertical.ReadActivity> {
                            putExtra(
                                "data",
                                ReadParcel(viewModel.novel, data.volume, data.chapter, true)
                            )
                        }
                    }
                } else {
                    viewModel.getLatestReadHistory().take(1).last()?.let { data ->
                        startActivity<com.cyh128.hikarinovel.ui.view.read.horizontal.ReadActivity> {
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