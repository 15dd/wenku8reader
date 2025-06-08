package com.cyh128.hikari_novel.ui.read.vertical

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cyh128.hikari_novel.data.model.EmptyException
import com.cyh128.hikari_novel.data.model.Event
import com.cyh128.hikari_novel.data.model.Novel
import com.cyh128.hikari_novel.data.repository.ReadColorRepository
import com.cyh128.hikari_novel.data.repository.VerticalReadRepository
import com.cyh128.hikari_novel.data.repository.Wenku8Repository
import com.cyh128.hikari_novel.data.source.local.database.read_history.vertical_read_history.VerticalReadHistoryEntity
import com.drake.channel.sendEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.properties.Delegates

@HiltViewModel
class ReadViewModel @Inject constructor(
    private val wenku8Repository: Wenku8Repository,
    private val verticalReadRepository: VerticalReadRepository,
    private val readColorRepository: ReadColorRepository
) : ViewModel() {
    var curChapterPos by Delegates.notNull<Int>()
    var curVolumePos by Delegates.notNull<Int>()
    var goToLatest = false //是否是上次阅读的章节

    lateinit var curNovelContent: String //当前章节的小说内容
    lateinit var curImages: List<String> //当前小说的插图的链接列表
    lateinit var novel: Novel

    private val aid get() = novel.aid
    private val cid get() = novel.volume[curVolumePos].chapters[curChapterPos].cid
    val chapterTitle get() = novel.volume[curVolumePos].chapters[curChapterPos].chapterTitle
    val curVolume get() = novel.volume[curVolumePos]

    var isBarShown = false //上下栏是否显示

    var progressText = MutableLiveData<String>()

    var curReadPos = 0 //阅读位置

    val getByCid get() = verticalReadRepository.getByCid(cid)

    fun getNovelContent() {
        curNovelContent = ""
        viewModelScope.launch(Dispatchers.IO) {
            wenku8Repository.getNovelContent(aid, cid)
                .onSuccess { success ->
                    curNovelContent = success.content
                    curImages = success.image
                    sendEvent(Event.LoadSuccessEvent, "event_vertical_read_activity")
                }.onFailure { failure ->
                    if (failure is EmptyException) sendEvent(Event.EmptyContentEvent, "event_vertical_read_activity")
                    else sendEvent(Event.NetworkErrorEvent(failure.message), "event_vertical_read_activity")
                }
        }
    }

    //保存阅读记录
    suspend fun saveReadHistory() {
        if (curNovelContent.isNotBlank()) {
            verticalReadRepository.addOrReplace(
                aid,
                VerticalReadHistoryEntity(
                    cid,
                    aid,
                    curVolumePos,
                    curChapterPos,
                    curReadPos,
                    progressText.value?.substringBefore("%")?.toInt() ?: 0,
                    true
                )
            )
        }
    }

    fun setFontSize(size: Float) {
        viewModelScope.launch {
            verticalReadRepository.setFontSize(size)
            sendEvent(Event.ChangeFontSizeEvent(size), "event_vertical_read_fragment")
        }
    }

    fun getFontSize() = verticalReadRepository.getFontSize()

    fun setLineSpacing(lineSpacing: Float) {
        viewModelScope.launch {
            verticalReadRepository.setLineSpacing(lineSpacing)
            sendEvent(Event.ChangeLineSpacingEvent(lineSpacing), "event_vertical_read_fragment")
        }
    }

    fun getLineSpacing() = verticalReadRepository.getLineSpacing()

    fun getKeepScreenOn() = verticalReadRepository.getKeepScreenOn()

    fun setKeepScreenOn(value: Boolean) {
        verticalReadRepository.setKeepScreenOn(value)
    }

    fun getTextColorDay() = readColorRepository.getTextColorDay()

    fun getTextColorNight() = readColorRepository.getTextColorNight()

    fun getBgColorDay() = readColorRepository.getBgColorDay()

    fun getBgColorNight() = readColorRepository.getBgColorNight()

    fun getIsShowChapterReadHistory() = verticalReadRepository.getIsShowChapterReadHistory()

    fun setIsShowChapterReadHistory(value: Boolean) {
        verticalReadRepository.setIsShowChapterReadHistory(value)
    }

    fun getIsShowChapterReadHistoryWithoutConfirm() =
        verticalReadRepository.getIsShowChapterReadHistoryWithoutConfirm()

    fun setIsShowChapterReadHistoryWithoutConfirm(value: Boolean) {
        verticalReadRepository.setIsShowChapterReadHistoryWithoutConfirm(value)
    }
}