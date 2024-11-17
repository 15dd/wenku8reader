package com.cyh128.hikari_novel.ui.view.main.more.more.about

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
class AboutViewModel @Inject constructor(
    private val appRepository: AppRepository
): ViewModel(){
    fun checkUpdate() {
        viewModelScope.launch(Dispatchers.IO) {
            appRepository.checkUpdate()
                .onSuccess {
                    if (it) sendEvent(Event.HaveAvailableUpdateEvent,"event_about_activity")
                    else sendEvent(Event.NoAvailableUpdateEvent, "event_about_activity")
                }.onFailure {
                    sendEvent(Event.NetworkErrorEvent(it.message),"event_about_activity")
                }
        }
    }
}