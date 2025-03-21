package com.cyh128.hikari_novel.ui.main.home.ranking

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cyh128.hikari_novel.HikariApp
import com.cyh128.hikari_novel.R
import com.cyh128.hikari_novel.data.model.Event
import com.cyh128.hikari_novel.data.model.LoadMode
import com.cyh128.hikari_novel.data.model.NovelCover
import com.cyh128.hikari_novel.data.repository.AppRepository
import com.cyh128.hikari_novel.data.repository.Wenku8Repository
import com.drake.channel.sendEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RankingViewModel @Inject constructor(
    private val wenku8Repository: Wenku8Repository,
    private val appRepository: AppRepository
) : ViewModel() {
    private var currentIndex: Int = 0
    private var maxNum: Int? = null //总页数

    val listViewType get() = appRepository.getListViewType()

    val pager: MutableList<NovelCover> = mutableListOf()

    fun getData(mode: LoadMode, ranking: String) {
        viewModelScope.launch(Dispatchers.IO) {
            if (mode == LoadMode.REFRESH) {
                currentIndex = 0
                pager.clear()
            }
            ++currentIndex
            wenku8Repository.getNovelByRanking(getRanking(ranking), currentIndex)
                .onSuccess { success ->
                    pager.addAll(success.curPage)
                    maxNum = success.maxNum
                    sendEvent(Event.LoadSuccessEvent, "event_ranking_view_model")
                }.onFailure { failure ->
                    --currentIndex
                    sendEvent(Event.NetworkErrorEvent(failure.message), "event_ranking_view_model")
                }
        }
    }

    fun haveMore() = maxNum == null || currentIndex < maxNum!!

    private fun getRanking(ranking: String): String = when (ranking) {
        HikariApp.application.getString(R.string.last_update) -> "lastupdate"
        HikariApp.application.getString(R.string.post_date) -> "postdate"
        HikariApp.application.getString(R.string.all_visit) -> "allvisit"
        HikariApp.application.getString(R.string.all_vote) -> "allvote"
        HikariApp.application.getString(R.string.good_num) -> "goodnum"
        HikariApp.application.getString(R.string.day_visit) -> "dayvisit"
        HikariApp.application.getString(R.string.day_vote) -> "dayvote"
        HikariApp.application.getString(R.string.month_visit) -> "monthvisit"
        HikariApp.application.getString(R.string.month_vote) -> "monthvote"
        HikariApp.application.getString(R.string.week_visit) -> "weekvisit"
        HikariApp.application.getString(R.string.week_vote) -> "weekvote"
        HikariApp.application.getString(R.string.size) -> "size"
        HikariApp.application.getString(R.string.animated) -> "anime"

        else -> throw IllegalArgumentException("Unknown Argument")
    }

    fun getWenku8Node() = wenku8Repository.getWenku8Node()
}