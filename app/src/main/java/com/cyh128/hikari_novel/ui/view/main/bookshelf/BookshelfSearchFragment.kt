package com.cyh128.hikari_novel.ui.view.main.bookshelf

import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.cyh128.hikari_novel.base.BaseFragment
import com.cyh128.hikari_novel.data.model.Event
import com.cyh128.hikari_novel.databinding.FragmentBookshelfSearchBinding
import com.cyh128.hikari_novel.ui.view.detail.NovelInfoActivity
import com.cyh128.hikari_novel.ui.view.main.BookshelfListAdapter
import com.cyh128.hikari_novel.util.launchWithLifecycle
import com.cyh128.hikari_novel.util.startActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class BookshelfSearchFragment : BaseFragment<FragmentBookshelfSearchBinding>() {
    private val viewModel by lazy { ViewModelProvider(requireActivity())[BookshelfViewModel::class] }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val bookshelfListAdapter = BookshelfListAdapter(viewModel.searchList) { aid ->
            startActivity<NovelInfoActivity> {
                putExtra("aid", aid)
            }
        }
        binding.rvFBookshelfSearch.apply {
            adapter = bookshelfListAdapter
            layoutManager = GridLayoutManager(requireContext(), 3)
        }

        launchWithLifecycle {
            viewModel.eventFlow.collect {
                if (it == Event.SearchBookshelfSuccessEvent) {
                    bookshelfListAdapter.notifyDataSetChanged()
                }
            }
        }

        viewModel.searchBookshelf(requireArguments().getString("keyword")!!)
    }
}