package com.cyh128.hikari_novel.ui.main.bookshelf

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cyh128.hikari_novel.data.model.BookshelfNovelInfo
import com.cyh128.hikari_novel.data.model.Event
import com.cyh128.hikari_novel.data.repository.AppRepository
import com.cyh128.hikari_novel.data.repository.BookshelfRepository
import com.cyh128.hikari_novel.data.repository.Wenku8Repository
import com.cyh128.hikari_novel.data.source.local.database.bookshelf.BookshelfEntity
import com.drake.channel.sendEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BookshelfViewModel @Inject constructor(
    private val wenku8Repository: Wenku8Repository,
    private val appRepository: AppRepository,
    private val bookshelfRepository: BookshelfRepository
) : ViewModel() {
    var getAllFlow = bookshelfRepository.getAll()

    fun getByClassIdFlow(classId: Int) = bookshelfRepository.getByClassId(classId)

    val maxCollection get() = bookshelfRepository.getMaxCollection()

    val listViewType get() = appRepository.getListViewType()

    //批量移除小说
    fun removeNovelFromList(list: List<BookshelfNovelInfo>, classId: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            wenku8Repository.removeNovelFromList(list.map { it.bid }, classId)
                .onSuccess {
                    list.forEach { bookshelfRepository.delete(it.aid) }
                }.onFailure { failure ->
                    sendEvent(Event.NetworkErrorEvent(failure.message), "event_bookshelf_fragment")
                }
        }
    }

    //移动到其它书架
    fun moveNovelToOther(list: List<BookshelfNovelInfo>, classId: Int, newClassId: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            wenku8Repository.moveNovelToOther(list.map { it.bid }, classId, newClassId)
                .onSuccess {
                    list.forEach { b -> bookshelfRepository.updateClassId(b.aid, newClassId) }
                }.onFailure { failure ->
                    sendEvent(Event.NetworkErrorEvent(failure.message), "event_bookshelf_fragment")
                }
        }
    }

    fun getAllBookshelf() {
        viewModelScope.launch {
            bookshelfRepository.deleteAll()

            val list = (0..5).map { classId ->
                async {
                    wenku8Repository.getBookshelf(classId)
                        .onSuccess { success ->
                            bookshelfRepository.upsertAll(
                                success.list.map { info ->
                                    BookshelfEntity(
                                        aid = info.aid,
                                        bid = info.bid,
                                        detailUrl = info.detailUrl,
                                        title = info.title,
                                        img = info.img,
                                        classId = classId
                                    )
                                }
                            )
                            if (classId == 5) {
                                bookshelfRepository.setMaxCollection(success.maxNum)
                            }
                            return@async true
                        }.onFailure { failure ->
                            sendEvent(Event.NetworkErrorEvent(failure.message), "event_bookshelf_fragment")
                            cancel()
                        }
                }
            }

            val result = list.awaitAll().all { it as Boolean }
            if (result) sendEvent(Event.LoadSuccessEvent, "event_bookshelf_fragment")
        }
    }
}