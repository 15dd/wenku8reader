package com.cyh128.hikarinovel.util

import android.text.format.DateUtils
import com.yariksoffice.lingver.Lingver
import java.text.SimpleDateFormat
import java.util.Date

object TimeUtil {
    //20xx-01-01 00:00:00转换成xx天前
    fun dateToText1(dateStr: String): String {
        val pattern = "yyyy-MM-dd HH:mm:ss"
        val simpleDateFormat = SimpleDateFormat(pattern, Lingver.getInstance().getLocale())
        val date = simpleDateFormat.parse(dateStr)!!
        return DateUtils.getRelativeTimeSpanString(date.time).toString()
    }

    //20xx-01-01转换成xx天前
    fun dateToText2(dateStr: String): String {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Lingver.getInstance().getLocale())
        val date: Date = dateFormat.parse(dateStr)!!
        val timeInMillis: Long = date.time
        return DateUtils.getRelativeTimeSpanString(
            timeInMillis,
            System.currentTimeMillis(),
            DateUtils.DAY_IN_MILLIS
        ).toString()
    }

    fun getTimeToken(): String = SimpleDateFormat(
        "yyyy/MM/dd HH:mm:ss",
        Lingver.getInstance().getLocale()
    ).format(System.currentTimeMillis())
}