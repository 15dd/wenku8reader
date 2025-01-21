package com.cyh128.hikari_novel.ui.main.more.more.account

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cyh128.hikari_novel.data.model.Event
import com.cyh128.hikari_novel.data.model.SignedInException
import com.cyh128.hikari_novel.data.model.TempSignInException
import com.cyh128.hikari_novel.data.model.UserInfo
import com.cyh128.hikari_novel.data.repository.Wenku8Repository
import com.drake.channel.sendEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class AccountViewModel @Inject constructor(
    private val wenku8Repository: Wenku8Repository
) : ViewModel() {
    lateinit var userInfo: UserInfo

    fun getUserInfo() {
        viewModelScope.launch(Dispatchers.IO) {
            wenku8Repository.getUserInfo()
                .onSuccess { success ->
                    userInfo = success
                    sendEvent(Event.LoadSuccessEvent,"event_account_activity")
                }.onFailure { failure ->
                    sendEvent(Event.NetworkErrorEvent(failure.message),"event_account_activity")
                }
        }
    }

    //退出账号，即清空用户名和密码
    fun clearLoginInfo() {
        wenku8Repository.username = null
        wenku8Repository.password = null
        wenku8Repository.expDate = null
        wenku8Repository.cookie = null
    }

    fun signIn() {
        viewModelScope.launch(Dispatchers.IO) {
            wenku8Repository.loginWenku8Com()
                .onSuccess {
                    signInAfterLoggedIn()
                }.onFailure { failure ->
                    if (failure is TempSignInException) sendEvent(Event.TempSignInUnableEvent,"event_account_activity")
                    else sendEvent(Event.NetworkErrorEvent(failure.message),"event_account_activity")
                }
        }
    }

    private suspend fun signInAfterLoggedIn() {
        wenku8Repository.sign()
            .onSuccess {
                sendEvent(Event.SignInSuccessEvent,"event_account_activity")
            }.onFailure { failure ->
                if (failure is SignedInException) sendEvent(Event.SignInFailureEvent,"event_account_activity")
                else sendEvent(Event.NetworkErrorEvent(failure.message),"event_account_activity")
            }
    }
}