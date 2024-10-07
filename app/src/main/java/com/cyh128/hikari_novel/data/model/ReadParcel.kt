package com.cyh128.hikari_novel.data.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class ReadParcel(
    val novel: Novel,
    val curVolumePos: Int,
    val curChapterPos: Int,
    val goToLatest: Boolean
): Parcelable