package com.cyh128.hikari_novel.ui.view.main.bookshelf

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cyh128.hikari_novel.data.model.BookshelfNovelInfo
import com.cyh128.hikari_novel.data.model.Event
import com.cyh128.hikari_novel.data.repository.BookshelfRepository
import com.cyh128.hikari_novel.data.repository.Wenku8Repository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BookshelfViewModel @Inject constructor(
    private val wenku8Repository: Wenku8Repository,
    private val bookshelfRepository: BookshelfRepository
) : ViewModel() {
    val searchList: MutableList<BookshelfNovelInfo> = mutableListOf()

    val bookshelfList = bookshelfRepository.bookshelfList

    private val _eventFlow = MutableSharedFlow<Event>()
    val eventFlow = _eventFlow.asSharedFlow()

    //获取书架内容
    fun getBookshelfData() {
        viewModelScope.launch(Dispatchers.IO) {
            wenku8Repository.getBookshelf()
                .onSuccess { success ->
                    bookshelfRepository.updateBookshelfList(success)
                    _eventFlow.emit(Event.LoadSuccessEvent)
                }.onFailure { failure ->
                    _eventFlow.emit(Event.NetWorkErrorEvent(failure.message))
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
            if (searchList.isNotEmpty()) _eventFlow.emit(Event.SearchBookshelfSuccessEvent)
            else _eventFlow.emit(Event.SearchBookshelfFailureEvent)
        }
    }
}