package com.cyh128.hikarinovel.ui.view.main.main.home.recommend

import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.cyh128.hikarinovel.R
import com.cyh128.hikarinovel.base.BaseFragment
import com.cyh128.hikarinovel.data.model.Event
import com.cyh128.hikarinovel.databinding.FragmentRecommendBinding
import com.cyh128.hikarinovel.ui.view.detail.NovelInfoActivity
import com.cyh128.hikarinovel.ui.view.main.main.HomeBlockAdapter
import com.cyh128.hikarinovel.util.launchWithLifecycle
import com.cyh128.hikarinovel.util.startActivity
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class RecommendFragment: BaseFragment<FragmentRecommendBinding>() {
    private val viewModel by lazy { ViewModelProvider(requireActivity())[RecommendViewModel::class.java] }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        launchWithLifecycle {
            viewModel.eventFlow.collect { event ->
                when(event) {
                    Event.LoadSuccessEvent -> {
                        binding.srlFRecommend.isRefreshing = false
                        showContent()
                    }
                    is Event.NetWorkErrorEvent -> {
                        MaterialAlertDialogBuilder(requireContext())
                            .setTitle(R.string.network_error)
                            .setIcon(R.drawable.ic_error)
                            .setMessage(event.msg)
                            .setCancelable(false)
                            .setPositiveButton(R.string.ok) { _, _ -> }
                            .show()
                        binding.srlFRecommend.isRefreshing = false
                    }
                    else -> {}
                }
            }
        }

        binding.srlFRecommend.setOnRefreshListener {
            viewModel.getData()
        }

        if (viewModel.homeBlockList.isNullOrEmpty()) {
            binding.srlFRecommend.isRefreshing = true
            viewModel.getData()
        } else {
            showContent()
        }
    }

    private fun showContent() {
        binding.tvFRecommendTitle1.text = viewModel.homeBlockList!![0].title
        binding.rvFRecommendBlock1.apply {
            adapter = HomeBlockAdapter(viewModel.homeBlockList!![0]) { aid ->
                startActivity<NovelInfoActivity> {
                    putExtra("aid", aid)
                }
            }
            layoutManager = GridLayoutManager(requireContext(), 3)
        }

        binding.tvFRecommendTitle2.text = viewModel.homeBlockList!![1].title
        binding.rvFRecommendBlock2.apply {
            adapter = HomeBlockAdapter(viewModel.homeBlockList!![1]) { aid ->
                startActivity<NovelInfoActivity> {
                    putExtra("aid", aid)
                }
            }
            layoutManager = GridLayoutManager(requireContext(), 3)
        }

        binding.tvFRecommendTitle3.text = viewModel.homeBlockList!![2].title
        binding.rvFRecommendBlock3.apply {
            adapter = HomeBlockAdapter(viewModel.homeBlockList!![2]) { aid ->
                startActivity<NovelInfoActivity> {
                    putExtra("aid", aid)
                }
            }
            layoutManager = GridLayoutManager(requireContext(), 3)
        }

        binding.tvFRecommendTitle4.text = viewModel.homeBlockList!![3].title
        binding.rvFRecommendBlock4.apply {
            adapter = HomeBlockAdapter(viewModel.homeBlockList!![3]) { aid ->
                startActivity<NovelInfoActivity> {
                    putExtra("aid", aid)
                }
            }
            layoutManager = GridLayoutManager(requireContext(), 3)
        }

        binding.tvFRecommendTitle5.text = viewModel.homeBlockList!![4].title
        binding.rvFRecommendBlock5.apply {
            adapter = HomeBlockAdapter(viewModel.homeBlockList!![4]) { aid ->
                startActivity<NovelInfoActivity> {
                    putExtra("aid", aid)
                }
            }
            layoutManager = GridLayoutManager(requireContext(), 3)
        }
    }
}