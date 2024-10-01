package com.cyh128.hikarinovel.ui.view.splash

import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProvider
import com.cyh128.hikarinovel.base.BaseFragment
import com.cyh128.hikarinovel.databinding.FragmentLoggingInBinding
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
}