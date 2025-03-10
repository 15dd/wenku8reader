package com.cyh128.hikari_novel.ui.detail.user_bookshelf

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cyh128.hikari_novel.data.model.Event
import com.cyh128.hikari_novel.data.model.SimpleNovelCover
import com.cyh128.hikari_novel.data.repository.Wenku8Repository
import com.drake.channel.sendEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UserBookshelfViewModel @Inject constructor(
    private val wenku8Repository: Wenku8Repository
): ViewModel() {
    lateinit var uid: String
    val list = mutableListOf<SimpleNovelCover>()

    fun getUserBookshelf() {
        viewModelScope.launch(Dispatchers.IO) {
            list.clear()
            wenku8Repository.getBookshelfFromUser(uid)
                .onSuccess { success ->
                    list.addAll(success)
                    sendEvent(Event.LoadSuccessEvent, "event_user_bookshelf_activity")
                }.onFailure { failure ->
                    sendEvent(Event.NetworkErrorEvent(failure.message), "event_user_bookshelf_activity")
                }
        }
    }
}