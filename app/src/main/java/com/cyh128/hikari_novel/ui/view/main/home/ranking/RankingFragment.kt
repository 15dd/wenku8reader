package com.cyh128.hikari_novel.ui.view.main.home.ranking

import android.os.Bundle
import android.view.View
import com.cyh128.hikari_novel.R
import com.cyh128.hikari_novel.base.BaseFragment
import com.cyh128.hikari_novel.databinding.FragmentRankingBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class RankingFragment: BaseFragment<FragmentRankingBinding>() {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.actFRanking.setOnItemClickListener { parent, _, position, _ ->
            childFragmentManager.beginTransaction().replace(
                R.id.fcv_f_ranking,
                RankingContentFragment().apply {
                    arguments = Bundle().apply { putString("ranking", parent.getItemAtPosition(position) as String) }
                }
            ).commit()
        }
    }
}