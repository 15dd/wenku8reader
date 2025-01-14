package com.cyh128.hikari_novel.data.model

data class NovelCoverResponse(
    val maxNum: Int,
    val curPage: List<NovelCover>
)

data class ChapterContentResponse(
    val content: String,
    val image: List<String>
)

data class LoginResponse(
    var isUsernameCorrect: Boolean,
    var isPasswordCorrect: Boolean,
    var isCodeCorrect: Boolean,
    var isLoginSuccessful: Boolean
) {
    fun isCorrect() = isUsernameCorrect && isPasswordCorrect && isCodeCorrect

    companion object {
        fun empty() = LoginResponse(
            isUsernameCorrect = false,
            isPasswordCorrect = false,
            isCodeCorrect = false,
            isLoginSuccessful = false
        )
    }
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