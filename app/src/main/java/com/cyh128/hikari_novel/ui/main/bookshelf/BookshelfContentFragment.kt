package com.cyh128.hikari_novel.ui.main.bookshelf

import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.withStarted
import com.cyh128.hikari_novel.R
import com.cyh128.hikari_novel.base.BaseFragment
import com.cyh128.hikari_novel.data.model.BookshelfNovelInfo
import com.cyh128.hikari_novel.databinding.FragmentBookshelfContentBinding
import com.cyh128.hikari_novel.ui.other.EmptyView
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.count
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.properties.Delegates

@AndroidEntryPoint
class BookshelfContentFragment : BaseFragment<FragmentBookshelfContentBinding>() {
    private val viewModel by lazy { ViewModelProvider(this)[BookshelfViewModel::class] }
    private var currentBookshelfId by Delegates.notNull<Int>()
    val displayList = mutableListOf<BookshelfNovelInfo>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        currentBookshelfId = requireArguments().getInt("classId")

        //此处必须使用<Flow>.onEach{}.launchIn()方法，不要使用<Flow>.collect{}方法
        //否则当别的逻辑访问room数据库时，会误触发此处的collect()方法
        viewModel.getByClassIdFlow(currentBookshelfId).onEach { bookshelfEntityList ->
            lifecycle.withStarted {
                if (bookshelfEntityList.isNullOrEmpty()) {
                    showEmptyScreen()
                } else {
                    displayList.clear()
                    displayList.addAll(
                        bookshelfEntityList.map {
                            BookshelfNovelInfo(
                                aid = it.aid,
                                bid = it.bid,
                                img = it.img,
                                detailUrl = it.detailUrl,
                                title = it.title
                            )
                        }
                    )
                    showContentScreen()
                }
                lifecycleScope.launch {
                    binding.tvFBookshelfContent.text = getString(R.string.bookshelf_show, bookshelfEntityList?.size, viewModel.getAllFlow.take(1).first()?.size ,viewModel.maxCollection)
                }
            }
        }.launchIn(lifecycleScope)
    }

    private fun showContentScreen() {
        childFragmentManager.beginTransaction().replace(
            R.id.fcv_f_bookshelf_content,
            BookshelfListFragment().apply {
                arguments = Bundle().apply { putInt("classId", currentBookshelfId) }
            }
        ).commit()
    }

    private fun showEmptyScreen() {
        childFragmentManager.beginTransaction().replace(
            R.id.fcv_f_bookshelf_content,
            EmptyView()
        ).commit()
    }
}