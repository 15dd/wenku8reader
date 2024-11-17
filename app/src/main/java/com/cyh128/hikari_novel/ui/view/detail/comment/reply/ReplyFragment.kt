package com.cyh128.hikari_novel.ui.view.detail.comment.reply

import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.cyh128.hikari_novel.base.BaseBottomSheetDialogFragment
import com.cyh128.hikari_novel.data.model.Event
import com.cyh128.hikari_novel.databinding.FragmentReplyBinding
import com.drake.channel.receiveEvent
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ReplyFragment : BaseBottomSheetDialogFragment<FragmentReplyBinding>() {
    private val viewModel by lazy { ViewModelProvider(this)[ReplyViewModel::class.java] }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val url = requireArguments().getString("url")!!
        val adapter = ReplyListAdapter(viewModel.pager)

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
}