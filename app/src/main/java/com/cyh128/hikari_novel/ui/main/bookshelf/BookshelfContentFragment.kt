package com.cyh128.hikari_novel.ui.main.bookshelf

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.cyh128.hikari_novel.R
import com.cyh128.hikari_novel.base.BaseFragment
import com.cyh128.hikari_novel.data.model.BookshelfNovelInfo
import com.cyh128.hikari_novel.data.model.Event
import com.cyh128.hikari_novel.databinding.FragmentBookshelfContentBinding
import com.cyh128.hikari_novel.databinding.FragmentNovelListBinding
import com.cyh128.hikari_novel.ui.detail.NovelInfoActivity
import com.cyh128.hikari_novel.ui.main.BookshelfListAdapter
import com.cyh128.hikari_novel.ui.other.EmptyView
import com.cyh128.hikari_novel.util.startActivity
import com.drake.channel.receiveEvent
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class BookshelfContentFragment : BaseFragment<FragmentBookshelfContentBinding>() {
    private val viewModel by lazy { ViewModelProvider(this)[BookshelfViewModel::class] }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.currentBookshelfId = requireArguments().getInt("classId")
        viewModel.getListByClassId()

        receiveEvent<Event>("event_bookshelf_content_fragment") { event ->
            if (event == Event.LoadSuccessEvent) {
                if (viewModel.displayList.isEmpty()) showEmptyScreen()
                else showContentScreen()
            }
        }

//        binding.root.setOnRefreshListener {
//            //TODO
////            viewModel.getBookshelfData()
//        }
    }

    private fun showContentScreen() {
        childFragmentManager.beginTransaction().replace(
            R.id.fcv_f_bookshelf_content,
            BookshelfListFragment()
        ).commit()
    }

    private fun showEmptyScreen() {
        childFragmentManager.beginTransaction().replace(
            R.id.fcv_f_bookshelf_content,
            EmptyView()
        ).commit()
    }
}