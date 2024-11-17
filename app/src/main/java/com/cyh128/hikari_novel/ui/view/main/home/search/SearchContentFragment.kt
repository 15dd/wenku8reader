package com.cyh128.hikari_novel.ui.view.main.home.search

import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.cyh128.hikari_novel.R
import com.cyh128.hikari_novel.base.BaseFragment
import com.cyh128.hikari_novel.data.model.Event
import com.cyh128.hikari_novel.databinding.FragmentNovelListBinding
import com.cyh128.hikari_novel.ui.view.detail.NovelInfoActivity
import com.cyh128.hikari_novel.ui.view.main.NovelCoverListAdapter
import com.cyh128.hikari_novel.util.startActivity
import com.drake.channel.receiveEvent
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SearchContentFragment : BaseFragment<FragmentNovelListBinding>() {
    private val viewModel by lazy { ViewModelProvider(requireActivity())[SearchViewModel::class.java] }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val adapter = NovelCoverListAdapter(viewModel.pager) { aid ->
            startActivity<NovelInfoActivity> {
                putExtra("aid", aid)
            }
        }
        binding.rvFNovelList.apply {
            layoutManager = GridLayoutManager(requireContext(), 3)
            this.adapter = adapter
        }

        binding.srlFNovelList.isEnabled = false

        receiveEvent<Event>("event_search_content_fragment") { event ->
            when(event) {
                Event.LoadSuccessEvent -> {
                    if (binding.rvFNovelList.isLoadingMore) binding.rvFNovelList.loadMoreComplete()
                    adapter.notifyItemChanged(adapter.itemCount, 20)
                }
                Event.SearchLoadErrorCauseByInFiveSecondEvent -> {
                    binding.rvFNovelList.loadMoreFail()
                    MaterialAlertDialogBuilder(requireContext())
                        .setMessage(R.string.in_five_second_tip)
                        .setCancelable(false)
                        .setPositiveButton(R.string.ok) { _, _ -> }
                        .show()
                }
                is Event.NetworkErrorEvent -> {
                    binding.rvFNovelList.loadMoreFail()
                    binding.srlFNovelList.isRefreshing = false
                    MaterialAlertDialogBuilder(requireContext())
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

        binding.rvFNovelList.setOnLoadMoreListener(false) {
            if (viewModel.haveMore()) viewModel.getData(false)
            else binding.rvFNovelList.loadMoreEnd()
        }
    }
}