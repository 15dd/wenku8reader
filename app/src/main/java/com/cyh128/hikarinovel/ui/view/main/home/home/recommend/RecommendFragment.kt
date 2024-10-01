package com.cyh128.hikarinovel.ui.view.main.home.home.recommend

import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProvider
import com.cyh128.hikarinovel.R
import com.cyh128.hikarinovel.base.BaseFragment
import com.cyh128.hikarinovel.data.model.Event
import com.cyh128.hikarinovel.databinding.FragmentRecommendBinding
import com.cyh128.hikarinovel.ui.view.other.LoadingView
import com.cyh128.hikarinovel.ui.view.other.MessageView
import com.cyh128.hikarinovel.util.launchWithLifecycle
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class RecommendFragment: BaseFragment<FragmentRecommendBinding>() {
    private val viewModel by lazy { ViewModelProvider(requireActivity())[RecommendViewModel::class.java] }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        launchWithLifecycle {
            viewModel.eventFlow.collect { event ->
                when(event) {
                    Event.LoadSuccessEvent -> showContentScreen()
                    is Event.NetWorkErrorEvent -> showMessageScreen(event.msg)
                    else -> {}
                }
            }
        }

        if (viewModel.homeBlockList.isNullOrEmpty()) {
            showLoadingScreen()
            viewModel.getData()
        }
    }

    private fun showContentScreen() {
        childFragmentManager.beginTransaction().replace(
            R.id.fcv_f_recommend,
            RecommendContentFragment()
        ).commitAllowingStateLoss()
    }

    private fun showLoadingScreen() {
        childFragmentManager.beginTransaction().replace(
            R.id.fcv_f_recommend,
            LoadingView()
        ).commitAllowingStateLoss()
    }

    private fun showMessageScreen(msg: String?) {
        childFragmentManager.beginTransaction().replace(
            R.id.fcv_f_recommend,
            MessageView().apply { arguments = Bundle().apply { putString("msg",msg) } }
        ).commitAllowingStateLoss()
    }

}