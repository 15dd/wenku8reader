package com.cyh128.hikarinovel.data.source.local.database.visit_history

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "visit_history")
data class VisitHistoryEntity(
    @PrimaryKey
    @ColumnInfo(name = "aid")
    val aid: String,

    @ColumnInfo(name = "title")
    val title: String,

    @ColumnInfo(name = "img")
    val img: String,

    @ColumnInfo(name = "time")
    val time: String
)
