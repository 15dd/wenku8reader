package com.cyh128.hikari_novel.ui.view.main.bookshelf

import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.cyh128.hikari_novel.base.BaseFragment
import com.cyh128.hikari_novel.data.model.Event
import com.cyh128.hikari_novel.databinding.FragmentNovelListBinding
import com.cyh128.hikari_novel.ui.view.detail.NovelInfoActivity
import com.cyh128.hikari_novel.ui.view.main.BookshelfListAdapter
import com.cyh128.hikari_novel.util.startActivity
import com.drake.channel.receiveEvent
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class BookshelfContentFragment: BaseFragment<FragmentNovelListBinding>() {
    private val viewModel by lazy { ViewModelProvider(requireActivity())[BookshelfViewModel::class] }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val adapter = BookshelfListAdapter(viewModel.bookshelfList.value) { aid ->
            startActivity<NovelInfoActivity> {
                putExtra("aid", aid)
            }
        }

        receiveEvent<Event>("event_bookshelf_content_fragment") { event ->
            if (event == Event.LoadSuccessEvent) {
                adapter.notifyDataSetChanged()
                binding.srlFNovelList.isRefreshing = false
            }
        }

        binding.rvFNovelList.apply {
            layoutManager = GridLayoutManager(context, 3)
            this.adapter = adapter
        }

        binding.srlFNovelList.setOnRefreshListener {
            viewModel.getBookshelfData()
        }

        if (viewModel.bookshelfList.value.isEmpty()) {
            binding.srlFNovelList.isRefreshing = true
            viewModel.getBookshelfData()
        }
    }
}