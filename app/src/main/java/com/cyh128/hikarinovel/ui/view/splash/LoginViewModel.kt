package com.cyh128.hikarinovel.ui.view.splash

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cyh128.hikarinovel.data.model.Event
import com.cyh128.hikarinovel.data.repository.BookshelfRepository
import com.cyh128.hikarinovel.data.repository.Wenku8Repository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val wenku8Repository: Wenku8Repository,
    private val bookshelfRepository: BookshelfRepository
) : ViewModel() {
//    var isUsernameCorrect = false
//    var isPasswordCorrect = false
    private val _isUsernameCorrect = MutableLiveData<Boolean>()
    val isUsernameCorrect: LiveData<Boolean> = _isUsernameCorrect

    private val _isPasswordCorrect = MutableLiveData<Boolean>()
    val isPasswordCorrect: LiveData<Boolean> = _isPasswordCorrect

    private val _eventFlow = MutableSharedFlow<Event>()
    val eventFlow = _eventFlow.asSharedFlow()

    fun login(username: String, password: String) {
        viewModelScope.launch(Dispatchers.IO) {
            wenku8Repository.login(username, password)
                .onSuccess { success ->
                    if (success.isCorrect()) {
                        _eventFlow.emit(Event.LogInSuccessEvent)
                    } else {
                        _isUsernameCorrect.postValue(success.isUsernameCorrect)
                        _isPasswordCorrect.postValue(success.isPasswordCorrect)
                        _eventFlow.emit(Event.LogInFailureEvent)
                    }
                }.onFailure { failure ->
                    _eventFlow.emit(Event.NetWorkErrorEvent(failure.message))
                }
        }
    }

    //保存账户用户名和密码
    fun saveLoginInfo(username: String, password: String) {
        wenku8Repository.username = username
        wenku8Repository.password = password
    }

    //获取网页wenku8节点
    fun getWenku8Node() = wenku8Repository.getWenku8Node()

    //设置网页wenku8节点
    fun setWenku8Node(node: String) = wenku8Repository.setWenku8Node(node)

    //刷新书架列表
    suspend fun refreshBookshelfList() {
        wenku8Repository.getBookshelf()
            .onSuccess { success ->
                bookshelfRepository.updateBookshelfList(success)
            }.onFailure { failure ->
                _eventFlow.emit(Event.NetWorkErrorEvent(failure.message))
            }
    }
}