package com.cyh128.hikari_novel.ui.view.main.visit_history

import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.cyh128.hikari_novel.base.BaseFragment
import com.cyh128.hikari_novel.databinding.FragmentNovelListBinding
import com.cyh128.hikari_novel.util.launchWithLifecycle
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class VisitHistoryContentFragment: BaseFragment<FragmentNovelListBinding>() {
    private val viewModel by lazy { ViewModelProvider(requireActivity())[VisitHistoryViewModel::class.java] }
    private lateinit var visitHistoryListAdapter: VisitHistoryListAdapter
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        visitHistoryListAdapter = VisitHistoryListAdapter { aid ->
            viewModel.delete(aid)
        }

        binding.rvFNovelList.apply {
            adapter = visitHistoryListAdapter
            layoutManager = LinearLayoutManager(context)
        }
        binding.srlFNovelList.isEnabled = false

        launchWithLifecycle {
            viewModel.visitHistoryFlow.collect {
                visitHistoryListAdapter.updateData(it)
                visitHistoryListAdapter.notifyDataSetChanged()
            }
        }
    }
}