package com.cyh128.hikari_novel.ui.view.main.home.recommend

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cyh128.hikari_novel.data.model.Event
import com.cyh128.hikari_novel.data.model.HomeBlock
import com.cyh128.hikari_novel.data.repository.Wenku8Repository
import com.drake.channel.sendEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RecommendViewModel @Inject constructor(
    private val wenku8Repository: Wenku8Repository
) : ViewModel() {
    var homeBlockList: List<HomeBlock>? = null

    fun getData() {
        viewModelScope.launch(Dispatchers.IO) {
            wenku8Repository.getRecommend()
                .onSuccess { success ->
                    homeBlockList = success
                    sendEvent(Event.LoadSuccessEvent, "event_recommend_view_model")
                }.onFailure { failure ->
                    sendEvent(Event.NetworkErrorEvent(failure.message), "event_recommend_view_model")
                }
        }
    }
}