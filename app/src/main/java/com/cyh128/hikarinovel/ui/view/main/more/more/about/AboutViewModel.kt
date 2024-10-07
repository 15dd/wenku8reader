package com.cyh128.hikarinovel.ui.view.main.more.more.about

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cyh128.hikarinovel.data.model.Event
import com.cyh128.hikarinovel.data.repository.AppRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AboutViewModel @Inject constructor(
    private val appRepository: AppRepository
): ViewModel(){
    private val _eventFlow = MutableSharedFlow<Event>()
    val eventFlow = _eventFlow.asSharedFlow()

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
}