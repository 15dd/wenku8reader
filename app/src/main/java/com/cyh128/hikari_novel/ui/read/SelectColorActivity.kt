package com.cyh128.hikari_novel.ui.read

import android.os.Bundle
import android.view.View
import androidx.core.view.WindowCompat
import androidx.lifecycle.ViewModelProvider
import com.cyh128.hikari_novel.R
import com.cyh128.hikari_novel.base.BaseActivity
import com.cyh128.hikari_novel.databinding.ActivitySelectColorBinding
import com.cyh128.hikari_novel.util.getIsInDarkMode
import dagger.hilt.android.AndroidEntryPoint
import java.util.regex.Pattern

@AndroidEntryPoint
class SelectColorActivity : BaseActivity<ActivitySelectColorBinding>() {
    private val viewModel by lazy { ViewModelProvider(this)[SelectColorViewModel::class.java] }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setSupportActionBar(binding.tbASelectColor)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        binding.tbASelectColor.setNavigationOnClickListener {
            finish()
        }

        WindowCompat.setDecorFitsSystemWindows(window, false)

        //初始化界面
        if (getIsInDarkMode()) {
            binding.tbASelectColor.title = getString(R.string.select_color_night)
            binding.cvASelectColorRecommendDay.visibility = View.GONE
            binding.tietASelectColorText.setText(viewModel.getTextColorNight())
            binding.tietASelectColorBg.setText(viewModel.getBgColorNight())
        } else {
            binding.tbASelectColor.title = getString(R.string.select_color_day)
            binding.cvASelectColorRecommendNight.visibility = View.GONE
            binding.tietASelectColorText.setText(viewModel.getTextColorDay())
            binding.tietASelectColorBg.setText(viewModel.getBgColorDay())
        }

        binding.bASelectColorSave.setOnClickListener {
            binding.tlASelectColorText.error = null
            binding.tlASelectColorBg.error = null

            //检查格式
            Pattern.matches(
                "^([A-Fa-f0-9]{6}|[A-Fa-f0-9]{3})$",
                binding.tietASelectColorText.text.toString()
            ).also {
                if (!it) {
                    binding.tlASelectColorText.error = getString(R.string.format_mismatch)
                    return@setOnClickListener
                }
            }
            Pattern.matches(
                "^([A-Fa-f0-9]{6}|[A-Fa-f0-9]{3})$",
                binding.tietASelectColorBg.text.toString()
            ).also {
                if (!it) {
                    binding.tlASelectColorBg.error = getString(R.string.format_mismatch)
                    return@setOnClickListener
                }
            }

            if (getIsInDarkMode()) {  //夜间模式
                viewModel.setTextColorNight(binding.tietASelectColorText.text.toString())
                viewModel.setBgColorNight(binding.tietASelectColorBg.text.toString())
            } else {
                viewModel.setTextColorDay(binding.tietASelectColorText.text.toString())
                viewModel.setBgColorDay(binding.tietASelectColorBg.text.toString())
            }

            setResult(RESULT_OK)
            finish()
        }

        binding.bASelectColorCancel.setOnClickListener {
            setResult(RESULT_CANCELED)
            finish()
        }
    }
}
