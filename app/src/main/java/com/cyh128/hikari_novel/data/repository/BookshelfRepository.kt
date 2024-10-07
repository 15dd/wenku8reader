package com.cyh128.hikari_novel.data.repository

import com.cyh128.hikari_novel.data.model.BookshelfNovelInfo
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BookshelfRepository @Inject constructor() {
    private val _bookshelfList = MutableStateFlow<List<BookshelfNovelInfo>>(mutableListOf())
    val bookshelfList get() = _bookshelfList.asStateFlow()

    fun updateBookshelfList(list: List<BookshelfNovelInfo>) {
        _bookshelfList.value = list
    }
}