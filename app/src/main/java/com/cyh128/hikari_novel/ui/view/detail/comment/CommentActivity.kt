package com.cyh128.hikari_novel.ui.view.detail.comment

import android.os.Bundle
import android.view.View
import androidx.core.view.WindowCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.cyh128.hikari_novel.base.BaseActivity
import com.cyh128.hikari_novel.data.model.Event
import com.cyh128.hikari_novel.data.model.LoadMode
import com.cyh128.hikari_novel.databinding.ActivityCommentBinding
import com.cyh128.hikari_novel.ui.view.detail.comment.reply.ReplyFragment
import com.drake.channel.receiveEvent
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CommentActivity : BaseActivity<ActivityCommentBinding>() {
    private val viewModel by lazy { ViewModelProvider(this)[CommentViewModel::class.java] }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setSupportActionBar(binding.tbAComment)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.tbAComment.setNavigationOnClickListener { finish() }

        WindowCompat.setDecorFitsSystemWindows(window, false)

        val adapter = CommentListAdapter(
            list = viewModel.list,
            onItemClick = { url ->
                showReplyFragment(url)
            }
        )
        binding.rvAComment.apply {
            layoutManager = LinearLayoutManager(this@CommentActivity)
            this.adapter = adapter
        }

        receiveEvent<Event>("event_comment_activity") { event ->
            when (event) {
                Event.LoadSuccessEvent -> {
                    if (binding.cpiAComment.isShown) {
                        binding.cpiAComment.hide()
                        binding.srlAComment.visibility = View.VISIBLE
                    }
                    adapter.notifyItemChanged(adapter.itemCount, 20)
                    if (binding.rvAComment.isLoadingMore) binding.rvAComment.loadMoreComplete()
                    else if (binding.srlAComment.isRefreshing) binding.srlAComment.isRefreshing =
                        false
                }

                is Event.NetworkErrorEvent -> {
                    binding.rvAComment.loadMoreFail()
                    if (binding.srlAComment.isRefreshing) binding.srlAComment.isRefreshing = false
                }

                else -> {}
            }
        }

        binding.rvAComment.setOnLoadMoreListener {
            if (viewModel.haveMore()) viewModel.getComment(LoadMode.LOADMORE)
            else binding.rvAComment.loadMoreEnd()
        }

        binding.srlAComment.setOnRefreshListener {
            viewModel.getComment(LoadMode.REFRESH)
        }

        viewModel.aid = intent.getStringExtra("aid")!!
        viewModel.getComment(LoadMode.REFRESH)
    }

    private fun showReplyFragment(url: String) {
        //防抖动
        if (viewModel.isDialogShown) return
        ReplyFragment().apply {
            arguments = Bundle().also { it.putString("url", url) }
            show(supportFragmentManager,"replyFragment")
        }
        viewModel.isDialogShown = true
    }

    fun setDialogShownFalse() {
        viewModel.isDialogShown = false
    }
}