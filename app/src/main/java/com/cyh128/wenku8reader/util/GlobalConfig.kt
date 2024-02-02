package com.cyh128.wenku8reader.util

import android.database.sqlite.SQLiteDatabase

object GlobalConfig {
    //app全局变量存放处
    var newReaderFontSize = 0f
    var newReaderLineSpacing = 0f
    var oldReaderFontSize = 0f
    var oldReaderLineSpacing = 0f
    var readerBottomTextSize = 0f
    var isUpToDown = false
    var canSwitchChapterByScroll = false
    var backgroundColorDay: String? = null
    var backgroundColorNight: String? = null
    var textColorDay: String? = null
    var textColorNight: String? = null
    var checkUpdate = false
    var bookcaseViewType = false
    lateinit var db: SQLiteDatabase
    var isFiveSecondDone = true
    var readerMode = 0
}
