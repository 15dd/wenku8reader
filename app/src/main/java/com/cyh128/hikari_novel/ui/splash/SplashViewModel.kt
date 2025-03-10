package com.cyh128.hikari_novel.ui.splash

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cyh128.hikari_novel.data.repository.AppRepository
import com.cyh128.hikari_novel.data.repository.Wenku8Repository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class SplashViewModel @Inject constructor(
    private val wenku8Repository: Wenku8Repository,
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