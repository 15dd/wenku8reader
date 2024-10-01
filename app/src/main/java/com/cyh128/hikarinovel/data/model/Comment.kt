package com.cyh128.hikarinovel.data.model

data class Comment(
    val replyUrl: String,
    val content: String,
    val viewCount: String,
    val replyCount: String,
    val userName: String,
    val time: String
)

data class Reply(
    val content: String,
    val userName: String,
    val time: String
)