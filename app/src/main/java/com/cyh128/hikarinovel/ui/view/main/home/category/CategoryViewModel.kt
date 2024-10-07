package com.cyh128.hikarinovel.ui.view.main.home.category

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cyh128.hikarinovel.data.model.Event
import com.cyh128.hikarinovel.data.model.LoadMode
import com.cyh128.hikarinovel.data.model.NovelCover
import com.cyh128.hikarinovel.data.repository.Wenku8Repository
import com.cyh128.hikarinovel.util.urlEncode
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CategoryViewModel @Inject constructor(
    private val wenku8Repository: Wenku8Repository
) : ViewModel() {
    private var currentIndex: Int = 0
    private var maxNum: Int? = null //总页数

    val pager: MutableList<NovelCover> = mutableListOf()

    private val _eventFlow = MutableSharedFlow<Event>()
    val eventFlow = _eventFlow.asSharedFlow()

    fun getData(loadMode: LoadMode, sort: String, category: String) {
        viewModelScope.launch(Dispatchers.IO) {
            if (loadMode == LoadMode.REFRESH) {
                currentIndex = 0
                pager.clear()
            }
            ++currentIndex
            wenku8Repository.getNovelByCategory(
                category.urlEncode(),
                sort,
                currentIndex
            ).onSuccess { success ->
                pager.addAll(success.curPage)
                maxNum = success.maxNum
                _eventFlow.emit(Event.LoadSuccessEvent)
            }.onFailure { failure ->
                --currentIndex
                _eventFlow.emit(Event.NetWorkErrorEvent(failure.message))
            }
        }
    }


    fun haveMore() = maxNum == null || currentIndex < maxNum!!

    fun getWenku8Node() = wenku8Repository.getWenku8Node()
}