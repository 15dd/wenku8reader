package com.cyh128.hikari_novel

import android.app.Application
import com.cyh128.hikari_novel.data.repository.AppRepository
import com.cyh128.hikari_novel.ui.view.other.CrashActivity
import com.cyh128.hikari_novel.util.LanguageHelper
import com.cyh128.hikari_novel.util.ThemeHelper
import com.developer.crashx.config.CrashConfig
import com.tencent.mmkv.MMKV
import dagger.hilt.android.HiltAndroidApp
import okhttp3.OkHttpClient
import rxhttp.RxHttpPlugins
import rxhttp.wrapper.cookie.CookieStore
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

        ThemeHelper.setCurrentTheme(appRepository.getAppTheme())
        ThemeHelper.setDarkMode(appRepository.getDarkMode())
        LanguageHelper.setCurrentLanguage(appRepository.getLanguage())

        application = this
        val client: OkHttpClient = OkHttpClient.Builder()
            .cookieJar(CookieStore()) //保存cookie
            .hostnameVerifier { _: String?, _: SSLSession? -> true }
            .build()
        RxHttpPlugins.init(client)
        CrashConfig.Builder.create()
            .errorActivity(CrashActivity::class.java)
            .apply()
    }
}