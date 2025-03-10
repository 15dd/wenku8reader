package com.cyh128.hikari_novel

import android.app.Application
import android.util.Log
import com.bumptech.glide.Glide
import com.bumptech.glide.integration.okhttp3.OkHttpUrlLoader
import com.bumptech.glide.load.model.GlideUrl
import com.cyh128.hikari_novel.data.repository.AppRepository
import com.cyh128.hikari_novel.data.repository.Wenku8Repository
import com.cyh128.hikari_novel.ui.other.CrashActivity
import com.cyh128.hikari_novel.util.LanguageHelper
import com.cyh128.hikari_novel.util.ThemeHelper
import com.developer.crashx.config.CrashConfig
import com.tencent.mmkv.MMKV
import dagger.hilt.android.HiltAndroidApp
import okhttp3.Cookie
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import rxhttp.RxHttpPlugins
import rxhttp.wrapper.cookie.CookieStore
import java.io.InputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone
import javax.inject.Inject
import javax.net.ssl.SSLSession


@HiltAndroidApp
class HikariApp : Application() {

    @Inject
    lateinit var appRepository: AppRepository

    @Inject
    lateinit var wenku8Repository: Wenku8Repository

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
            .addInterceptor(CookieInterceptor())
            .cookieJar(CookieStore()) //保存cookie
            .hostnameVerifier { _: String?, _: SSLSession? -> true }
//            .protocols(Collections.singletonList(Protocol.HTTP_1_1))
            .build()

        Glide.get(this).registry
            .replace(
                GlideUrl::class.java,
                InputStream::class.java, OkHttpUrlLoader.Factory(client)
            )

        RxHttpPlugins.init(client)
        CrashConfig.Builder.create()
            .errorActivity(CrashActivity::class.java)
            .apply()
    }

    inner class CookieInterceptor: Interceptor {
        override fun intercept(chain: Interceptor.Chain): Response {
            val request = chain.request()
            val response = chain.proceed(request)

            val setCookies = response.headers("Set-Cookie")

            if (setCookies.isNotEmpty()) {
                Log.d("c_i","从拦截器中获取 Set-Cookie:")
                Log.d("c_i","当前url: ${request.url}")
                if (request.url.toString().startsWith("https://www.wenku8")) {
                    val httpUrl = request.url
                    setCookies.forEach { setCookie ->
                        val cookie = Cookie.parse(httpUrl, setCookie)
                        if (cookie != null && cookie.name == "jieqiUserInfo") {
                            Log.d("c_i","Cookie 名称: ${cookie.name}")
                            Log.d("c_i","Cookie 值: ${cookie.value}")
                            Log.d("c_i","过期时间 (毫秒): ${cookie.expiresAt}")
                            Log.d("c_i","过期时间 (GMT): ${timestampToGMT(cookie.expiresAt)}")
                            wenku8Repository.cookie = cookie.name + "=" + cookie.value + ";"
                            wenku8Repository.expDate = cookie.expiresAt
                            if (cookie.expiresAt == 253402300799999) {
                                wenku8Repository.expDate = 0
                            }
                            Log.d("c_i","本地cookie: ${wenku8Repository.cookie}")
                            Log.d("c_i","本地cookie过期时间 (毫秒): ${wenku8Repository.expDate}")
                        }
                    }
                }
            }
            return response
        }

        // 工具方法：时间戳转 GMT 格式
        private fun timestampToGMT(timestamp: Long): String {
            val sdf = SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss 'GMT'", Locale.US)
            sdf.timeZone = TimeZone.getTimeZone("GMT")
            return sdf.format(Date(timestamp))
        }
    }
}