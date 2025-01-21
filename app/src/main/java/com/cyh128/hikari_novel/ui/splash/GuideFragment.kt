package com.cyh128.hikari_novel.ui.splash

import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProvider
import com.cyh128.hikari_novel.R
import com.cyh128.hikari_novel.base.BaseFragment
import com.cyh128.hikari_novel.databinding.FragmentGuideBinding
import com.cyh128.hikari_novel.util.openUrl
import com.cyh128.hikari_novel.util.startActivity
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class GuideFragment: BaseFragment<FragmentGuideBinding>() {
    private val viewModel by lazy { ViewModelProvider(requireActivity())[SplashViewModel::class.java] }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.cbFGuideAccept.setOnCheckedChangeListener { _, isChecked ->
            binding.bFGuideGoOn.isEnabled = isChecked
        }

        binding.bFGuideGoOn.setOnClickListener {
            if (binding.cbFGuideAccept.isChecked) {
                startActivity<LoginActivity>()
                viewModel.setIsFirstLaunch(false)
            }
        }

        binding.bFGuideDisclaimers.setOnClickListener {
            MaterialAlertDialogBuilder(requireContext())
                .setIcon(R.drawable.ic_contract)
                .setTitle(R.string.disclaimers)
                .setMessage(R.string.disclaimers_message)
                .setPositiveButton(R.string.ok) { _, _ -> }
                .show()
        }

        binding.bFGuideUsingHelp.setOnClickListener {
            openUrl("https://github.com/15dd/wenku8reader/wiki")
        }
    }
}