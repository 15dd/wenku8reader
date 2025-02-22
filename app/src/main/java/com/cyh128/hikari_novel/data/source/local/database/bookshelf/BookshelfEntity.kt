package com.cyh128.hikari_novel.data.source.local.database.bookshelf

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "bookshelf")
data class BookshelfEntity(
    @PrimaryKey
    @ColumnInfo(name = "aid")
    val aid: String,

    @ColumnInfo(name = "bid")
    val bid: String,

    @ColumnInfo(name = "detail_url")
    val detailUrl: String,

    @ColumnInfo(name = "title")
    val title: String,

    @ColumnInfo(name = "img")
    val img: String,

    @ColumnInfo(name = "classid")
    val classId: Int
)