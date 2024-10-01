package com.cyh128.hikarinovel.data.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Chapter(
    val chapterTitle: String,
    val url: String,
    val cid: String
): Parcelable

@Parcelize
data class Volume(
    var volumeTitle: String,
    var chapters: List<Chapter>
): Parcelable {
    companion object {
        fun empty() = Volume(
            "",
            mutableListOf()
        )
    }
}

@Parcelize
data class Novel(
    val aid: String,
    val volume: List<Volume>
): Parcelable