package com.cyh128.hikari_novel.ui.main.home.recommend

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cyh128.hikari_novel.data.model.Event
import com.cyh128.hikari_novel.data.model.HomeBlock
import com.cyh128.hikari_novel.data.repository.AppRepository
import com.cyh128.hikari_novel.data.repository.Wenku8Repository
import com.drake.channel.sendEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RecommendViewModel @Inject constructor(
    private val wenku8Repository: Wenku8Repository,
    private val appRepository: AppRepository
) : ViewModel() {
    var homeBlockList: List<HomeBlock>? = null

    val listViewType get() = appRepository.getListViewType()

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