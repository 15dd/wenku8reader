package com.cyh128.hikari_novel.data.model

data class NovelCover(
    val title: String,
    val img: String,
    val detailUrl: String,
    val aid: String
)

data class SimpleNovelCover(
    val title: String,
    val aid: String
)