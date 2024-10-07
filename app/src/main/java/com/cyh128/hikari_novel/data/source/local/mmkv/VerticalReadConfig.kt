package com.cyh128.hikari_novel.data.source.local.mmkv

import com.tencent.mmkv.MMKV
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class VerticalReadConfig @Inject constructor() {
    private val cursor = MMKV.mmkvWithID("vertical_read_config")

    var fontSize: Float
        get() = cursor.decodeFloat("font_size",16f)
        set(value) {
            cursor.encode("font_size",value)
        }

    var lineSpacing: Float
        get() = cursor.decodeFloat("line_spacing",1f)
        set(value) {
            cursor.encode("line_spacing",value)
        }

    var isShowChapterReadHistory: Boolean
        get() = cursor.decodeBool("is_show_chapter_read_history", true)
        set(value) {
            cursor.encode("is_show_chapter_read_history", value)
        }

    var keepScreenOn: Boolean
        get() = cursor.decodeBool("keep_screen_on", false)
        set(value) {
            cursor.encode("keep_screen_on", value)
        }
}