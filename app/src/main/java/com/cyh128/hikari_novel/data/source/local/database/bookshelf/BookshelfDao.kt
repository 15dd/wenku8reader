package com.cyh128.hikari_novel.data.source.local.database.bookshelf

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface BookshelfDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(bookshelfEntity: BookshelfEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertAll(list: List<BookshelfEntity>)

    @Query("DELETE FROM bookshelf WHERE aid = (:aid)")
    suspend fun delete(aid: String)

    @Query("DELETE FROM bookshelf")
    suspend fun deleteAll()

    @Query("SELECT * FROM bookshelf WHERE classid = (:classId)")
    fun getByClassId(classId: Int): Flow<List<BookshelfEntity>?>

    @Query("SELECT * FROM bookshelf")
    fun getAll(): Flow<List<BookshelfEntity>?>

    @Query("SELECT * FROM bookshelf WHERE aid = (:aid)")
    fun getByAid(aid: String): BookshelfEntity?
}