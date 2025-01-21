package com.cyh128.hikari_novel.ui.main.home.ranking

import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.cyh128.hikari_novel.R
import com.cyh128.hikari_novel.base.BaseFragment
import com.cyh128.hikari_novel.data.model.Event
import com.cyh128.hikari_novel.data.model.LoadMode
import com.cyh128.hikari_novel.databinding.FragmentNovelListBinding
import com.cyh128.hikari_novel.ui.detail.NovelInfoActivity
import com.cyh128.hikari_novel.ui.main.NovelCoverListAdapter
import com.cyh128.hikari_novel.util.startActivity
import com.drake.channel.receiveEvent
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class RankingContentFragment: BaseFragment<FragmentNovelListBinding>() {
    private val viewModel by lazy { ViewModelProvider(this)[RankingViewModel::class.java] }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val ranking = requireArguments().getString("ranking")!!

        val adapter = NovelCoverListAdapter(viewModel.pager) { aid ->
            startActivity<NovelInfoActivity> {
                putExtra("aid", aid)
            }
        }
        binding.rvFNovelList.apply {
            layoutManager = GridLayoutManager(requireContext(), 3)
            this.adapter = adapter
        }

        receiveEvent<Event>("event_ranking_view_model") { event ->
            when(event) {
                Event.LoadSuccessEvent -> {
                    adapter.notifyItemChanged(adapter.itemCount, 20)
                    if (binding.rvFNovelList.isLoadingMore) binding.rvFNovelList.loadMoreComplete()
                    binding.srlFNovelList.isRefreshing = false
                }
                is Event.NetworkErrorEvent -> {
                    MaterialAlertDialogBuilder(requireContext())
                        .setTitle(R.string.network_error)
                        .setIcon(R.drawable.ic_error)
                        .setMessage(event.msg)
                        .setCancelable(false)
                        .setPositiveButton(R.string.ok) { _, _ -> }
                        .show()
                    binding.rvFNovelList.loadMoreFail()
                    binding.srlFNovelList.isRefreshing = false
                }
                else -> {}
            }
        }

        binding.rvFNovelList.setOnLoadMoreListener {
            if (viewModel.haveMore()) viewModel.getData(LoadMode.LOADMORE,ranking)
            else binding.rvFNovelList.loadMoreEnd()
        }

        binding.srlFNovelList.setOnRefreshListener {
            viewModel.getData(LoadMode.REFRESH, ranking)
        }

        if (viewModel.pager.isEmpty()) {
            binding.srlFNovelList.isRefreshing = true
            viewModel.getData(LoadMode.REFRESH, ranking)
        }
    }
}