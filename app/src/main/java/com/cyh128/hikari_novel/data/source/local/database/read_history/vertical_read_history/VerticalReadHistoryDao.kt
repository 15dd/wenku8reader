package com.cyh128.hikari_novel.data.source.local.database.read_history.vertical_read_history

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

@Dao
interface VerticalReadHistoryDao {
    @Query("SELECT * FROM vertical_read_history WHERE cid = (:cid)")
    fun getByCid(cid: String): Flow<VerticalReadHistoryEntity?>

    @Query("SELECT * FROM vertical_read_history WHERE aid = (:aid) AND volume = (:volume)")
    fun getByVolume(aid: String, volume: Int): Flow<List<VerticalReadHistoryEntity>?>

    @Transaction
    suspend fun addOrReplace(
        aid: String,
        verticalReadHistoryEntity: VerticalReadHistoryEntity
    ) {
        cancelLatestChapter(aid)
        upsert(verticalReadHistoryEntity)
    }

    @Upsert
    suspend fun upsert(verticalReadHistoryEntity: VerticalReadHistoryEntity)

    @Query("SELECT * FROM vertical_read_history WHERE aid = (:aid) AND is_latest = 1")
    fun getLatestChapter(aid: String): Flow<VerticalReadHistoryEntity?>

    @Query("UPDATE vertical_read_history SET is_latest = 0 WHERE aid = (:aid) AND is_latest = 1")
    suspend fun cancelLatestChapter(aid: String)

    @Query("DELETE FROM vertical_read_history WHERE cid = (:cid)")
    suspend fun delete(cid: String)

    @Query("DELETE FROM vertical_read_history WHERE aid = (:aid)")
    suspend fun deleteAll(aid: String)
}