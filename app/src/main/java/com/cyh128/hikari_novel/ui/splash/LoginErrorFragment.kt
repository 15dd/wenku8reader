package com.cyh128.hikari_novel.ui.splash

import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProvider
import com.cyh128.hikari_novel.R
import com.cyh128.hikari_novel.base.BaseFragment
import com.cyh128.hikari_novel.databinding.FragmentLoginErrorBinding
import com.cyh128.hikari_novel.ui.main.more.more.setting.SettingActivity
import com.cyh128.hikari_novel.util.startActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LoginErrorFragment : BaseFragment<FragmentLoginErrorBinding>() {
    private val viewModel by lazy { ViewModelProvider(requireActivity())[SplashViewModel::class.java] }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.bFLoginError.setOnClickListener {
            viewModel.setLoggingInText(getString(R.string.logging))
            (requireActivity() as SplashActivity).startFragment<LoggingInFragment>()
        }
        binding.tvFLoginError.text = requireArguments().getString("msg")

        binding.bFLoginErrorSetting.setOnClickListener {
            startActivity<SettingActivity>()
        }
    }
}