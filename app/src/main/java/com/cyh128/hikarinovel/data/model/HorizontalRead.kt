package com.cyh128.hikarinovel.data.model

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
