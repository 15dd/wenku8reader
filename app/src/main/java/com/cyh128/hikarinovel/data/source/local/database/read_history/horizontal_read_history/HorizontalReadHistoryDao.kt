package com.cyh128.hikarinovel.data.source.local.database.read_history.horizontal_read_history

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

@Dao
interface HorizontalReadHistoryDao {
    @Query("SELECT * FROM horizontal_read_history WHERE cid = (:cid)")
    fun getByCid(cid: String): Flow<HorizontalReadHistoryEntity?>

    @Query("SELECT * FROM horizontal_read_history WHERE aid = (:aid) AND volume = (:volume)")
    fun getByVolume(aid: String, volume: Int): Flow<List<HorizontalReadHistoryEntity>?>

    @Transaction
    suspend fun addOrReplace(
        aid: String,
        horizontalReadHistoryEntity: HorizontalReadHistoryEntity
    ) {
        cancelLatestChapter(aid)
        upsert(horizontalReadHistoryEntity)
    }

    @Upsert
    suspend fun upsert(horizontalReadHistoryEntity: HorizontalReadHistoryEntity)

    @Query("SELECT * FROM horizontal_read_history WHERE aid = (:aid) AND is_latest = 1")
    fun getLatestChapter(aid: String): Flow<HorizontalReadHistoryEntity?>

    @Query("UPDATE horizontal_read_history SET is_latest = 0 WHERE aid = (:aid) AND is_latest = 1")
    suspend fun cancelLatestChapter(aid: String)

    @Query("DELETE FROM horizontal_read_history WHERE cid = (:cid)")
    suspend fun delete(cid: String)

    @Query("DELETE FROM horizontal_read_history WHERE aid = (:aid)")
    suspend fun deleteAll(aid: String)
}