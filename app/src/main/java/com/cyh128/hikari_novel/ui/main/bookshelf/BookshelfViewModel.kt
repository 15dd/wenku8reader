package com.cyh128.hikari_novel.ui.main.bookshelf

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cyh128.hikari_novel.data.model.BookshelfNovelInfo
import com.cyh128.hikari_novel.data.model.Event
import com.cyh128.hikari_novel.data.repository.BookshelfRepository
import com.cyh128.hikari_novel.data.repository.Wenku8Repository
import com.drake.channel.sendEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BookshelfViewModel @Inject constructor(
    private val wenku8Repository: Wenku8Repository,
    private val bookshelfRepository: BookshelfRepository
) : ViewModel() {
    val searchList: MutableList<BookshelfNovelInfo> = mutableListOf()

    val bookshelfList = bookshelfRepository.bookshelfList

    //获取书架内容
    fun getBookshelfData() {
        viewModelScope.launch(Dispatchers.IO) {
            wenku8Repository.getBookshelf()
                .onSuccess { success ->
                    bookshelfRepository.updateBookshelfList(success)
                    sendEvent(Event.LoadSuccessEvent, "event_bookshelf_content_fragment")
                }.onFailure { failure ->
                    sendEvent(Event.NetworkErrorEvent(failure.message), "event_bookshelf_fragment")
                }
        }
    }

    //搜索书架
    fun searchBookshelf(keyword: String) {
        viewModelScope.launch(Dispatchers.IO) {
            searchList.clear()
            for (i in bookshelfList.value) {
                if (i.title.contains(keyword, true)) {
                    searchList.add(i)
                }
            }
            if (searchList.isNotEmpty()) sendEvent(Event.SearchBookshelfSuccessEvent, "event_bookshelf_search_fragment")
            else sendEvent(Event.SearchBookshelfFailureEvent, "event_bookshelf_fragment")
        }
    }
}