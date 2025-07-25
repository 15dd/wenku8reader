package com.cyh128.hikari_novel.ui.read.horizontal

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cyh128.hikari_novel.data.model.EmptyException
import com.cyh128.hikari_novel.data.model.Event
import com.cyh128.hikari_novel.data.model.Novel
import com.cyh128.hikari_novel.data.repository.AppRepository
import com.cyh128.hikari_novel.data.repository.HorizontalReadRepository
import com.cyh128.hikari_novel.data.repository.ReadColorRepository
import com.cyh128.hikari_novel.data.repository.Wenku8Repository
import com.cyh128.hikari_novel.data.source.local.database.read_history.horizontal_read_history.HorizontalReadHistoryEntity
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
    private val horizontalReadRepository: HorizontalReadRepository,
    private val readColorRepository: ReadColorRepository,
    private val appRepository: AppRepository
) : ViewModel() {
    var curChapterPos by Delegates.notNull<Int>()
    var curVolumePos by Delegates.notNull<Int>()

    private val aid get() = novel.aid
    private val cid get() = novel.volume[curVolumePos].chapters[curChapterPos].cid
    val chapterTitle get() = novel.volume[curVolumePos].chapters[curChapterPos].chapterTitle
    val curVolume get() = novel.volume[curVolumePos]

    lateinit var novel: Novel
    lateinit var curNovelContent: String //当前章节的小说内容
    lateinit var curImages: List<String> //当前小说的插图的链接列表

    var isBarShown = true //上下栏是否显示

    var goToLatest = false

    val getByCid get() = horizontalReadRepository.getByCid(cid)

    val isHorizontalReadFirstLaunch get() = appRepository.getIsHorizontalFirstLaunch()

    fun setIsHorizontalReadFirstLaunch(isHorizontalReadFirstLaunch: Boolean) {
        appRepository.setIsHorizontalFirstLaunch(isHorizontalReadFirstLaunch)
    }

    fun getNovelContent() {
        curNovelContent = ""
        viewModelScope.launch(Dispatchers.IO) {
            wenku8Repository.getNovelContent(aid, cid)
                .onSuccess { success ->
                    curNovelContent = success.content
                    curImages = success.image
                    sendEvent(Event.LoadSuccessEvent, "event_horizontal_read_activity")
                }.onFailure { failure ->
                    if (failure is EmptyException) sendEvent(Event.EmptyContentEvent, "event_horizontal_read_activity")
                    else sendEvent(Event.NetworkErrorEvent(failure.message), "event_horizontal_read_activity")
                }
        }
    }

    //保存阅读记录
    suspend fun saveReadHistory(readPos: Int, maxNum: Int) {
        if (curNovelContent.isNotBlank()) {
            horizontalReadRepository.addOrReplace(
                aid,
                HorizontalReadHistoryEntity(
                    cid,
                    aid,
                    curVolumePos,
                    curChapterPos,
                    readPos,
                    (readPos.toFloat() / maxNum.toFloat() * 100).toInt(),
                    true
                )
            )
        }
    }

    fun setFontSize(size: Float) {
        horizontalReadRepository.setFontSize(size)
    }

    fun getFontSize() = horizontalReadRepository.getFontSize()

    fun getBottomFontSize() = horizontalReadRepository.getBottomFontSize()

    fun setBottomFontSize(size: Float) {
        horizontalReadRepository.setBottomFontSize(size)
    }

    fun setLineSpacing(lineSpacing: Float) {
        horizontalReadRepository.setLineSpacing(lineSpacing)
    }

    fun getLineSpacing() = horizontalReadRepository.getLineSpacing()

    fun getTextColorDay() = readColorRepository.getTextColorDay()

    fun getTextColorNight() = readColorRepository.getTextColorNight()

    fun getBgColorDay() = readColorRepository.getBgColorDay()

    fun getBgColorNight() = readColorRepository.getBgColorNight()

    fun getKeyDownSwitchChapter() = horizontalReadRepository.getKeyDownSwitchChapter()

    fun setKeyDownSwitchChapter(value: Boolean) {
        horizontalReadRepository.setKeyDownSwitchChapter(value)
    }

    fun getKeepScreenOn() = horizontalReadRepository.getKeepScreenOn()

    fun setKeepScreenOn(value: Boolean) {
        horizontalReadRepository.setKeepScreenOn(value)
    }

    fun getSwitchAnimation() = horizontalReadRepository.getSwitchAnimation()

    fun setSwitchAnimation(value: Boolean) {
        horizontalReadRepository.setSwitchAnimation(value)
    }

    fun getIsShowChapterReadHistory() = horizontalReadRepository.getIsShowChapterReadHistory()

    fun setIsShowChapterReadHistory(value: Boolean) {
        horizontalReadRepository.setIsShowChapterReadHistory(value)
    }

    fun getIsShowChapterReadHistoryWithoutConfirm() =
        horizontalReadRepository.getIsShowChapterReadHistoryWithoutConfirm()

    fun setIsShowChapterReadHistoryWithoutConfirm(value: Boolean) {
        horizontalReadRepository.setIsShowChapterReadHistoryWithoutConfirm(value)
    }
}