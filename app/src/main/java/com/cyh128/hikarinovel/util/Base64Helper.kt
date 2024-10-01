package com.cyh128.hikarinovel.util

import android.util.Base64
import java.nio.charset.Charset

object Base64Helper {
    private fun encodeBase64(b: ByteArray): String {
        return Base64.encodeToString(b, Base64.DEFAULT).trim { it <= ' ' }
    }

    fun encodeBase64(s: String): String {
        return encodeBase64(s.toByteArray(Charset.forName("UTF-8")))
    }

    private fun decodeBase64(s: String): ByteArray {
        try {
            val b = Base64.decode(s, Base64.DEFAULT)
            return b
        } catch (e: IllegalArgumentException) {
            return ByteArray(0)
        }
    }

    fun decodeBase64String(s: String): String {
        return String(decodeBase64(s), Charset.forName("UTF-8"))
    }
}