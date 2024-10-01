package com.cyh128.hikarinovel.ui.view.main.home.bookshelf

import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.cyh128.hikarinovel.base.BaseFragment
import com.cyh128.hikarinovel.data.model.Event
import com.cyh128.hikarinovel.databinding.FragmentBookshelfSearchBinding
import com.cyh128.hikarinovel.ui.view.detail.NovelInfoActivity
import com.cyh128.hikarinovel.ui.view.main.home.BookshelfListAdapter
import com.cyh128.hikarinovel.util.launchWithLifecycle
import com.cyh128.hikarinovel.util.startActivity
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