package com.cyh128.hikari_novel.ui.splash

import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProvider
import com.cyh128.hikari_novel.base.BaseFragment
import com.cyh128.hikari_novel.databinding.FragmentLoggingInBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LoggingInFragment: BaseFragment<FragmentLoggingInBinding>() {
    private val viewModel by lazy { ViewModelProvider(requireActivity())[SplashViewModel::class.java] }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.loggingInText.observe(viewLifecycleOwner) {
            binding.tvFLoggingIn.text = it
        }
    }

    override fun onDestroyView() {
        binding.liFLoggingIn.clearAnimation() //防止内存泄漏
        super.onDestroyView()
    }
}