package com.cyh128.hikari_novel.ui.main.bookshelf

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cyh128.hikari_novel.data.model.BookshelfNovelInfo
import com.cyh128.hikari_novel.data.model.Event
import com.cyh128.hikari_novel.data.repository.BookshelfRepository
import com.cyh128.hikari_novel.data.repository.Wenku8Repository
import com.cyh128.hikari_novel.data.source.local.database.bookshelf.BookshelfEntity
import com.drake.channel.sendEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BookshelfViewModel @Inject constructor(
    private val wenku8Repository: Wenku8Repository,
    private val bookshelfRepository: BookshelfRepository
) : ViewModel() {
    var getAllFlow = bookshelfRepository.getAll()

    fun getByClassIdFlow(classId: Int) = bookshelfRepository.getByClassId(classId)

    val maxCollection get() = bookshelfRepository.getMaxCollection()

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

    suspend fun getAllBookshelf() {
        bookshelfRepository.deleteAll()

        wenku8Repository.getBookshelf(0)
            .onSuccess { it0 ->
                bookshelfRepository.upsertAll(
                    it0.list.map { info ->
                        BookshelfEntity(
                            aid = info.aid,
                            bid = info.bid,
                            detailUrl = info.detailUrl,
                            title = info.title,
                            img = info.img,
                            classId = 0
                        )
                    }
                )
                wenku8Repository.getBookshelf(1)
                    .onSuccess { it1 ->
                        bookshelfRepository.upsertAll(
                            it1.list.map { info ->
                                BookshelfEntity(
                                    aid = info.aid,
                                    bid = info.bid,
                                    detailUrl = info.detailUrl,
                                    title = info.title,
                                    img = info.img,
                                    classId = 1
                                )
                            }
                        )
                        wenku8Repository.getBookshelf(2)
                            .onSuccess { it2 ->
                                bookshelfRepository.upsertAll(
                                    it2.list.map { info ->
                                        BookshelfEntity(
                                            aid = info.aid,
                                            bid = info.bid,
                                            detailUrl = info.detailUrl,
                                            title = info.title,
                                            img = info.img,
                                            classId = 2
                                        )
                                    }
                                )
                                wenku8Repository.getBookshelf(3)
                                    .onSuccess { it3 ->
                                        bookshelfRepository.upsertAll(
                                            it3.list.map { info ->
                                                BookshelfEntity(
                                                    aid = info.aid,
                                                    bid = info.bid,
                                                    detailUrl = info.detailUrl,
                                                    title = info.title,
                                                    img = info.img,
                                                    classId = 3
                                                )
                                            }
                                        )
                                        wenku8Repository.getBookshelf(4)
                                            .onSuccess { it4 ->
                                                bookshelfRepository.upsertAll(
                                                    it4.list.map { info ->
                                                        BookshelfEntity(
                                                            aid = info.aid,
                                                            bid = info.bid,
                                                            detailUrl = info.detailUrl,
                                                            title = info.title,
                                                            img = info.img,
                                                            classId = 4
                                                        )
                                                    }
                                                )
                                                wenku8Repository.getBookshelf(5)
                                                    .onSuccess { it5 ->
                                                        bookshelfRepository.upsertAll(
                                                            it5.list.map { info ->
                                                                BookshelfEntity(
                                                                    aid = info.aid,
                                                                    bid = info.bid,
                                                                    detailUrl = info.detailUrl,
                                                                    title = info.title,
                                                                    img = info.img,
                                                                    classId = 5
                                                                )
                                                            }
                                                        )

                                                        bookshelfRepository.setMaxCollection(it5.maxNum)

                                                        sendEvent(Event.LoadSuccessEvent, "event_bookshelf_content_fragment")
                                                    }.onFailure { failure ->
                                                        sendEvent(
                                                            Event.NetworkErrorEvent(failure.message),
                                                            "event_bookshelf_content_fragment"
                                                        )
                                                    }
                                            }.onFailure { failure ->
                                                sendEvent(
                                                    Event.NetworkErrorEvent(failure.message),
                                                    "event_bookshelf_content_fragment"
                                                )
                                            }
                                    }.onFailure { failure ->
                                        sendEvent(
                                            Event.NetworkErrorEvent(failure.message),
                                            "event_bookshelf_content_fragment"
                                        )
                                    }
                            }.onFailure { failure ->
                                sendEvent(
                                    Event.NetworkErrorEvent(failure.message),
                                    "event_bookshelf_content_fragment"
                                )
                            }
                    }.onFailure { failure ->
                        sendEvent(
                            Event.NetworkErrorEvent(failure.message),
                            "event_bookshelf_content_fragment"
                        )
                    }
            }.onFailure { failure ->
                sendEvent(
                    Event.NetworkErrorEvent(failure.message),
                    "event_bookshelf_content_fragment"
                )
            }
    }
}