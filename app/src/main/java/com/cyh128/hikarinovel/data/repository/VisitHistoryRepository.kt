package com.cyh128.hikarinovel.data.repository

import com.cyh128.hikarinovel.data.source.local.database.visit_history.VisitHistoryDao
import com.cyh128.hikarinovel.data.source.local.database.visit_history.VisitHistoryEntity
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class VisitHistoryRepository @Inject constructor(
   private val visitHistoryDao: VisitHistoryDao
) {
    suspend fun add(visitHistoryEntity: VisitHistoryEntity) = visitHistoryDao.upsert(visitHistoryEntity)

    suspend fun delete(aid: String) = visitHistoryDao.delete(aid)

    suspend fun deleteAll() = visitHistoryDao.deleteAll()

    fun getAll() = visitHistoryDao.getAll()
}