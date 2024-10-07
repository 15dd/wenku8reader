package com.cyh128.hikari_novel.data.source.local.database.read_history

abstract class BaseReadHistoryEntity(
    open val cid: String,
    open val aid: String,
    open val volume: Int,
    open val chapter: Int,
    open val location: Int,  //保存阅读位置
    open val progressPercent: Int,
    open var isLatest: Boolean //是否为上次阅读的章节
)