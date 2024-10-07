package com.cyh128.hikari_novel.data.source.local.mmkv

import com.tencent.mmkv.MMKV
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ReadColorConfig @Inject constructor() {
    private val cursor = MMKV.mmkvWithID("read_color_config")

    var textColorDay: String
        get() = cursor.decodeString("text_color_day", "000000")!!
        set(value) {
            cursor.encode("text_color_day",value)
        }

    var textColorNight: String
        get() = cursor.decodeString("text_color_night","ffffff")!!
        set(value) {
            cursor.encode("text_color_night",value)
        }

    var bgColorDay: String
        get() = cursor.decodeString("bg_color_day","ffffff")!!
        set(value) {
            cursor.encode("bg_color_day",value)
        }

    var bgColorNight: String
        get() = cursor.decodeString("bg_color_night","000000")!!
        set(value) {
            cursor.encode("bg_color_night",value)
        }
}