package com.cyh128.hikarinovel.ui.view.splash

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cyh128.hikarinovel.data.model.Event
import com.cyh128.hikarinovel.data.repository.AppRepository
import com.cyh128.hikarinovel.data.repository.BookshelfRepository
import com.cyh128.hikarinovel.data.repository.Wenku8Repository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
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

    private val _eventFlow = MutableSharedFlow<Event>()
    val eventFlow = _eventFlow.asSharedFlow()

    //判断是否处于未登录状态
    fun isNotLoggedIn() = wenku8Repository.username.isNullOrEmpty() || wenku8Repository.password.isNullOrEmpty()

    fun login() {
        viewModelScope.launch(Dispatchers.IO) {
            wenku8Repository.login()
                .onSuccess {
                    _eventFlow.emit(Event.LogInSuccessEvent)
                }.onFailure {
                    _eventFlow.emit(Event.LogInFailureEvent)
                }
        }
    }

    //刷新书架列表
    suspend fun refreshBookshelfList() {
        wenku8Repository.getBookshelf()
            .onSuccess { success ->
                bookshelfRepository.updateBookshelfList(success)
            }.onFailure { failure ->
                _eventFlow.emit(Event.NetWorkErrorEvent(failure.message))
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

    //获取网页wenku8节点
    fun getWenku8Node() = wenku8Repository.getWenku8Node()

    //设置网页wenku8节点
    fun setWenku8Node(node: String) = wenku8Repository.setWenku8Node(node)

    fun getLanguage() = appRepository.getLanguage()
}