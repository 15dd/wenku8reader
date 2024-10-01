package com.cyh128.hikarinovel.util

import com.cyh128.hikarinovel.HikariApp
import com.cyh128.hikarinovel.R

object HttpCodeParser {
    fun parser(code: Int): String = when {
        code == 403 -> HikariApp.application.getString(R.string.http_code_403)
        code/100 == 5 -> HikariApp.application.getString(R.string.http_code_5xx)
        else -> "http status code:"
    }
}