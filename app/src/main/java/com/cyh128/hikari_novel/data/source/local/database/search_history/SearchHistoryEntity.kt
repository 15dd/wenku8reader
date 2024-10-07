package com.cyh128.hikari_novel.data.source.local.database.search_history

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

//名称和表的结构不要修改，除非做数据迁移Migration，否则数据会丢失
@Entity(tableName = "search_history")
data class SearchHistoryEntity(
    @PrimaryKey
    @ColumnInfo(name = "keyword")
    val keyword: String
)