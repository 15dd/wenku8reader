package com.cyh128.hikari_novel.ui.view.splash

import android.content.DialogInterface
import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProvider
import com.cyh128.hikari_novel.R
import com.cyh128.hikari_novel.base.BaseFragment
import com.cyh128.hikari_novel.databinding.FragmentLoginErrorBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LoginErrorFragment : BaseFragment<FragmentLoginErrorBinding>() {
    private val viewModel by lazy { ViewModelProvider(requireActivity())[SplashViewModel::class.java] }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.bFLoginError.setOnClickListener {
            viewModel.login()
            viewModel.setLoggingInText(getString(R.string.logging))
            (requireActivity() as SplashActivity).toLoggingInScreen()
        }
        binding.tvFLoginError.text = requireArguments().getString("msg")

        binding.bFLoginErrorDomain.setOnClickListener {
            MaterialAlertDialogBuilder(requireContext())
                .setTitle(R.string.switch_node)
                .setIcon(R.drawable.ic_network_node)
                .setNegativeButton(R.string.cancel, null)
                .setSingleChoiceItems(
                    arrayOf("www.wenku8.cc", "www.wenku8.net"),
                    run {
                        if (viewModel.getWenku8Node() == "www.wenku8.cc") return@run 0
                        else return@run 1
                    }
                ) { dialog: DialogInterface, which: Int ->
                    when (which) {
                        0 -> viewModel.setWenku8Node("www.wenku8.cc")
                        1 -> viewModel.setWenku8Node("www.wenku8.net")
                    }
                    dialog.dismiss()
                }
                .show()
        }
    }
}