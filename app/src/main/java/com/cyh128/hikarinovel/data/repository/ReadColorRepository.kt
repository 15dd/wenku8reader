package com.cyh128.hikarinovel.data.repository

import com.cyh128.hikarinovel.data.source.local.mmkv.ReadColorConfig
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ReadColorRepository @Inject constructor(
    private val readColorConfig: ReadColorConfig
) {
    fun getTextColorDay() = readColorConfig.textColorDay

    fun setTextColorDay(color: String) {
        readColorConfig.textColorDay = color
    }

    fun getTextColorNight() = readColorConfig.textColorNight

    fun setTextColorNight(color: String) {
        readColorConfig.textColorNight = color
    }

    fun getBgColorDay() = readColorConfig.bgColorDay

    fun setBgColorDay(color: String) {
        readColorConfig.bgColorDay = color
    }

    fun getBgColorNight() = readColorConfig.bgColorNight

    fun setBgColorNight(color: String) {
        readColorConfig.bgColorNight = color
    }
}