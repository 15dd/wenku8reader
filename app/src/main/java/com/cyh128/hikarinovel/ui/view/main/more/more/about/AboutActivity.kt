package com.cyh128.hikarinovel.ui.view.main.more.more.about

import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import com.cyh128.hikarinovel.R
import com.cyh128.hikarinovel.base.BaseActivity
import com.cyh128.hikarinovel.data.model.Event
import com.cyh128.hikarinovel.databinding.ActivityAboutBinding
import com.cyh128.hikarinovel.util.launchWithLifecycle
import com.cyh128.hikarinovel.util.openUrl
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AboutActivity : BaseActivity<ActivityAboutBinding>() {
    private val viewModel by lazy { ViewModelProvider(this)[AboutViewModel::class.java] }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setSupportActionBar(binding.tbAAbout)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.tbAAbout.setNavigationOnClickListener { finish() }

        launchWithLifecycle {
            viewModel.eventFlow.collect { event ->
                when (event) {
                    Event.HaveAvailableUpdateEvent -> {
                        MaterialAlertDialogBuilder(this@AboutActivity)
                            .setIcon(R.drawable.ic_release_alert)
                            .setTitle(R.string.update_available)
                            .setMessage(R.string.update_available_tip)
                            .setCancelable(false)
                            .setPositiveButton(R.string.go_to_download) { _, _ ->
                                openUrl("https://github.com/15dd/wenku8reader/releases")
                            }
                            .setNegativeButton(R.string.cancel) { _, _ -> }
                            .show()
                        binding.llAAboutUpdate.isClickable = true
                    }

                    Event.NoAvailableUpdateEvent -> {
                        MaterialAlertDialogBuilder(this@AboutActivity)
                            .setMessage(R.string.you_have_used_in_latest_version)
                            .setCancelable(false)
                            .setPositiveButton(R.string.ok) { _, _ -> }
                            .show()
                        binding.llAAboutUpdate.isClickable = true
                    }

                    else -> {}
                }
            }
        }

        binding.tvAAbout.text =
            applicationContext.packageManager.getPackageInfo(packageName, 0).versionName

        binding.llAAboutDisclaimers.setOnClickListener {
            MaterialAlertDialogBuilder(this)
                .setIcon(R.drawable.ic_contract)
                .setTitle(R.string.disclaimers)
                .setMessage(R.string.disclaimers_message)
                .setPositiveButton(R.string.ok) { _, _ -> }
                .show()
        }

        binding.llAAboutUpdate.setOnClickListener {
            binding.llAAboutUpdate.isClickable = false
            viewModel.checkUpdate()
        }

        binding.bAAboutGithub.setOnClickListener {
            openUrl("https://github.com/15dd/wenku8reader")
        }
        binding.bAAboutTelegram.setOnClickListener {
            openUrl("https://t.me/+JH2H3VpET7ozMTU9")
        }

        binding.llAAboutHelp.setOnClickListener {
            openUrl("https://github.com/15dd/wenku8reader/wiki")
        }
    }
}