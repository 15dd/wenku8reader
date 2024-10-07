package com.cyh128.hikarinovel.ui.view.main.home.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cyh128.hikarinovel.data.model.Event
import com.cyh128.hikarinovel.data.model.InFiveSecondException
import com.cyh128.hikarinovel.data.model.NetworkException
import com.cyh128.hikarinovel.data.model.NovelCover
import com.cyh128.hikarinovel.data.model.SearchMode
import com.cyh128.hikarinovel.data.repository.SearchHistoryRepository
import com.cyh128.hikarinovel.data.repository.Wenku8Repository
import com.cyh128.hikarinovel.data.source.local.database.search_history.SearchHistoryEntity
import com.cyh128.hikarinovel.util.urlEncode
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val wenku8Repository: Wenku8Repository,
    private val searchHistoryRepository: SearchHistoryRepository
) : ViewModel() {
    private val _eventFlow = MutableSharedFlow<Event>()
    val eventFlow = _eventFlow.asSharedFlow()

    private var currentIndex: Int = 0
    private var maxNum: Int? = null //总页数

    val pager: MutableList<NovelCover> = mutableListOf() //指针，勿动

    var historyList: MutableList<String> = mutableListOf() //指针，勿动

    lateinit var keyword: String //搜索词
    lateinit var searchMode: SearchMode //搜索模式 标题或作者

    fun getData(isInit: Boolean) {
        viewModelScope.launch {
            if (isInit) {
                maxNum = null
                pager.clear()
                currentIndex = 0
            }
            ++currentIndex
            when (searchMode) {
                SearchMode.TITLE -> wenku8Repository.searchNovelByTitle(
                    keyword.urlEncode(),
                    currentIndex
                )

                SearchMode.AUTHOR -> wenku8Repository.searchNovelByAuthor(
                    keyword.urlEncode(),
                    currentIndex
                )
            }.onSuccess { success ->
                if (success.curPage.isEmpty()) {
                    _eventFlow.emit(Event.SearchResultEmptyEvent)
                } else {
                    pager.addAll(success.curPage)
                    maxNum = success.maxNum
                    if (isInit) _eventFlow.emit(Event.SearchInitSuccessEvent)
                    else _eventFlow.emit(Event.LoadSuccessEvent)
                }
            }.onFailure { failure ->
                when (failure) {
                    is InFiveSecondException -> {
                        if (isInit) {
                            _eventFlow.emit(Event.SearchInitErrorCauseByInFiveSecondEvent)
                        } else {
                            _eventFlow.emit(Event.SearchLoadErrorCauseByInFiveSecondEvent)
                        }
                        --currentIndex
                    }

                    is NetworkException -> _eventFlow.emit(Event.NetWorkErrorEvent(failure.message))
                }
            }
        }
    }

    fun haveMore() = maxNum == null || currentIndex < maxNum!!

    fun getSearchHistory() {
        viewModelScope.launch(Dispatchers.IO) {
            searchHistoryRepository.getAll().also {
                if (it == null) return@launch
                else {
                    historyList.clear()
                    historyList.addAll(it)
                    historyList.reverse()
                    _eventFlow.emit(Event.RefreshSearchHistoryEvent)
                }
            }
        }
    }

    fun addOrReplaceSearchHistory(searchHistoryEntity: SearchHistoryEntity) {
        viewModelScope.launch(Dispatchers.IO) {
            searchHistoryRepository.addOrReplace(searchHistoryEntity)
            getSearchHistory()
        }
    }

    fun getWenku8Node() = wenku8Repository.getWenku8Node()

    fun deleteAllSearchHistory() {
        viewModelScope.launch(Dispatchers.IO) {
            searchHistoryRepository.deleteAll()
            historyList.clear()

            _eventFlow.emit(Event.RefreshSearchHistoryEvent)
        }
    }
}