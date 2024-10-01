package com.cyh128.hikarinovel.ui.view.main.home.home.recommend

import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.cyh128.hikarinovel.base.BaseFragment
import com.cyh128.hikarinovel.databinding.FragmentRecommendContentBinding
import com.cyh128.hikarinovel.ui.view.detail.NovelInfoActivity
import com.cyh128.hikarinovel.ui.view.main.home.HomeBlockAdapter
import com.cyh128.hikarinovel.util.startActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class RecommendContentFragment: BaseFragment<FragmentRecommendContentBinding>() {
    private val viewModel by lazy { ViewModelProvider(requireActivity())[RecommendViewModel::class.java] }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (viewModel.homeBlockList.isNullOrEmpty()) return

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