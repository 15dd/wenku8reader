package com.cyh128.hikarinovel.data.source.local.database.read_history.horizontal_read_history

import androidx.room.Database
import androidx.room.RoomDatabase

//version不要修改，除非做数据迁移Migration，否则数据会丢失
@Database(
    entities = [HorizontalReadHistoryEntity::class],
    version = 1
)
abstract class HorizontalReadHistoryDatabase: RoomDatabase() {
    abstract fun horizontalReadHistoryDao(): HorizontalReadHistoryDao
}