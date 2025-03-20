package com.cyh128.hikari_novel.ui.main.bookshelf.search

import androidx.lifecycle.ViewModel
import com.cyh128.hikari_novel.data.model.NovelCover
import com.cyh128.hikari_novel.data.repository.AppRepository
import com.cyh128.hikari_novel.data.repository.BookshelfRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class BookshelfSearchViewModel @Inject constructor(
    private val bookshelfRepository: BookshelfRepository,
    private val appRepository: AppRepository
): ViewModel() {
    var getAllFlow = bookshelfRepository.getAll()

    val searchList = mutableListOf<NovelCover>()

    val listViewType get() = appRepository.getListViewType()
}