package com.cyh128.hikari_novel.ui.view.main.more

import android.os.Bundle
import android.view.View
import com.cyh128.hikari_novel.base.BaseFragment
import com.cyh128.hikari_novel.databinding.FragmentMoreBinding
import com.cyh128.hikari_novel.ui.view.main.more.more.about.AboutActivity
import com.cyh128.hikari_novel.ui.view.main.more.more.account.AccountActivity
import com.cyh128.hikari_novel.ui.view.main.more.more.setting.SettingActivity
import com.cyh128.hikari_novel.util.startActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MoreFragment: BaseFragment<FragmentMoreBinding>() {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.llFMoreAccount.setOnClickListener {
            startActivity<AccountActivity>()
        }
        binding.llFMoreSetting.setOnClickListener {
            startActivity<SettingActivity>()
        }
        binding.llFMoreAbout.setOnClickListener {
            startActivity<AboutActivity>()
        }
    }
}