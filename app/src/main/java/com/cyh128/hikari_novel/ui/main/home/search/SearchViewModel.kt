package com.cyh128.hikari_novel.ui.main.home.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cyh128.hikari_novel.data.model.Event
import com.cyh128.hikari_novel.data.model.InFiveSecondException
import com.cyh128.hikari_novel.data.model.NetworkException
import com.cyh128.hikari_novel.data.model.NovelCover
import com.cyh128.hikari_novel.data.model.SearchMode
import com.cyh128.hikari_novel.data.repository.AppRepository
import com.cyh128.hikari_novel.data.repository.SearchHistoryRepository
import com.cyh128.hikari_novel.data.repository.Wenku8Repository
import com.cyh128.hikari_novel.data.source.local.database.search_history.SearchHistoryEntity
import com.cyh128.hikari_novel.util.urlEncode
import com.drake.channel.sendEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val wenku8Repository: Wenku8Repository,
    private val appRepository: AppRepository,
    private val searchHistoryRepository: SearchHistoryRepository
) : ViewModel() {
    private var currentIndex: Int = 0
    private var maxNum: Int? = null //总页数

    val listViewType get() = appRepository.getListViewType()

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
                    sendEvent(Event.SearchResultEmptyEvent, "event_search_activity")
                } else {
                    pager.addAll(success.curPage)
                    maxNum = success.maxNum
                    if (isInit) sendEvent(Event.SearchInitSuccessEvent,"event_search_activity")
                    else sendEvent(Event.LoadSuccessEvent,"event_search_content_fragment")
                }
            }.onFailure { failure ->
                when (failure) {
                    is InFiveSecondException -> {
                        if (isInit) {
                            sendEvent(Event.SearchInitErrorCauseByInFiveSecondEvent,"event_search_activity")
                        } else {
                            sendEvent(Event.SearchLoadErrorCauseByInFiveSecondEvent,"event_search_content_fragment")
                        }
                        --currentIndex
                    }

                    is NetworkException -> {
                        sendEvent(Event.NetworkErrorEvent(failure.message),"event_search_activity")
                        sendEvent(Event.NetworkErrorEvent(failure.message),"event_search_content_fragment")
                    }
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
                    sendEvent(Event.RefreshSearchHistoryEvent,"event_search_activity")
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

            sendEvent(Event.RefreshSearchHistoryEvent,"event_search_activity")
        }
    }
}