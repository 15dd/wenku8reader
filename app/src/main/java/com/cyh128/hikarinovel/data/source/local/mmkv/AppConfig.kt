package com.cyh128.hikarinovel.data.source.local.mmkv

import com.cyh128.hikarinovel.data.model.Language
import com.cyh128.hikarinovel.data.model.ReaderOrientation
import com.tencent.mmkv.MMKV
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AppConfig @Inject constructor() {
    private val cursor = MMKV.mmkvWithID("app_config")

    var isFirstLaunch: Boolean
        get() = cursor.decodeBool("is_first_launch", true)
        set(value) {
            cursor.encode("is_first_launch", value)
        }

    var isHorizontalFirstLaunch: Boolean
        get() = cursor.decodeBool("is_horizontal_first_launch", true)
        set(value) {
            cursor.encode("is_horizontal_first_launch", value)
        }

    var isAutoUpdate: Boolean
        get() = cursor.decodeBool("is_auto_update", true)
        set(value) {
            cursor.encode("is_auto_update", value)
        }

    var readerOrientation: Int
        get() = cursor.decodeInt("reader_orientation",ReaderOrientation.Horizontal.ordinal)
        set(value) {
            cursor.encode("reader_orientation", value)
        }

    //www.wenku8.net或www.wenku8.cc
    var node: String
        get() = cursor.decodeString("node", "www.wenku8.cc")!!
        set(value) {
            cursor.encode("node", value)
        }

    var language: Int
        get() = cursor.decodeInt("language",Language.FOLLOW_SYSTEM.ordinal)
        set(value) {
            cursor.encode("language", value)
        }
}