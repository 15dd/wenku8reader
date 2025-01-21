package com.cyh128.hikari_novel.ui.main.home

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.cyh128.hikari_novel.R
import com.cyh128.hikari_novel.base.BaseFragment
import com.cyh128.hikari_novel.databinding.FragmentHomeBinding
import com.cyh128.hikari_novel.ui.main.home.category.CategoryFragment
import com.cyh128.hikari_novel.ui.main.home.completion.CompletionFragment
import com.cyh128.hikari_novel.ui.main.home.ranking.RankingFragment
import com.cyh128.hikari_novel.ui.main.home.recommend.RecommendFragment
import com.cyh128.hikari_novel.ui.main.home.search.SearchActivity
import com.cyh128.hikari_novel.util.startActivity
import com.google.android.material.tabs.TabLayoutMediator
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeFragment : BaseFragment<FragmentHomeBinding>() {
    private val tabTexts = listOf(R.string.recommendation, R.string.category, R.string.ranking,R.string.completion)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.vpFHome.adapter = object : FragmentStateAdapter(childFragmentManager, lifecycle) {
            override fun getItemCount(): Int = 4
            override fun createFragment(position: Int): Fragment = when(position) {
                0 -> RecommendFragment()
                1 -> CategoryFragment()
                2 -> RankingFragment()
                3 -> CompletionFragment()
                else -> throw IllegalArgumentException()
            }
        }

        TabLayoutMediator(binding.tlFHome, binding.vpFHome) { tab, position ->
            tab.text = getString(tabTexts[position])
        }.attach()

//        binding.vpFHome.isUserInputEnabled = false

        binding.tbFHome.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.menu_f_home_search -> {
                    startActivity<SearchActivity>()
                    true
                }
                else -> false
            }
        }
    }
}