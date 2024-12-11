package com.cyh128.hikari_novel.ui.view.main.more.more.setting

import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProvider
import com.cyh128.hikari_novel.R
import com.cyh128.hikari_novel.data.model.AppTheme
import com.cyh128.hikari_novel.util.ThemeHelper
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class ThemeListDialogFragment: DialogFragment() {
    private val viewModel by lazy { ViewModelProvider(requireActivity())[SettingViewModel::class.java] }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val view = layoutInflater.inflate(R.layout.view_theme_list, null)

        when (viewModel.getAppTheme()) {
            AppTheme.Dynamic -> {
                view.findViewById<Chip>(R.id.c_v_theme_list_dynamic_color).isChecked = true
            }

            AppTheme.GreenApple -> {
                view.findViewById<Chip>(R.id.c_v_theme_list_green_apple).isChecked = true
            }

            AppTheme.Lavender -> {
                view.findViewById<Chip>(R.id.c_v_theme_list_lavender).isChecked = true
            }

            AppTheme.MidnightDusk -> {
                view.findViewById<Chip>(R.id.c_v_theme_list_midnightdusk).isChecked = true
            }

            AppTheme.Nord -> {
                view.findViewById<Chip>(R.id.c_v_theme_list_nord).isChecked = true
            }

            AppTheme.StrawberryDaiquiri -> {
                view.findViewById<Chip>(R.id.c_v_theme_list_strawberry).isChecked = true
            }

            AppTheme.Tako -> {
                view.findViewById<Chip>(R.id.c_v_theme_list_tako).isChecked = true
            }

            AppTheme.TealTurquoise -> {
                view.findViewById<Chip>(R.id.c_v_theme_list_tealturquoise).isChecked = true
            }

            AppTheme.TidalWave -> {
                view.findViewById<Chip>(R.id.c_v_theme_list_tidalwave).isChecked = true
            }

            AppTheme.YinYang -> {
                view.findViewById<Chip>(R.id.c_v_theme_list_yinyang).isChecked = true
            }

            AppTheme.Yotsuba -> {
                view.findViewById<Chip>(R.id.c_v_theme_list_yotsuba).isChecked = true
            }
        }

        view.findViewById<ChipGroup>(R.id.cg_v_theme_list).setOnCheckedStateChangeListener { group, checkedIds ->
            if (checkedIds.isNotEmpty()) {
                val selectedChipId = checkedIds[0]
                val index = group.indexOfChild(group.findViewById(selectedChipId))

                when (index) {
                    0 -> {
                        viewModel.setAppTheme(AppTheme.Dynamic)
                        ThemeHelper.setCurrentTheme(AppTheme.Dynamic)
                        (requireActivity() as SettingActivity).binding.tvASettingAppTheme.text = getString(R.string.dynamic_color)
                    }

                    1 -> {
                        viewModel.setAppTheme(AppTheme.GreenApple)
                        ThemeHelper.setCurrentTheme(AppTheme.GreenApple)
                        (requireActivity() as SettingActivity).binding.tvASettingAppTheme.text = getString(R.string.green_apple)
                    }

                    2 -> {
                        viewModel.setAppTheme(AppTheme.Lavender)
                        ThemeHelper.setCurrentTheme(AppTheme.Lavender)
                        (requireActivity() as SettingActivity).binding.tvASettingAppTheme.text = getString(R.string.lavender)
                    }

                    3 -> {
                        viewModel.setAppTheme(AppTheme.MidnightDusk)
                        ThemeHelper.setCurrentTheme(AppTheme.MidnightDusk)
                        (requireActivity() as SettingActivity).binding.tvASettingAppTheme.text = getString(R.string.midnight_dusk)
                    }

                    4 -> {
                        viewModel.setAppTheme(AppTheme.Nord)
                        ThemeHelper.setCurrentTheme(AppTheme.Nord)
                        (requireActivity() as SettingActivity).binding.tvASettingAppTheme.text = getString(R.string.nord)
                    }

                    5 -> {
                        viewModel.setAppTheme(AppTheme.StrawberryDaiquiri)
                        ThemeHelper.setCurrentTheme(AppTheme.StrawberryDaiquiri)
                        (requireActivity() as SettingActivity).binding.tvASettingAppTheme.text = getString(R.string.strawberry_daiquiri)
                    }

                    6 -> {
                        viewModel.setAppTheme(AppTheme.Tako)
                        ThemeHelper.setCurrentTheme(AppTheme.Tako)
                        (requireActivity() as SettingActivity).binding.tvASettingAppTheme.text = getString(R.string.tako)
                    }

                    7 -> {
                        viewModel.setAppTheme(AppTheme.TealTurquoise)
                        ThemeHelper.setCurrentTheme(AppTheme.TealTurquoise)
                        (requireActivity() as SettingActivity).binding.tvASettingAppTheme.text = getString(R.string.teal_turquoise)
                    }

                    8 -> {
                        viewModel.setAppTheme(AppTheme.TidalWave)
                        ThemeHelper.setCurrentTheme(AppTheme.TidalWave)
                        (requireActivity() as SettingActivity).binding.tvASettingAppTheme.text = getString(R.string.tidal_wave)
                    }

                    9 -> {
                        viewModel.setAppTheme(AppTheme.YinYang)
                        ThemeHelper.setCurrentTheme(AppTheme.YinYang)
                        (requireActivity() as SettingActivity).binding.tvASettingAppTheme.text = getString(R.string.yin_yang)
                    }

                    10 -> {
                        viewModel.setAppTheme(AppTheme.Yotsuba)
                        ThemeHelper.setCurrentTheme(AppTheme.Yotsuba)
                        (requireActivity() as SettingActivity).binding.tvASettingAppTheme.text = getString(R.string.yotsuba)
                    }
                }
            }
        }

        return MaterialAlertDialogBuilder(requireContext())
            .setTitle(R.string.app_theme)
            .setIcon(R.drawable.ic_palette)
            .setNegativeButton(R.string.cancel, null)
            .setView(view)
            .create()
    }
}