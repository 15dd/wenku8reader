package com.cyh128.hikari_novel.ui.view.main.more.more.account

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cyh128.hikari_novel.data.model.Event
import com.cyh128.hikari_novel.data.model.SignedInException
import com.cyh128.hikari_novel.data.model.TempSignInException
import com.cyh128.hikari_novel.data.model.UserInfo
import com.cyh128.hikari_novel.data.repository.Wenku8Repository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class AccountViewModel @Inject constructor(
    private val wenku8Repository: Wenku8Repository
) : ViewModel() {
    lateinit var userInfo: UserInfo

    private val _eventFlow = MutableSharedFlow<Event>()
    val eventFlow = _eventFlow.asSharedFlow()

    fun getUserInfo() {
        viewModelScope.launch(Dispatchers.IO) {
            wenku8Repository.getUserInfo()
                .onSuccess { success ->
                    userInfo = success
                    _eventFlow.emit(Event.LoadSuccessEvent)
                }.onFailure { failure ->
                    _eventFlow.emit(Event.NetWorkErrorEvent(failure.message))
                }
        }
    }

    //退出账号，即清空用户名和密码
    fun clearLoginInfo() {
        wenku8Repository.username = null
        wenku8Repository.password = null
    }

    fun signIn() {
        viewModelScope.launch(Dispatchers.IO) {
            wenku8Repository.loginWenku8Com()
                .onSuccess {
                    signInAfterLoggedIn()
                }.onFailure { failure ->
                    if (failure is TempSignInException) _eventFlow.emit(Event.TempSignInUnableEvent)
                    else _eventFlow.emit(Event.NetWorkErrorEvent(failure.message))
                }
        }
    }

    private suspend fun signInAfterLoggedIn() {
        wenku8Repository.sign()
            .onSuccess {
                _eventFlow.emit(Event.SignInSuccessEvent)
            }.onFailure { failure ->
                if (failure is SignedInException) _eventFlow.emit(Event.SignInFailureEvent)
                else _eventFlow.emit(Event.NetWorkErrorEvent(failure.message))
            }
    }
}