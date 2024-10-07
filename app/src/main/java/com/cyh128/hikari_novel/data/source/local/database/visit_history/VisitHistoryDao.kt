package com.cyh128.hikari_novel.data.source.local.database.visit_history

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface VisitHistoryDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(vararg visitHistoryEntity: VisitHistoryEntity)

    @Query("DELETE FROM visit_history WHERE aid = (:aid)")
    suspend fun delete(aid: String)

    @Query("DELETE FROM visit_history")
    suspend fun deleteAll()

    @Query("SELECT * FROM visit_history")
    fun getAll(): Flow<List<VisitHistoryEntity>?>
}