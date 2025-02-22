package com.cyh128.hikari_novel.ui.splash

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cyh128.hikari_novel.data.model.Event
import com.cyh128.hikari_novel.data.repository.BookshelfRepository
import com.cyh128.hikari_novel.data.repository.Wenku8Repository
import com.cyh128.hikari_novel.data.source.local.database.bookshelf.BookshelfEntity
import com.drake.channel.sendEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.HttpUrl.Companion.toHttpUrlOrNull
import rxhttp.RxHttpPlugins
import rxhttp.wrapper.cookie.ICookieJar
import java.util.Calendar
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val wenku8Repository: Wenku8Repository,
    private val bookshelfRepository: BookshelfRepository
) : ViewModel() {
    private val _isUsernameCorrect = MutableLiveData<Boolean>()
    val isUsernameCorrect: LiveData<Boolean> = _isUsernameCorrect

    private val _isPasswordCorrect = MutableLiveData<Boolean>()
    val isPasswordCorrect: LiveData<Boolean> = _isPasswordCorrect

    private val _isCodeCorrect = MutableLiveData<Boolean>()
    val isCodeCorrect: LiveData<Boolean> = _isCodeCorrect

    fun login(username: String, password: String, checkcode: String, usecookie: String) {
        viewModelScope.launch(Dispatchers.IO) {
            wenku8Repository.login(username, password, checkcode, usecookie)
                .onSuccess { success ->
                    if (success.isCorrect() && success.isLoginSuccessful) { //当密码正确时并且网页提示登录成功时
                        sendEvent(Event.LogInSuccessEvent,"event_login_activity")
                    } else if (!success.isCorrect()) { //当密码错误时
                        if (!success.isUsernameCorrect && !success.isPasswordCorrect) {
                            sendEvent(Event.LogInFailureEvent,"event_login_activity")
                        } else {
                            _isUsernameCorrect.postValue(success.isUsernameCorrect)
                            _isPasswordCorrect.postValue(success.isPasswordCorrect)
                            _isCodeCorrect.postValue(success.isCodeCorrect)
                            sendEvent(Event.AuthFailedEvent,"event_login_activity")
                        }
                    } else sendEvent(Event.LogInFailureEvent,"event_login_activity")
                }.onFailure { failure ->
                    sendEvent(Event.NetworkErrorEvent(failure.message),"event_login_activity")
                }
        }
    }

    //保存账户用户名和密码
    fun saveLoginInfo(username: String, password: String) {
        wenku8Repository.username = username
        wenku8Repository.password = password
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

    fun getWenku8Node() = wenku8Repository.getWenku8Node()
}