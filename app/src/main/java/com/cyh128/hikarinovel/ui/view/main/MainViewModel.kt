package com.cyh128.hikarinovel.ui.view.main

import android.content.pm.PackageManager
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cyh128.hikarinovel.HikariApp
import com.cyh128.hikarinovel.data.model.Event
import com.cyh128.hikarinovel.data.repository.AppRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val appRepository: AppRepository
) : ViewModel() {
    private val _eventFlow = MutableSharedFlow<Event>()
    val eventFlow = _eventFlow.asSharedFlow()

    val isAutoUpdate get() = appRepository.getIsAutoUpdate()

    val defaultTab get() = appRepository.getDefaultTab()

    fun checkUpdate() {
        viewModelScope.launch(Dispatchers.IO) {
            appRepository.checkUpdate()
                .onSuccess {
                    if (it) _eventFlow.emit(Event.HaveAvailableUpdateEvent)
                    else _eventFlow.emit(Event.NoAvailableUpdateEvent)
                }.onFailure {
                    _eventFlow.emit(Event.NetWorkErrorEvent(it.message))
                }
        }
    }

    fun getVersion(): String? {
        val manager = HikariApp.application.packageManager
        var name: String? = null
        try {
            val info = manager.getPackageInfo(HikariApp.application.packageName, 0)
            name = info.versionName
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
        }
        return name
    }
}