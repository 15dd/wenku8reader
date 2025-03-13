package com.cyh128.hikari_novel.ui.main.more.more.setting

import androidx.lifecycle.ViewModel
import com.cyh128.hikari_novel.data.model.AppTheme
import com.cyh128.hikari_novel.data.model.DarkMode
import com.cyh128.hikari_novel.data.model.DefaultTab
import com.cyh128.hikari_novel.data.model.Language
import com.cyh128.hikari_novel.data.model.ListViewType
import com.cyh128.hikari_novel.data.model.ReaderOrientation
import com.cyh128.hikari_novel.data.repository.AppRepository
import com.cyh128.hikari_novel.data.repository.Wenku8Repository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SettingViewModel @Inject constructor(
    private val appRepository: AppRepository,
    private val wenku8Repository: Wenku8Repository
): ViewModel() {
    fun getIsAutoUpdate() = appRepository.getIsAutoUpdate()

    fun setIsAutoUpdate(isAutoUpdate: Boolean) = appRepository.setIsAutoUpdate(isAutoUpdate)

    fun getReaderOrientation() = appRepository.getReaderOrientation()

    fun setReaderOrientation(orientation: ReaderOrientation) = appRepository.setReaderOrientation(orientation)

    fun getWenku8Node() = wenku8Repository.getWenku8Node()

    fun setWenku8Node(node: String) = wenku8Repository.setWenku8Node(node)

    fun getLanguage() = appRepository.getLanguage()

    fun setLanguage(language: Language) = appRepository.setLanguage(language)

    fun setDefaultTab(defaultTab: DefaultTab) = appRepository.setDefaultTab(defaultTab)

    fun getDefaultTab() = appRepository.getDefaultTab()

    fun getAppTheme() = appRepository.getAppTheme()

    fun setAppTheme(theme: AppTheme) = appRepository.setAppTheme(theme)

    fun getDarkMode() = appRepository.getDarkMode()

    fun setDarkMode(darkMode: DarkMode) = appRepository.setDarkMode(darkMode)

    fun getListViewType() = appRepository.getListViewType()

    fun setListViewType(listViewType: ListViewType) = appRepository.setListViewType(listViewType)
}