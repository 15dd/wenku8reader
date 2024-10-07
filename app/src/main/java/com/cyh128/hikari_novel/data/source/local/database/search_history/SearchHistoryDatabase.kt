package com.cyh128.hikari_novel.data.source.local.database.search_history

import androidx.room.Database
import androidx.room.RoomDatabase

//version不要修改，除非做数据迁移Migration，否则数据会丢失
@Database(entities = [SearchHistoryEntity::class], version = 1)
abstract class SearchHistoryDatabase: RoomDatabase() {
    abstract fun searchHistoryDao(): SearchHistoryDao
}