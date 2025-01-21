package com.cyh128.hikari_novel.ui.splash

import android.util.Log
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

    //刷新书架列表
    suspend fun refreshBookshelfList() {
        wenku8Repository.getBookshelf()
            .onSuccess { success ->
                bookshelfRepository.updateBookshelfList(success)
            }.onFailure { failure ->
                sendEvent(Event.NetworkErrorEvent(failure.message),"event_login_activity")
            }
    }

    //算出时间戳
    fun calculateTimestampFromSeconds(seconds: Long): Long {
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.SECOND, seconds.toInt())
        return calendar.timeInMillis / 1000
    }

    fun getWenku8Node() = wenku8Repository.getWenku8Node()

    fun getCookie(): String {
        var cookie = ""
        val iCookieJar = RxHttpPlugins.getOkHttpClient().cookieJar as ICookieJar
        val httpUrl = "https://${getWenku8Node()}".toHttpUrlOrNull()
        val cookies = iCookieJar.loadCookie(httpUrl)
        cookies.forEach {
            Log.d("l_v_m","name:${it.name} value:${it.value}")
            cookie += it.name + "=" + it.value + ";"
        }
        return cookie
    }
}