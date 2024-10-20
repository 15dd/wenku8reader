package com.cyh128.hikari_novel.ui.view.main.more.more.setting

import android.content.DialogInterface
import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import com.cyh128.hikari_novel.R
import com.cyh128.hikari_novel.base.BaseActivity
import com.cyh128.hikari_novel.data.model.DefaultTab
import com.cyh128.hikari_novel.data.model.Language
import com.cyh128.hikari_novel.data.model.ReaderOrientation
import com.cyh128.hikari_novel.databinding.ActivitySettingBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SettingActivity : BaseActivity<ActivitySettingBinding>() {
    private val viewModel by lazy { ViewModelProvider(this)[SettingViewModel::class.java] }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setSupportActionBar(binding.tbASetting)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.tbASetting.setNavigationOnClickListener { finish() }

        initView()
        initListener()
    }

    private fun initView() {
        binding.sASettingAutoUpdate.isChecked = viewModel.getIsAutoUpdate()

        if (viewModel.getReaderOrientation() == ReaderOrientation.Vertical) binding.tvASettingReadMode.text = getString(R.string.vertical)
        else binding.tvASettingReadMode.text = getString(R.string.horizontal)

        if (viewModel.getWenku8Node() == "www.wenku8.cc") binding.tvASettingNode.text = "www.wenku8.cc"
        else binding.tvASettingNode.text = "www.wenku8.net"

        when(viewModel.getDefaultTab()) {
            DefaultTab.Home -> binding.tvASettingDefaultTab.text = getString(R.string.home)
            DefaultTab.Bookshelf -> binding.tvASettingDefaultTab.text = getString(R.string.bookshelf)
            DefaultTab.History -> binding.tvASettingDefaultTab.text = getString(R.string.history)
            DefaultTab.More -> binding.tvASettingDefaultTab.text = getString(R.string.more)
        }

        binding.tvASettingLanguage.text = when(viewModel.getLanguage()) {
            Language.FOLLOW_SYSTEM -> getString(R.string.follow_system)
            Language.ZH_CN -> getString(R.string.simplified_chinese)
            Language.ZH_TW -> getString(R.string.traditional_chinese)
        }
    }

    private fun initListener() {
        binding.sASettingAutoUpdate.setOnCheckedChangeListener { _, isChecked ->
            viewModel.setIsAutoUpdate(isChecked)
        }

        binding.llASettingNode.setOnClickListener {
            MaterialAlertDialogBuilder(this)
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
                        0 -> {
                            viewModel.setWenku8Node("www.wenku8.cc")
                            binding.tvASettingNode.text = "www.wenku8.cc"
                        }

                        1 -> {
                            viewModel.setWenku8Node("www.wenku8.net")
                            binding.tvASettingNode.text = "www.wenku8.net"
                        }
                    }
                    dialog.dismiss()
                }
                .show()
        }

        binding.llASettingReadMode.setOnClickListener {
            MaterialAlertDialogBuilder(this)
                .setTitle(R.string.read_mode)
                .setIcon(R.drawable.ic_reader)
                .setNegativeButton(R.string.cancel, null)
                .setSingleChoiceItems(
                    arrayOf(
                        getString(R.string.vertical),
                        getString(R.string.horizontal)
                    ),
                    run {
                        if (viewModel.getReaderOrientation() == ReaderOrientation.Vertical) return@run 0
                        else return@run 1
                    }
                ) { dialog: DialogInterface, which: Int ->
                    when (which) {
                        0 -> {
                            viewModel.setReaderOrientation(ReaderOrientation.Vertical)
                            binding.tvASettingReadMode.text = getString(R.string.vertical)
                        }

                        1 -> {
                            viewModel.setReaderOrientation(ReaderOrientation.Horizontal)
                            binding.tvASettingReadMode.text = getString(R.string.horizontal)
                        }
                    }
                    dialog.dismiss()
                }
                .show()
        }

        binding.llASettingLanguage.setOnClickListener {
            MaterialAlertDialogBuilder(this)
                .setTitle(R.string.language)
                .setIcon(R.drawable.ic_language)
                .setNegativeButton(R.string.cancel, null)
                .setSingleChoiceItems(
                    arrayOf(
                        getString(R.string.follow_system),
                        getString(R.string.simplified_chinese),
                        getString(R.string.traditional_chinese)
                    ),
                    run {
                        return@run when(viewModel.getLanguage()) {
                            Language.FOLLOW_SYSTEM -> 0
                            Language.ZH_CN -> 1
                            Language.ZH_TW -> 2
                        }
                    }
                ) { dialog: DialogInterface, which: Int ->
                    when (which) {
                        0 -> {
                            viewModel.setLanguage(Language.FOLLOW_SYSTEM)
                            binding.tvASettingLanguage.text = getString(R.string.follow_system)
                        }
                        1 -> {
                            viewModel.setLanguage(Language.ZH_CN)
                            binding.tvASettingLanguage.text = getString(R.string.simplified_chinese)
                        }
                        2 -> {
                            viewModel.setLanguage(Language.ZH_TW)
                            binding.tvASettingLanguage.text = getString(R.string.traditional_chinese)
                        }
                    }
                    dialog.dismiss()
                }
                .show()
        }

        binding.llASettingDefaultTab.setOnClickListener {
            MaterialAlertDialogBuilder(this)
                .setTitle(R.string.default_tab)
                .setIcon(R.drawable.ic_page_control)
                .setNegativeButton(R.string.cancel, null)
                .setSingleChoiceItems(
                    arrayOf(
                        getString(R.string.home),
                        getString(R.string.bookshelf),
                        getString(R.string.history),
                        getString(R.string.more)
                    ),
                    run {
                        when(viewModel.getDefaultTab()) {
                            DefaultTab.Home -> return@run 0
                            DefaultTab.Bookshelf -> return@run 1
                            DefaultTab.History -> return@run 2
                            DefaultTab.More -> return@run 3
                        }
                    }
                ) { dialog: DialogInterface, which: Int ->
                    when (which) {
                        0 -> {
                            viewModel.setDefaultTab(DefaultTab.Home)
                            binding.tvASettingDefaultTab.text = getString(R.string.home)
                        }

                        1 -> {
                            viewModel.setDefaultTab(DefaultTab.Bookshelf)
                            binding.tvASettingDefaultTab.text = getString(R.string.bookshelf)
                        }

                        2 -> {
                            viewModel.setDefaultTab(DefaultTab.History)
                            binding.tvASettingDefaultTab.text = getString(R.string.history)
                        }

                        3 -> {
                            viewModel.setDefaultTab(DefaultTab.More)
                            binding.tvASettingDefaultTab.text = getString(R.string.more)
                        }
                    }
                    dialog.dismiss()
                }
                .show()
        }
    }
}