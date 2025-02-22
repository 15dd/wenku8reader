package com.cyh128.hikari_novel.data.model

data class Bookshelf(
    val list: List<BookshelfNovelInfo>,
    val maxNum: Int
)

data class BookshelfNovelInfo(
    val bid: String,
    val aid: String,
    val detailUrl: String,
    val title: String,
    val img: String,
    var isSelected: Boolean = false //多选模式用
)