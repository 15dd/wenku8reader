package com.cyh128.hikari_novel.data.source.local.database.bookshelf

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [BookshelfEntity::class], version = 1)
abstract class BookshelfDatabase: RoomDatabase() {
    abstract fun bookshelfDao(): BookshelfDao
}