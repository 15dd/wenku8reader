package com.cyh128.hikari_novel.data.repository

import com.cyh128.hikari_novel.data.source.local.database.bookshelf.BookshelfDao
import com.cyh128.hikari_novel.data.source.local.database.bookshelf.BookshelfEntity
import com.cyh128.hikari_novel.data.source.local.mmkv.BookshelfInfo
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BookshelfRepository @Inject constructor(
    private val bookshelfInfo: BookshelfInfo,
    private val bookshelfDao: BookshelfDao
) {
    suspend fun upsert(bookshelfEntity: BookshelfEntity) = bookshelfDao.upsert(bookshelfEntity)

    suspend fun upsertAll(list: List<BookshelfEntity>) = bookshelfDao.upsertAll(list)

    suspend fun updateClassId(aid: String, newClassId: Int) = bookshelfDao.updateClassId(aid, newClassId)

    suspend fun delete(aid: String) = bookshelfDao.delete(aid)

    suspend fun deleteAll() = bookshelfDao.deleteAll()

    fun getAll() = bookshelfDao.getAll()

    fun getByClassId(classId: Int) = bookshelfDao.getByClassId(classId)

    fun getByAid(aid: String) = bookshelfDao.getByAid(aid)

    fun setMaxCollection(value: Int) {
        bookshelfInfo.maxCollection = value
    }

    fun getMaxCollection() = bookshelfInfo.maxCollection
}

