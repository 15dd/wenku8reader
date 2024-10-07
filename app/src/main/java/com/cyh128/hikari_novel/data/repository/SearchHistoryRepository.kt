package com.cyh128.hikari_novel.data.repository

import com.cyh128.hikari_novel.data.source.local.database.search_history.SearchHistoryDao
import com.cyh128.hikari_novel.data.source.local.database.search_history.SearchHistoryEntity
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SearchHistoryRepository @Inject constructor(
    private val searchHistoryDao: SearchHistoryDao
) {
    suspend fun getAll() = searchHistoryDao.getAll()

    suspend fun addOrReplace(searchHistoryEntity: SearchHistoryEntity) = searchHistoryDao.upsert(searchHistoryEntity)

    suspend fun deleteAll() = searchHistoryDao.deleteAll()
}