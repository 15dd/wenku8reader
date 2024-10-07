package com.cyh128.hikari_novel.data.repository

import com.cyh128.hikari_novel.data.source.local.database.read_history.horizontal_read_history.HorizontalReadHistoryDao
import com.cyh128.hikari_novel.data.source.local.database.read_history.horizontal_read_history.HorizontalReadHistoryEntity
import com.cyh128.hikari_novel.data.source.local.mmkv.HorizontalReadConfig
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class HorizontalReadRepository @Inject constructor(
    private val horizontalReadHistoryDao: HorizontalReadHistoryDao,
    private val horizontalReadConfig: HorizontalReadConfig
) {
    fun getByCid(cid: String) = horizontalReadHistoryDao.getByCid(cid)

    fun getByVolume(aid: String, volume: Int) = horizontalReadHistoryDao.getByVolume(aid, volume)

    suspend fun addOrReplace(
        aid: String,
        horizontalReadHistoryEntity: HorizontalReadHistoryEntity
    ) = horizontalReadHistoryDao.addOrReplace(aid, horizontalReadHistoryEntity)

    fun getLatestChapter(aid: String) = horizontalReadHistoryDao.getLatestChapter(aid)

    suspend fun delete(cid: String) = horizontalReadHistoryDao.delete(cid)

    suspend fun deleteAll(aid: String) = horizontalReadHistoryDao.deleteAll(aid)

    //获取是否显示当前章节的历史阅读记录
    fun getIsShowChapterReadHistory() = horizontalReadConfig.isShowChapterReadHistory

    fun setIsShowChapterReadHistory(value: Boolean) {
        horizontalReadConfig.isShowChapterReadHistory = value
    }

    fun getFontSize() = horizontalReadConfig.fontSize

    fun setFontSize(size: Float) {
        horizontalReadConfig.fontSize = size
    }

    fun getBottomFontSize() = horizontalReadConfig.bottomTextSize

    fun setBottomFontSize(size: Float) {
        horizontalReadConfig.bottomTextSize = size
    }

    fun getLineSpacing() = horizontalReadConfig.lineSpacing

    fun setLineSpacing(lineSpacing: Float) {
        horizontalReadConfig.lineSpacing = lineSpacing
    }

    fun getKeyDownSwitchChapter() = horizontalReadConfig.keyDownSwitchChapter

    fun setKeyDownSwitchChapter(value: Boolean) {
        horizontalReadConfig.keyDownSwitchChapter = value
    }

    fun getKeepScreenOn() = horizontalReadConfig.keepScreenOn

    fun setKeepScreenOn(value: Boolean) {
        horizontalReadConfig.keepScreenOn = value
    }

    fun getSwitchAnimation() = horizontalReadConfig.switchAnimation

    fun setSwitchAnimation(value: Boolean) {
        horizontalReadConfig.switchAnimation = value
    }
}