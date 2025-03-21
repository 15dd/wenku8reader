package com.cyh128.hikari_novel.ui.splash

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
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
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
                        getAllBookshelf()
                    } else if (!success.isCorrect()) { //当密码错误时
                        if (!success.isUsernameCorrect && !success.isPasswordCorrect) {
                            sendEvent(Event.LogInFailureEvent, "event_login_activity")
                        } else {
                            _isUsernameCorrect.postValue(success.isUsernameCorrect)
                            _isPasswordCorrect.postValue(success.isPasswordCorrect)
                            _isCodeCorrect.postValue(success.isCodeCorrect)
                            sendEvent(Event.AuthFailedEvent, "event_login_activity")
                        }
                    } else sendEvent(Event.LogInFailureEvent, "event_login_activity")
                }.onFailure { failure ->
                    sendEvent(Event.NetworkErrorEvent(failure.message), "event_login_activity")
                }
        }
    }

    //保存账户用户名和密码
    fun saveLoginInfo(username: String, password: String) {
        wenku8Repository.username = username
        wenku8Repository.password = password
    }

    private fun getAllBookshelf() {
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
                            sendEvent(Event.NetworkErrorEvent(failure.message), "event_login_activity")
                            cancel() //取消协程
                        }
                }
            }

            val result = list.awaitAll().all { it as Boolean }
            if (result) sendEvent(Event.LogInSuccessEvent, "event_login_activity")
        }
    }

    fun getWenku8Node() = wenku8Repository.getWenku8Node()
}