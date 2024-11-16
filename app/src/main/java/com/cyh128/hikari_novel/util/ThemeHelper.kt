package com.cyh128.hikari_novel.util

import android.app.Activity
import android.app.Application
import androidx.annotation.StyleRes
import com.cyh128.hikari_novel.R
import com.cyh128.hikari_novel.data.model.AppTheme
import com.google.android.material.color.DynamicColors

object ThemeHelper {
    @StyleRes private var currentTheme: Int? = null

    fun setCurrentTheme(appTheme: AppTheme) {
        currentTheme = when(appTheme) {
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
    }

    fun initActivityTheme(activity: Activity) {
        if (currentTheme != null) activity.setTheme(currentTheme!!)
        else DynamicColors.applyToActivityIfAvailable(activity)
    }
}