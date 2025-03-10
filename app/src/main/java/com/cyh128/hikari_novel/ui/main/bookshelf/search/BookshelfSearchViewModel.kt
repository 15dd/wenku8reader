package com.cyh128.hikari_novel.ui.main.bookshelf.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cyh128.hikari_novel.data.model.NovelCover
import com.cyh128.hikari_novel.data.repository.BookshelfRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class BookshelfSearchViewModel @Inject constructor(
    private val bookshelfRepository: BookshelfRepository
): ViewModel() {
    var getAllFlow = bookshelfRepository.getAll()

    val searchList = mutableListOf<NovelCover>()
}