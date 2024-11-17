package com.cyh128.hikari_novel.ui.view.main.visit_history

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cyh128.hikari_novel.data.repository.VisitHistoryRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class VisitHistoryViewModel @Inject constructor(
    private val visitHistoryRepository: VisitHistoryRepository
) : ViewModel() {
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