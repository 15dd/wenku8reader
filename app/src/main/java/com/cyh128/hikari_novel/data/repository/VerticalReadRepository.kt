package com.cyh128.hikari_novel.data.repository

import com.cyh128.hikari_novel.data.source.local.database.read_history.vertical_read_history.VerticalReadHistoryDao
import com.cyh128.hikari_novel.data.source.local.database.read_history.vertical_read_history.VerticalReadHistoryEntity
import com.cyh128.hikari_novel.data.source.local.mmkv.VerticalReadConfig
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class VerticalReadRepository @Inject constructor(
    private val verticalReadHistoryDao: VerticalReadHistoryDao,
    private val verticalReadConfig: VerticalReadConfig
) {
    fun getByCid(cid: String) = verticalReadHistoryDao.getByCid(cid)

    fun getByVolume(aid: String, volume: Int) = verticalReadHistoryDao.getByVolume(aid, volume)

    suspend fun addOrReplace(
        aid: String,
        verticalReadHistoryEntity: VerticalReadHistoryEntity
    ) = verticalReadHistoryDao.addOrReplace(aid, verticalReadHistoryEntity)

    fun getLatestChapter(aid: String) = verticalReadHistoryDao.getLatestChapter(aid)

    suspend fun delete(cid: String) = verticalReadHistoryDao.delete(cid)

    suspend fun deleteAll(aid: String) = verticalReadHistoryDao.deleteAll(aid)

    fun getIsShowChapterReadHistory() = verticalReadConfig.isShowChapterReadHistory

    fun setIsShowChapterReadHistory(value: Boolean) {
        verticalReadConfig.isShowChapterReadHistory = value
    }

    fun getFontSize() = verticalReadConfig.fontSize

    fun setFontSize(size: Float) {
        verticalReadConfig.fontSize = size
    }

    fun getLineSpacing() = verticalReadConfig.lineSpacing

    fun setLineSpacing(lineSpacing: Float) {
        verticalReadConfig.lineSpacing = lineSpacing
    }

    fun getKeepScreenOn() = verticalReadConfig.keepScreenOn

    fun setKeepScreenOn(value: Boolean) {
        verticalReadConfig.keepScreenOn = value
    }
}