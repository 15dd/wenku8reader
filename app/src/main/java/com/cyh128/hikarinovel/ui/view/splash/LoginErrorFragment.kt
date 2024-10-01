package com.cyh128.hikarinovel.ui.view.splash

import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProvider
import com.cyh128.hikarinovel.base.BaseFragment
import com.cyh128.hikarinovel.databinding.FragmentLoginErrorBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LoginErrorFragment: BaseFragment<FragmentLoginErrorBinding>() {
    private val viewModel by lazy { ViewModelProvider(requireActivity())[SplashViewModel::class.java] }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.bFLoginError.setOnClickListener {
            viewModel.login()
            viewModel.emitToLoggingInScreenSignal()
        }
        binding.tvFLoginError.text = requireArguments().getString("msg")
    }
}