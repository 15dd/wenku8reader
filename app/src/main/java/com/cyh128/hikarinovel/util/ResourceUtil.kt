package com.cyh128.hikarinovel.util

import androidx.annotation.StringRes
import com.cyh128.hikarinovel.HikariApp

object ResourceUtil {
    fun getString(@StringRes res: Int): String {
        return HikariApp.application.getString(res)
    }
}