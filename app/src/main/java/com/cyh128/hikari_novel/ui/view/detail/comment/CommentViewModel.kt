package com.cyh128.hikari_novel.ui.view.detail.comment

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cyh128.hikari_novel.data.model.Comment
import com.cyh128.hikari_novel.data.model.Event
import com.cyh128.hikari_novel.data.model.LoadMode
import com.cyh128.hikari_novel.data.repository.Wenku8Repository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CommentViewModel @Inject constructor(
    private val wenku8Repository: Wenku8Repository
) : ViewModel() {

    private val _eventFlow = MutableSharedFlow<Event>()
    val eventFlow = _eventFlow.asSharedFlow()

    private var maxNum: Int? = null
    val list = mutableListOf<Comment>()

    lateinit var aid: String
    private var currentIndex: Int = 0

    fun getComment(mode: LoadMode) {
        viewModelScope.launch(Dispatchers.IO) {
            if (mode == LoadMode.REFRESH) {
                currentIndex = 0
                list.clear()
            }
            ++currentIndex
            wenku8Repository.getComment(aid, currentIndex)
                .onSuccess { success ->
                    list.addAll(success.list)
                    maxNum = success.maxNum
                    _eventFlow.emit(Event.LoadSuccessEvent)
                }.onFailure { failure ->
                    --currentIndex
                    _eventFlow.emit(Event.NetWorkErrorEvent(failure.message))
                }
        }
    }

    fun haveMore() = maxNum == null || currentIndex < maxNum!!

}