package com.cyh128.hikari_novel.util

import android.app.Activity
import androidx.annotation.StyleRes
import androidx.appcompat.app.AppCompatDelegate
import com.cyh128.hikari_novel.R
import com.cyh128.hikari_novel.data.model.AppTheme
import com.cyh128.hikari_novel.data.model.DarkMode
import com.cyh128.hikari_novel.data.model.Event
import com.drake.channel.sendEvent
import com.google.android.material.color.DynamicColors

object ThemeHelper {
    @StyleRes
    private var currentTheme: Int? = null
    private lateinit var currentDarkMode: DarkMode

    fun setCurrentTheme(appTheme: AppTheme) {
        currentTheme = when (appTheme) {
            AppTheme.Dynamic -> null
            AppTheme.GreenApple -> R.style.Theme_HikariNovel_GreenApple
            AppTheme.Lavender -> R.style.Theme_HikariNovel_Lavender
            AppTheme.MidnightDusk -> R.style.Theme_HikariNovel_MidnightDusk
            AppTheme.Nord -> R.style.Theme_HikariNovel_Nord
            AppTheme.StrawberryDaiquiri -> R.style.Theme_HikariNovel_StrawberryDaiquiri
            AppTheme.Tako -> R.style.Theme_HikariNovel_Tako
            AppTheme.TealTurquoise -> R.style.Theme_HikariNovel_TealTurquoise
            AppTheme.TidalWave -> R.style.Theme_HikariNovel_TidalWave
            AppTheme.YinYang -> R.style.Theme_HikariNovel_YinYang
            AppTheme.Yotsuba -> R.style.Theme_HikariNovel_Yotsuba
        }
        sendEvent(Event.ThemeChangedEvent, "event_theme_changed")
    }

    fun setDarkMode(darkMode: DarkMode) {
        currentDarkMode = darkMode
        sendEvent(Event.ThemeChangedEvent, "event_theme_changed")
    }

    fun initActivityThemeAndDarkMode(activity: Activity) {
        when (currentDarkMode) {
            DarkMode.System -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
            DarkMode.Enable -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            DarkMode.Disable -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        }

        if (currentTheme != null) activity.setTheme(currentTheme!!)
        else DynamicColors.applyToActivitiesIfAvailable(activity.application)
    }
}