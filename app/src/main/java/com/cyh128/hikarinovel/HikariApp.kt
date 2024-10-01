package com.cyh128.hikarinovel

import android.app.Application
import com.cyh128.hikarinovel.data.model.Language
import com.cyh128.hikarinovel.ui.view.other.CrashActivity
import com.developer.crashx.config.CrashConfig
import com.google.android.material.color.DynamicColors
import com.tencent.mmkv.MMKV
import com.yariksoffice.lingver.Lingver
import dagger.hilt.android.HiltAndroidApp
import okhttp3.OkHttpClient
import rxhttp.RxHttpPlugins
import rxhttp.wrapper.cookie.CookieStore
import java.util.Locale
import javax.net.ssl.SSLSession


@HiltAndroidApp
class HikariApp : Application() {
    companion object {
        lateinit var application: Application
    }

    override fun onCreate() {
        super.onCreate()
        application = this
        DynamicColors.applyToActivitiesIfAvailable(this)
        MMKV.initialize(this)
        val client: OkHttpClient = OkHttpClient.Builder()
            .cookieJar(CookieStore()) //保存cookie
            .hostnameVerifier { _: String?, _: SSLSession? -> true }
            .build()
        RxHttpPlugins.init(client)
        CrashConfig.Builder.create()
            .errorActivity(CrashActivity::class.java)
            .apply()

        Lingver.init(this, Locale.SIMPLIFIED_CHINESE)
        when(getLanguage()) {
            Language.FOLLOW_SYSTEM -> Lingver.getInstance().setFollowSystemLocale(this)
            Language.ZH_CN -> Lingver.getInstance().setLocale(this, Locale.SIMPLIFIED_CHINESE)
            Language.ZH_TW -> Lingver.getInstance().setLocale(this, Locale.TRADITIONAL_CHINESE)
        }
    }

    private fun getLanguage(): Language { //TODO 待改进
        val cursor = MMKV.mmkvWithID("app_config")
        return enumValues<Language>()[cursor.decodeInt("language", Language.FOLLOW_SYSTEM.ordinal)]
    }
}