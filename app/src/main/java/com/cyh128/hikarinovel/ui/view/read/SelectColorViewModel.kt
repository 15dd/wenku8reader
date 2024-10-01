package com.cyh128.hikarinovel.ui.view.read

import androidx.lifecycle.ViewModel
import com.cyh128.hikarinovel.data.repository.ReadColorRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SelectColorViewModel @Inject constructor(
    private val readColorRepository: ReadColorRepository
): ViewModel() {
    fun getTextColorDay() = readColorRepository.getTextColorDay()

    fun setTextColorDay(color: String) = readColorRepository.setTextColorDay(color)

    fun getTextColorNight() = readColorRepository.getTextColorNight()

    fun setTextColorNight(color: String) = readColorRepository.setTextColorNight(color)

    fun getBgColorDay() = readColorRepository.getBgColorDay()

    fun setBgColorDay(color: String) = readColorRepository.setBgColorDay(color)

    fun getBgColorNight() = readColorRepository.getBgColorNight()

    fun setBgColorNight(color: String) = readColorRepository.setBgColorNight(color)
}