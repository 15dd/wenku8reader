package com.cyh128.hikarinovel.ui.view.main.home.home.completion

import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.cyh128.hikarinovel.R
import com.cyh128.hikarinovel.base.BaseFragment
import com.cyh128.hikarinovel.data.model.Event
import com.cyh128.hikarinovel.data.model.LoadMode
import com.cyh128.hikarinovel.databinding.FragmentNovelListBinding
import com.cyh128.hikarinovel.ui.view.detail.NovelInfoActivity
import com.cyh128.hikarinovel.ui.view.main.home.NovelCoverListAdapter
import com.cyh128.hikarinovel.util.launchWithLifecycle
import com.cyh128.hikarinovel.util.startActivity
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CompletionFragment : BaseFragment<FragmentNovelListBinding>() {
    private val viewModel by lazy { ViewModelProvider(this)[CompletionViewModel::class.java] }
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

        launchWithLifecycle {
            viewModel.eventFlow.collect { event ->
                when(event) {
                    Event.LoadSuccessEvent -> {
                        adapter.notifyItemChanged(adapter.itemCount, 20)
                        if (binding.rvFNovelList.isLoadingMore) binding.rvFNovelList.loadMoreComplete()
                        if (binding.srlFNovelList.isRefreshing) binding.srlFNovelList.isRefreshing = false
                    }
                    is Event.NetWorkErrorEvent -> {
                        MaterialAlertDialogBuilder(requireContext())
                            .setTitle(R.string.network_error)
                            .setIcon(R.drawable.ic_error)
                            .setMessage(event.msg)
                            .setCancelable(false)
                            .setPositiveButton(R.string.ok) { _, _ -> }
                            .show()
                        binding.rvFNovelList.loadMoreFail()
                        if (binding.srlFNovelList.isRefreshing) binding.srlFNovelList.isRefreshing = false
                    }
                    else -> {}
                }
            }
        }

        binding.rvFNovelList.setOnLoadMoreListener {
            if (viewModel.haveMore()) viewModel.getData(LoadMode.LOADMORE)
            else binding.rvFNovelList.loadMoreEnd()
        }

        binding.srlFNovelList.setOnRefreshListener {
            viewModel.getData(LoadMode.REFRESH)
        }

        if (viewModel.pager.isEmpty()) {
            binding.srlFNovelList.isRefreshing = true
            viewModel.getData(LoadMode.REFRESH)
        }
    }
}