package com.cyh128.hikari_novel.data.repository

import android.accounts.NetworkErrorException
import android.content.pm.PackageManager
import com.cyh128.hikari_novel.HikariApp
import com.cyh128.hikari_novel.data.model.AppTheme
import com.cyh128.hikari_novel.data.model.DarkMode
import com.cyh128.hikari_novel.data.model.DefaultTab
import com.cyh128.hikari_novel.data.model.Language
import com.cyh128.hikari_novel.data.model.ReaderOrientation
import com.cyh128.hikari_novel.data.source.local.mmkv.AppConfig
import com.cyh128.hikari_novel.data.source.remote.Network
import com.google.gson.Gson
import rxhttp.awaitResult
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AppRepository @Inject constructor(
    private val appConfig: AppConfig,
    private val network: Network
) {
    //获取首次启动状态
    fun getIsFirstLaunch() = appConfig.isFirstLaunch

    //设置首次启动状态
    fun setIsFirstLaunch(isFirstLaunch: Boolean) {
        appConfig.isFirstLaunch = isFirstLaunch
    }

    //获取横向阅读器首次启动状态
    fun getIsHorizontalFirstLaunch() = appConfig.isHorizontalFirstLaunch

    //设置横向阅读器首次启动状态
    fun setIsHorizontalFirstLaunch(isHorizontalFirstLaunch: Boolean) {
        appConfig.isHorizontalFirstLaunch = isHorizontalFirstLaunch
    }

    //获取自动更新状态
    fun getIsAutoUpdate() = appConfig.isAutoUpdate

    //设置自动更新状态
    fun setIsAutoUpdate(isAutoUpdate: Boolean) {
        appConfig.isAutoUpdate = isAutoUpdate
    }

    //获取阅读器阅读方式
    fun getReaderOrientation() = enumValues<ReaderOrientation>()[appConfig.readerOrientation]

    //设置阅读器阅读方式
    fun setReaderOrientation(readerOrientation: ReaderOrientation) {
        appConfig.readerOrientation = readerOrientation.ordinal
    }

    fun getLanguage() = enumValues<Language>()[appConfig.language]

    fun setLanguage(language: Language) {
        appConfig.language = language.ordinal
    }

    fun getDefaultTab() = enumValues<DefaultTab>()[appConfig.defaultTab]

    fun setDefaultTab(defaultTab: DefaultTab) {
        appConfig.defaultTab = defaultTab.ordinal
    }

    fun getAppTheme() = enumValues<AppTheme>()[appConfig.appTheme]

    fun setAppTheme(appTheme: AppTheme) {
        appConfig.appTheme = appTheme.ordinal
    }

    fun getDarkMode() = enumValues<DarkMode>()[appConfig.darkMode]

    fun setDarkMode(darkMode: DarkMode) {
        appConfig.darkMode = darkMode.ordinal
    }

    //检查更新
    suspend fun checkUpdate(): Result<Boolean> {
        network.getWithoutCookie("https://api.github.com/repos/15dd/wenku8reader/releases/latest")
            .awaitResult {
                it.body()?.let { data ->
                    val result = Gson().fromJson(data.string(), Map::class.java)
                    return if (result["tag_name"] == getVersion()) Result.success(false)
                    else Result.success(true)
                }
                return Result.success(false)
            }.onFailure {
                return Result.failure(NetworkErrorException(it.message))
            }
        throw RuntimeException()
    }

    private fun getVersion(): String? {
        val manager = HikariApp.application.packageManager
        var name: String? = null
        try {
            val info = manager.getPackageInfo(HikariApp.application.packageName, 0)
            name = info.versionName
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
        }
        return name
    }
}