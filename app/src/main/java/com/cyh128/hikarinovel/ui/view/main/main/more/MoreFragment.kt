package com.cyh128.hikarinovel.ui.view.main.main.more

import android.os.Bundle
import android.view.View
import com.cyh128.hikarinovel.base.BaseFragment
import com.cyh128.hikarinovel.databinding.FragmentMoreBinding
import com.cyh128.hikarinovel.ui.view.main.main.more.more.about.AboutActivity
import com.cyh128.hikarinovel.ui.view.main.main.more.more.account.AccountActivity
import com.cyh128.hikarinovel.ui.view.main.main.more.more.setting.SettingActivity
import com.cyh128.hikarinovel.util.startActivity
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