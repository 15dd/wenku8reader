package com.cyh128.hikarinovel.ui.view.main.home.visit_history

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cyh128.hikarinovel.data.model.Event
import com.cyh128.hikarinovel.data.repository.VisitHistoryRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class VisitHistoryViewModel @Inject constructor(
    private val visitHistoryRepository: VisitHistoryRepository
) : ViewModel() {
    private val _eventFlow = MutableSharedFlow<Event>()
    val eventFlow = _eventFlow.asSharedFlow()

    val visitHistoryFlow get() = visitHistoryRepository.getAll().shareIn(
        scope = CoroutineScope(Dispatchers.IO),
        started = SharingStarted.Lazily,
        replay = 0
    )

    fun deleteAllHistory() {
        viewModelScope.launch(Dispatchers.IO) {
            visitHistoryRepository.deleteAll()
        }
    }

    fun delete(aid: String) {
        viewModelScope.launch(Dispatchers.IO) {
            visitHistoryRepository.delete(aid)
        }
    }
}