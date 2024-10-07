package com.cyh128.hikari_novel.data.model

data class HorizontalRead(
    val title: String,
    val content: String,
    val imageList: List<String>
) {
    companion object {
        fun empty() = HorizontalRead(
            "",
            "",
            mutableListOf()
        )
    }
}
