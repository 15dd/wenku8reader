package com.cyh128.hikari_novel.data.source.local.database.read_history.horizontal_read_history

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.cyh128.hikari_novel.data.source.local.database.read_history.BaseReadHistoryEntity

//名称和表的结构不要修改，除非做数据迁移Migration，否则数据会丢失
@Entity(tableName = "horizontal_read_history")
data class HorizontalReadHistoryEntity (
    @PrimaryKey
    @ColumnInfo(name = "cid")
    override val cid: String,

    @ColumnInfo(name = "aid")
    override val aid: String,

    @ColumnInfo(name = "volume")
    override val volume: Int,

    @ColumnInfo(name = "chapter")
    override val chapter: Int,

    @ColumnInfo(name = "location")
    override val location: Int, //保存阅读位置

    @ColumnInfo(name = "progress_percent")
    override val progressPercent: Int,

    @ColumnInfo(name = "is_latest")
    override var isLatest: Boolean //是否为上次阅读的章节
): BaseReadHistoryEntity(cid, aid, volume, chapter, location, progressPercent, isLatest)