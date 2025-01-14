package com.cyh128.hikari_novel.data.source.local.mmkv

import com.tencent.mmkv.MMKV
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LoginInfo @Inject constructor() {
    private val cursor = MMKV.mmkvWithID("loginInfo")

    var username: String?
        get() = cursor.decodeString("username")
        set(value) {
            cursor.encode("username",value)
        }

    var password: String?
        get() = cursor.decodeString("password")
        set(value) {
            cursor.encode("password",value)
        }

    var cookie: String?
        get() = cursor.decodeString("cookie")
        set(value) {
            cursor.encode("cookie",value)
        }

    //毫秒级时间戳
    var expDate: Long?
        get() = cursor.decodeLong("exp_date")
        set(value) {
            if (value != null) {
                cursor.encode("exp_date",value)
            }
        }
}