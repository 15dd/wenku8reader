package com.cyh128.hikarinovel.ui.view.main.main.home.recommend

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cyh128.hikarinovel.data.model.Event
import com.cyh128.hikarinovel.data.model.HomeBlock
import com.cyh128.hikarinovel.data.repository.Wenku8Repository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RecommendViewModel @Inject constructor(
    private val wenku8Repository: Wenku8Repository
) : ViewModel() {
    var homeBlockList: List<HomeBlock>? = null

    private val _eventFlow = MutableSharedFlow<Event>()
    val eventFlow = _eventFlow.asSharedFlow()

    fun getData() {
        viewModelScope.launch(Dispatchers.IO) {
            wenku8Repository.getRecommend()
                .onSuccess { success ->
                    homeBlockList = success
                    _eventFlow.emit(Event.LoadSuccessEvent)
                }.onFailure { failure ->
                    _eventFlow.emit(Event.NetWorkErrorEvent(failure.message))
                }
        }
    }
}