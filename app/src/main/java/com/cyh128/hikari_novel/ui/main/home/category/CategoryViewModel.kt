package com.cyh128.hikari_novel.ui.main.home.category

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cyh128.hikari_novel.data.model.Event
import com.cyh128.hikari_novel.data.model.LoadMode
import com.cyh128.hikari_novel.data.model.NovelCover
import com.cyh128.hikari_novel.data.repository.Wenku8Repository
import com.cyh128.hikari_novel.util.urlEncode
import com.drake.channel.sendEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CategoryViewModel @Inject constructor(
    private val wenku8Repository: Wenku8Repository
) : ViewModel() {
    private var currentIndex: Int = 0
    private var maxNum: Int? = null //总页数

    val pager: MutableList<NovelCover> = mutableListOf()

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
                sendEvent(Event.LoadSuccessEvent,"event_category_content_fragment")
            }.onFailure { failure ->
                --currentIndex
                sendEvent(Event.NetworkErrorEvent(failure.message),"event_category_content_fragment")
            }
        }
    }


    fun haveMore() = maxNum == null || currentIndex < maxNum!!

    fun getWenku8Node() = wenku8Repository.getWenku8Node()
}