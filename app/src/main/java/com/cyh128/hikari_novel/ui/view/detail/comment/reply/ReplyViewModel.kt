package com.cyh128.hikari_novel.ui.view.detail.comment.reply

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cyh128.hikari_novel.data.model.Event
import com.cyh128.hikari_novel.data.model.Reply
import com.cyh128.hikari_novel.data.repository.Wenku8Repository
import com.drake.channel.sendEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ReplyViewModel @Inject constructor(
    private val wenku8Repository: Wenku8Repository
) : ViewModel() {
    private var currentIndex: Int = 0
    var maxNum: Int? = null //总页数
        private set

    val pager: MutableList<Reply> = mutableListOf()

    fun getReply(url: String) {
        viewModelScope.launch(Dispatchers.IO) {
            ++currentIndex
            wenku8Repository.getReply(url, currentIndex)
                .onSuccess { success ->
                    pager.addAll(success.curPage)
                    maxNum = success.maxNum
                    sendEvent(Event.LoadSuccessEvent,"event_reply_fragment")
                }.onFailure { failure ->
                    --currentIndex
                    sendEvent(Event.NetworkErrorEvent(failure.message),"event_reply_fragment")
                }
        }
    }

    fun haveMore() = maxNum == null || currentIndex < maxNum!!
}