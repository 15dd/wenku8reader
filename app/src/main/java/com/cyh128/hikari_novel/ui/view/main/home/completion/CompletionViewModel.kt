package com.cyh128.hikari_novel.ui.view.main.home.completion

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cyh128.hikari_novel.data.model.Event
import com.cyh128.hikari_novel.data.model.LoadMode
import com.cyh128.hikari_novel.data.model.NovelCover
import com.cyh128.hikari_novel.data.repository.Wenku8Repository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CompletionViewModel @Inject constructor(
    private val wenku8Repository: Wenku8Repository
) : ViewModel() {
    private var currentIndex: Int = 0
    private var maxNum: Int? = null //总页数

    val pager: MutableList<NovelCover> = mutableListOf()

    private val _eventFlow = MutableSharedFlow<Event>()
    val eventFlow = _eventFlow.asSharedFlow()

    fun getData(mode: LoadMode) {
        viewModelScope.launch(Dispatchers.IO) {
            if (mode == LoadMode.REFRESH) {
                currentIndex = 0
                pager.clear()
            }
            ++currentIndex
            wenku8Repository.getCompletionNovel(currentIndex)
                .onSuccess { success ->
                    pager.addAll(success.curPage)
                    maxNum = success.maxNum
                    _eventFlow.emit(Event.LoadSuccessEvent)
                }.onFailure {
                    --currentIndex
                    _eventFlow.emit(Event.NetWorkErrorEvent(it.message))
                }
        }
    }

    fun haveMore() = maxNum == null || currentIndex < maxNum!!

    fun getWenku8Node() = wenku8Repository.getWenku8Node()
}