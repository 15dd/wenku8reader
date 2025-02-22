package com.cyh128.hikari_novel.data.source.local.mmkv

import com.tencent.mmkv.MMKV
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BookshelfInfo @Inject constructor() {
    private val cursor = MMKV.mmkvWithID("bookshelf_info")

    var maxCollection: Int
        get() = cursor.decodeInt("max_collection", -1)
        set(value) {
            cursor.encode("max_collection", value)
        }
}