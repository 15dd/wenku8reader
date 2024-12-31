package com.cyh128.hikari_novel.ui.view.splash

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cyh128.hikari_novel.data.model.Event
import com.cyh128.hikari_novel.data.repository.BookshelfRepository
import com.cyh128.hikari_novel.data.repository.Wenku8Repository
import com.drake.channel.sendEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
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

    fun login(username: String, password: String) {
        viewModelScope.launch(Dispatchers.IO) {
            wenku8Repository.login(username, password)
                .onSuccess { success ->
                    if (success.isCorrect() && success.isLoginSuccessful) { //当密码正确时并且网页提示登录成功时
                        sendEvent(Event.LogInSuccessEvent,"event_login_activity")
                    } else if (!success.isCorrect() && !success.isLoginSuccessful) { //当密码不正确时并且网页没有提示登录成功时
                        sendEvent(Event.LogInFailureEvent, "event_login_activity")
                    } else if (!success.isCorrect()) { //当密码错误时
                        _isUsernameCorrect.postValue(success.isUsernameCorrect)
                        _isPasswordCorrect.postValue(success.isPasswordCorrect)
                        sendEvent(Event.AuthFailedEvent,"event_login_activity")
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

    //刷新书架列表
    suspend fun refreshBookshelfList() {
        wenku8Repository.getBookshelf()
            .onSuccess { success ->
                bookshelfRepository.updateBookshelfList(success)
            }.onFailure { failure ->
                sendEvent(Event.NetworkErrorEvent(failure.message),"event_login_activity")
            }
    }
}