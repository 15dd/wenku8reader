package com.cyh128.hikari_novel

import android.app.Application
import com.cyh128.hikari_novel.data.model.Language
import com.cyh128.hikari_novel.data.repository.AppRepository
import com.cyh128.hikari_novel.ui.view.other.CrashActivity
import com.cyh128.hikari_novel.util.ThemeHelper
import com.developer.crashx.config.CrashConfig
import com.tencent.mmkv.MMKV
import com.yariksoffice.lingver.Lingver
import dagger.hilt.android.HiltAndroidApp
import okhttp3.OkHttpClient
import rxhttp.RxHttpPlugins
import rxhttp.wrapper.cookie.CookieStore
import java.util.Locale
import javax.inject.Inject
import javax.net.ssl.SSLSession


@HiltAndroidApp
class HikariApp : Application() {

    @Inject
    lateinit var appRepository: AppRepository

    companion object {
        lateinit var application: Application
    }

    override fun onCreate() {
        MMKV.initialize(this)

        super.onCreate()

        application = this
        val client: OkHttpClient = OkHttpClient.Builder()
            .cookieJar(CookieStore()) //保存cookie
            .hostnameVerifier { _: String?, _: SSLSession? -> true }
            .build()
        RxHttpPlugins.init(client)
        CrashConfig.Builder.create()
            .errorActivity(CrashActivity::class.java)
            .apply()

        Lingver.init(this)
        when (appRepository.getLanguage()) {
            Language.FOLLOW_SYSTEM -> Lingver.getInstance().setFollowSystemLocale(this).apply {
                if (Lingver.getInstance().getLocale() != Locale.SIMPLIFIED_CHINESE && Lingver.getInstance().getLocale() != Locale.TRADITIONAL_CHINESE) {
                    Lingver.getInstance().setLocale(this@HikariApp, Locale.SIMPLIFIED_CHINESE)
                }
            }
            Language.ZH_CN -> Lingver.getInstance().setLocale(this, Locale.SIMPLIFIED_CHINESE)
            Language.ZH_TW -> Lingver.getInstance().setLocale(this, Locale.TRADITIONAL_CHINESE)
        }

        ThemeHelper.setCurrentTheme(appRepository.getAppTheme())
    }
}