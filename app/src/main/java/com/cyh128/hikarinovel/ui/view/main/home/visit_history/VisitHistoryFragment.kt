package com.cyh128.hikarinovel.ui.view.main.home.visit_history

import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProvider
import com.cyh128.hikarinovel.R
import com.cyh128.hikarinovel.base.BaseFragment
import com.cyh128.hikarinovel.databinding.FragmentVisitHistoryBinding
import com.cyh128.hikarinovel.ui.view.other.EmptyView
import com.cyh128.hikarinovel.util.launchWithLifecycle
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class VisitHistoryFragment : BaseFragment<FragmentVisitHistoryBinding>() {
    private val viewModel by lazy { ViewModelProvider(requireActivity())[VisitHistoryViewModel::class.java] }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.tbFVisitHistory.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.menu_a_visit_history_clear_history -> {
                    viewModel.deleteAllHistory()
                    showEmptyScreen()
                    true
                }

                else -> false
            }
        }

        launchWithLifecycle {
            viewModel.visitHistoryFlow.collect {
                if (it.isNullOrEmpty()) {
                    showEmptyScreen()
                }
                else {
                    showContentScreen()
                }
            }
        }
    }

    private fun showContentScreen() {
        val currentFragment = childFragmentManager.findFragmentByTag("visit_history_content_fragment")
        if (currentFragment?.isVisible != true) {
            binding.ablFVisitHistory.liftOnScrollTargetViewId = R.id.rv_f_novel_list
            childFragmentManager.beginTransaction().replace(
                R.id.fcv_f_visit_history,
                VisitHistoryContentFragment(),
                "visit_history_content_fragment"
            ).commit()
        }
    }

    private fun showEmptyScreen() {
        val currentFragment = childFragmentManager.findFragmentByTag("empty_view")
        if (currentFragment?.isVisible != true) {
            childFragmentManager.beginTransaction().replace(
                R.id.fcv_f_visit_history,
                EmptyView(),
                "empty_view"
            ).commit()
        }
    }
}