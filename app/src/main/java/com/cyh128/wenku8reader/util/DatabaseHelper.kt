package com.cyh128.wenku8reader.util

import android.content.ContentValues

object DatabaseHelper {
    fun SaveOldReaderReadHistory(
        bookUrl: String?,
        indexUrl: String?,
        title: String?,
        location: Int
    ) {
        val values = ContentValues()
        values.put("bookUrl", bookUrl)
        values.put("indexUrl", indexUrl)
        values.put("title", title)
        values.put("location", location)
        GlobalConfig.db.replace("old_reader_read_history", null, values)
    }

    fun SaveNewReaderReadHistory(
        bookUrl: String?,
        indexUrl: String?,
        title: String?,
        location: Int
    ) {
        val values = ContentValues()
        values.put("bookUrl", bookUrl)
        values.put("indexUrl", indexUrl)
        values.put("title", title)
        values.put("location", location)
        GlobalConfig.db.replace("new_reader_read_history", null, values)
    }

    fun SaveSetting() {
        val values = ContentValues()
        values.put("_id", 1)
        values.put("checkUpdate", GlobalConfig.checkUpdate)
        values.put("bookcaseViewType", GlobalConfig.bookcaseViewType)
        values.put("readerMode", GlobalConfig.readerMode)
        GlobalConfig.db.replace("setting", null, values)
    }

    fun SaveReaderSetting() {
        val values = ContentValues()
        values.put("_id", 1)
        values.put("newFontSize", GlobalConfig.newReaderFontSize)
        values.put("newLineSpacing", GlobalConfig.newReaderLineSpacing)
        values.put("oldFontSize", GlobalConfig.oldReaderFontSize)
        values.put("oldLineSpacing", GlobalConfig.oldReaderLineSpacing)
        values.put("bottomTextSize", GlobalConfig.readerBottomTextSize)
        values.put("isUpToDown", GlobalConfig.isUpToDown)
        values.put("canSwitchChapterByScroll", GlobalConfig.canSwitchChapterByScroll)
        values.put("backgroundColorDay", GlobalConfig.backgroundColorDay)
        values.put("backgroundColorNight", GlobalConfig.backgroundColorNight)
        values.put("textColorDay", GlobalConfig.textColorDay)
        values.put("textColorNight", GlobalConfig.textColorNight)
        values.put("canSwitchPageByVolumeKey", GlobalConfig.canSwitchPageByVolumeKey)
        GlobalConfig.db.replace("reader", null, values)
    }
}
