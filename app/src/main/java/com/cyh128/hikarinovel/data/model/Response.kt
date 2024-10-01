package com.cyh128.hikarinovel.data.model

data class NovelCoverResponse(
    val maxNum: Int,
    val curPage: List<NovelCover>
)

data class ChapterContentResponse(
    val content: String,
    val image: List<String>
)

data class LoginResponse(
    val isUsernameCorrect: Boolean,
    val isPasswordCorrect: Boolean
) {
    fun isCorrect() = isUsernameCorrect && isPasswordCorrect
}

data class CommentResponse(
    var maxNum: Int,
    val list: MutableList<Comment>
) {
    companion object {
        fun empty() = CommentResponse(
            -1,
            mutableListOf()
        )
    }
}

data class ReplyResponse(
    var maxNum: Int,
    val curPage: MutableList<Reply>
) {
    companion object {
        fun empty() = ReplyResponse(
            -1,
            mutableListOf()
        )
    }
}