package com.cyh128.hikari_novel.ui.detail.comment

import android.os.Bundle
import androidx.core.view.WindowCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.cyh128.hikari_novel.base.BaseActivity
import com.cyh128.hikari_novel.data.model.Event
import com.cyh128.hikari_novel.data.model.LoadMode
import com.cyh128.hikari_novel.databinding.ActivityCommentBinding
import com.cyh128.hikari_novel.ui.detail.comment.reply.ReplyFragment
import com.cyh128.hikari_novel.ui.detail.user_bookshelf.UserBookshelfActivity
import com.cyh128.hikari_novel.util.startActivity
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

        val adapter = CommentListAdapter(
            list = viewModel.list,
            onItemClick = { url ->
                showReplyFragment(url)
            },
            onUsernameClick = { uid ->
                startActivity<UserBookshelfActivity> {
                    putExtra("uid", uid)
                }
            }
        )
        binding.rvAComment.apply {
            layoutManager = LinearLayoutManager(this@CommentActivity)
            this.adapter = adapter
        }

        receiveEvent<Event>("event_comment_activity") { event ->
            when (event) {
                Event.LoadSuccessEvent -> {
                    binding.srlAComment.isRefreshing = false
                    adapter.notifyItemChanged(adapter.itemCount, 20)
                    if (binding.rvAComment.isLoadingMore) binding.rvAComment.loadMoreComplete()
                    else if (binding.srlAComment.isRefreshing) binding.srlAComment.isRefreshing = false
                }

                is Event.NetworkErrorEvent -> {
                    binding.srlAComment.isRefreshing = false
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

        binding.srlAComment.isRefreshing = true
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