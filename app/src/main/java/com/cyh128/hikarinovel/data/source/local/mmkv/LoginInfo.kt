package com.cyh128.hikarinovel.data.source.local.mmkv

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
}