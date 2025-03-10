package com.cyh128.hikari_novel.ui.main.bookshelf.search

import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.cyh128.hikari_novel.base.BaseFragment
import com.cyh128.hikari_novel.databinding.FragmentNovelListBinding
import com.cyh128.hikari_novel.ui.detail.NovelInfoActivity
import com.cyh128.hikari_novel.ui.main.NovelCoverListAdapter
import com.cyh128.hikari_novel.util.startActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class BookshelfSearchContentFragment: BaseFragment<FragmentNovelListBinding>() {
    private val viewModel by lazy { ViewModelProvider(requireActivity())[BookshelfSearchViewModel::class] }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val adapter = NovelCoverListAdapter(viewModel.searchList) { aid ->
            startActivity<NovelInfoActivity> {
                putExtra("aid", aid)
            }
        }
        binding.rvFNovelList.apply {
            layoutManager = GridLayoutManager(requireContext(), 3)
            this.adapter = adapter
        }
    }
}