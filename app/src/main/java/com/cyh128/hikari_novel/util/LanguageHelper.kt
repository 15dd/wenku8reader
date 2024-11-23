package com.cyh128.hikari_novel.util

import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat
import com.cyh128.hikari_novel.data.model.Event
import com.cyh128.hikari_novel.data.model.Language
import com.drake.channel.sendEvent

object LanguageHelper {
    private lateinit var currentLanguage: Language

    fun setCurrentLanguage(language: Language) {
        currentLanguage = language
        sendEvent(Event.LanguageChantedEvent, "event_language_changed")
    }

    fun initLanguage() {
        val locale = when (currentLanguage) {
            Language.FOLLOW_SYSTEM -> LocaleListCompat.getEmptyLocaleList()
            Language.ZH_CN -> LocaleListCompat.forLanguageTags("zh-cn")
            Language.ZH_TW -> LocaleListCompat.forLanguageTags("zh-tw")
        }
        AppCompatDelegate.setApplicationLocales(locale)
    }
}