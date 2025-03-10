package com.cyh128.hikari_novel.ui.detail.comment.reply

import android.content.DialogInterface
import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.cyh128.hikari_novel.base.BaseBottomSheetDialogFragment
import com.cyh128.hikari_novel.data.model.Event
import com.cyh128.hikari_novel.databinding.FragmentReplyBinding
import com.cyh128.hikari_novel.ui.detail.comment.CommentActivity
import com.cyh128.hikari_novel.ui.detail.user_bookshelf.UserBookshelfActivity
import com.cyh128.hikari_novel.util.startActivity
import com.drake.channel.receiveEvent
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ReplyFragment : BaseBottomSheetDialogFragment<FragmentReplyBinding>() {
    private val viewModel by lazy { ViewModelProvider(this)[ReplyViewModel::class.java] }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val url = requireArguments().getString("url")!!
        val adapter = ReplyListAdapter(
            list = viewModel.pager,
            onUsernameClick = { uid ->
                startActivity<UserBookshelfActivity> {
                    putExtra("uid", uid)
                }
            }
        )

        binding.rvFReply.apply {
            layoutManager = LinearLayoutManager(requireContext())
            this.adapter = adapter
            setOnLoadMoreListener {
                if (viewModel.haveMore()) viewModel.getReply(url)
                else loadMoreEnd()
            }
        }

        receiveEvent<Event>("event_reply_fragment") { event ->
            when(event) {
                Event.LoadSuccessEvent -> {
                    if (binding.cpiFReply.isShown) {
                        binding.cpiFReply.hide()
                        binding.rvFReply.visibility = View.VISIBLE
                    }
                    adapter.notifyItemChanged(viewModel.maxNum!!,20)
                    if (binding.rvFReply.isLoadingMore) binding.rvFReply.loadMoreComplete()
                    if (!viewModel.haveMore()) binding.rvFReply.loadMoreEnd()
                }
                is Event.NetworkErrorEvent -> {
                    if (binding.cpiFReply.isShown) {
                        binding.cpiFReply.hide()
                        binding.rvFReply.visibility = View.VISIBLE
                    }
                    binding.rvFReply.loadMoreFail()
                }
                else -> {}
            }
        }

        viewModel.getReply(url)
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        (requireActivity() as CommentActivity).setDialogShownFalse()
    }
}