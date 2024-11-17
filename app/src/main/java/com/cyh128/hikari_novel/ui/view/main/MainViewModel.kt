package com.cyh128.hikari_novel.ui.view.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cyh128.hikari_novel.data.model.Event
import com.cyh128.hikari_novel.data.repository.AppRepository
import com.drake.channel.sendEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val appRepository: AppRepository
) : ViewModel() {
    val isAutoUpdate get() = appRepository.getIsAutoUpdate()

    val defaultTab get() = appRepository.getDefaultTab()

    fun checkUpdate() {
        viewModelScope.launch(Dispatchers.IO) {
            appRepository.checkUpdate()
                .onSuccess {
                    if (it) sendEvent(Event.HaveAvailableUpdateEvent,"event_main_activity")
                    else sendEvent(Event.NoAvailableUpdateEvent,"event_main_activity")
                }.onFailure {
                    sendEvent(Event.NetworkErrorEvent(it.message),"event_main_activity")
                }
        }
    }
}