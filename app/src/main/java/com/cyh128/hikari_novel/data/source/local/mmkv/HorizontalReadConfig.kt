package com.cyh128.hikari_novel.data.source.local.mmkv

import com.tencent.mmkv.MMKV
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class HorizontalReadConfig @Inject constructor() {
    private val cursor = MMKV.mmkvWithID("horizontal_read_config")

    var fontSize: Float
        get() = cursor.decodeFloat("font_size",55f)
        set(value) {
            cursor.encode("font_size", value)
        }

    var lineSpacing: Float
        get() = cursor.decodeFloat("line_spacing",1.5f)
        set(value) {
            cursor.encode("line_spacing", value)
        }

    var bottomTextSize: Float
        get() = cursor.decodeFloat("bottom_text_size", 45f)
        set(value) {
            cursor.encode("bottom_text_size", value)
        }

    var keyDownSwitchChapter: Boolean
        get() = cursor.decodeBool("key_down_switch_chapter", false)
        set(value) {
            cursor.encode("key_down_switch_chapter", value)
        }

    var switchAnimation: Boolean
        get() = cursor.decodeBool("switch_animation", false)
        set(value) {
            cursor.encode("switch_animation", value)
        }

    var keepScreenOn: Boolean
        get() = cursor.decodeBool("keep_screen_on", false)
        set(value) {
            cursor.encode("keep_screen_on", value)
        }

    var isShowChapterReadHistory: Boolean
        get() = cursor.decodeBool("is_show_chapter_read_history", true)
        set(value) {
            cursor.encode("is_show_chapter_read_history", value)
        }
}