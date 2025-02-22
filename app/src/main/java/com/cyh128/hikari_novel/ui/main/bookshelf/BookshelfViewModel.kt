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
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BookshelfViewModel @Inject constructor(
    private val wenku8Repository: Wenku8Repository,
    private val bookshelfRepository: BookshelfRepository
) : ViewModel() {
    var currentBookshelfId = -1

    val searchList = mutableListOf<BookshelfNovelInfo>()

    lateinit var displayList: List<BookshelfNovelInfo>

    val getAllFlow = bookshelfRepository.getAll().flowOn(Dispatchers.IO).stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    val maxCollection get() = bookshelfRepository.getMaxCollection()

    fun getListByClassId() {
        viewModelScope.launch(Dispatchers.IO) {
             displayList = bookshelfRepository.getByClassId(currentBookshelfId).first()!!.map {
                BookshelfNovelInfo(
                    aid = it.aid,
                    bid = it.bid,
                    img = it.img,
                    detailUrl = it.detailUrl,
                    title = it.title
                )
            }
            sendEvent(Event.LoadSuccessEvent,"event_bookshelf_content_fragment")
        }
    }

    //搜索书架
    fun searchBookshelf(keyword: String) {
        viewModelScope.launch(Dispatchers.IO) {
            searchList.clear()
            for (i in bookshelfRepository.getByClassId(currentBookshelfId).first()!!) {
                if (i.title.contains(keyword, true)) {
                    searchList.add(
                        BookshelfNovelInfo(
                            aid = i.aid,
                            bid = i.bid,
                            img = i.img,
                            detailUrl = i.detailUrl,
                            title = i.title
                        )
                    )
                }
            }
            if (searchList.isNotEmpty()) sendEvent(Event.SearchBookshelfSuccessEvent, "event_bookshelf_search_fragment")
            else sendEvent(Event.SearchBookshelfFailureEvent, "event_bookshelf_fragment")
        }
    }

    //批量移除小说
    fun removeNovelFromList(list: List<BookshelfNovelInfo>, classId: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            wenku8Repository.removeNovelFromList(list.map { it.bid }, classId)
                .onSuccess {
                    sendEvent(Event.RemoveNovelFromListSuccessEvent, "event_bookshelf_fragment")
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
                    sendEvent(Event.MoveNovelFromListSuccessEvent, "event_bookshelf_fragment")
                }.onFailure { failure ->
                    sendEvent(Event.NetworkErrorEvent(failure.message), "event_bookshelf_fragment")
                }
        }
    }

    suspend fun getAllBookshelf() {
        bookshelfRepository.deleteAll()

        wenku8Repository.getBookshelf(0)
            .onSuccess { it0 ->
                bookshelfRepository.addAll(
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
                        bookshelfRepository.addAll(
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
                                bookshelfRepository.addAll(
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
                                        bookshelfRepository.addAll(
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
                                                bookshelfRepository.addAll(
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
                                                        bookshelfRepository.addAll(
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

                                                        sendEvent(Event.LoadSuccessEvent, "event_splash_activity")

                                                    }.onFailure { failure ->
                                                        sendEvent(Event.NetworkErrorEvent(failure.message), "event_splash_activity")
                                                    }
                                            }.onFailure { failure ->
                                                sendEvent(Event.NetworkErrorEvent(failure.message), "event_splash_activity")
                                            }
                                    }.onFailure { failure ->
                                        sendEvent(Event.NetworkErrorEvent(failure.message), "event_splash_activity")
                                    }
                            }.onFailure { failure ->
                                sendEvent(Event.NetworkErrorEvent(failure.message), "event_splash_activity")
                            }
                    }.onFailure { failure ->
                        sendEvent(Event.NetworkErrorEvent(failure.message), "event_splash_activity")
                    }
            }.onFailure { failure ->
                sendEvent(Event.NetworkErrorEvent(failure.message), "event_splash_activity")
            }
    }
}