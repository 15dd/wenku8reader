package com.cyh128.hikari_novel.util

import androidx.annotation.StringRes
import com.cyh128.hikari_novel.HikariApp

object ResourceUtil {
    fun getString(@StringRes res: Int): String {
        return HikariApp.application.getString(res)
    }
}