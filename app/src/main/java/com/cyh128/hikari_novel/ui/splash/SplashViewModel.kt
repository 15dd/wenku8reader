package com.cyh128.hikari_novel.ui.splash

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
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
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class SplashViewModel @Inject constructor(
    private val wenku8Repository: Wenku8Repository,
    private val bookshelfRepository: BookshelfRepository,
    private val appRepository: AppRepository
) : ViewModel() {
    private val _loggingInText = MutableLiveData<String>()
    val loggingInText: LiveData<String> = _loggingInText

    fun getCookie() = wenku8Repository.cookie

    //判断是否处于未登录状态
    fun isNotLoggedIn(): Boolean =
         wenku8Repository.username.isNullOrEmpty() ||
         wenku8Repository.password.isNullOrEmpty() ||
         wenku8Repository.cookie.isNullOrEmpty() ||
         wenku8Repository.expDate.let {
            if (it == null) {
                true
            } else {
                it <= System.currentTimeMillis()
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

    fun setLoggingInText(text: String) {
        viewModelScope.launch {
            _loggingInText.value = text
        }
    }

    //获取首次启动状态
    fun getIsFirstLaunch() = appRepository.getIsFirstLaunch()

    //设置首次启动状态
    fun setIsFirstLaunch(isFirstLaunch: Boolean) = appRepository.setIsFirstLaunch(isFirstLaunch)

}