package com.cyh128.hikari_novel.data.source.local.database.search_history

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert

@Dao
interface SearchHistoryDao {
    @Query("SELECT keyword FROM search_history")
    suspend fun getAll(): List<String>?

    @Upsert
    suspend fun upsert(searchHistoryEntity: SearchHistoryEntity)

    @Query("DELETE FROM search_history")
    suspend fun deleteAll()
}