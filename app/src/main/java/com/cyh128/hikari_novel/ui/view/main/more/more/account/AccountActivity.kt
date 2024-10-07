package com.cyh128.hikari_novel.ui.view.main.more.more.account

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.cyh128.hikari_novel.R
import com.cyh128.hikari_novel.base.BaseActivity
import com.cyh128.hikari_novel.data.model.Event
import com.cyh128.hikari_novel.data.model.UserInfo
import com.cyh128.hikari_novel.databinding.ActivityAccountBinding
import com.cyh128.hikari_novel.ui.view.splash.LoginActivity
import com.cyh128.hikari_novel.util.launchWithLifecycle
import com.cyh128.hikari_novel.util.startActivity
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AccountActivity : BaseActivity<ActivityAccountBinding>() {
    private val viewModel by lazy { ViewModelProvider(this)[AccountViewModel::class.java] }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setSupportActionBar(binding.tbAAccount)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.tbAAccount.setNavigationOnClickListener { finish() }

        binding.bAAccount.setOnClickListener {
            viewModel.clearLoginInfo()
            Toast.makeText(this, R.string.logout_successful, Toast.LENGTH_SHORT).show()
            startActivity<LoginActivity> {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
            }
        }

        binding.bAAccountSignIn.setOnClickListener {
            viewModel.signIn()
            binding.bAAccountSignIn.isEnabled = false
        }

        launchWithLifecycle {
            viewModel.eventFlow.collect {
                when(it) {
                    Event.LoadSuccessEvent -> setView(viewModel.userInfo)

                    is Event.NetWorkErrorEvent -> {
                        MaterialAlertDialogBuilder(this@AccountActivity)
                            .setTitle(R.string.network_error)
                            .setIcon(R.drawable.ic_error)
                            .setMessage(it.msg)
                            .setCancelable(false)
                            .setPositiveButton(R.string.ok) { _, _ -> }
                            .show()
                    }

                    Event.SignInSuccessEvent -> {
                        binding.bAAccountSignIn.isEnabled = true
                        viewModel.getUserInfo()
                        MaterialAlertDialogBuilder(this@AccountActivity)
                            .setIcon(R.drawable.ic_event_available)
                            .setTitle(R.string.sign_in_success)
                            .setMessage(R.string.sign_in_success_msg)
                            .setCancelable(false)
                            .setPositiveButton(R.string.ok) { _, _ -> }
                            .show()
                    }

                    Event.SignInFailureEvent -> {
                        binding.bAAccountSignIn.isEnabled = true
                        MaterialAlertDialogBuilder(this@AccountActivity)
                            .setIcon(R.drawable.ic_event_available)
                            .setTitle(R.string.sign_in_failure)
                            .setMessage(R.string.sign_in_failure_msg)
                            .setCancelable(false)
                            .setPositiveButton(R.string.ok) { _, _ -> }
                            .show()
                    }

                    Event.TempSignInUnableEvent -> {
                        binding.bAAccountSignIn.isEnabled = true
                        MaterialAlertDialogBuilder(this@AccountActivity)
                            .setIcon(R.drawable.ic_event_available)
                            .setTitle(R.string.sign_in_failure)
                            .setMessage(R.string.sign_in_failure_msg2)
                            .setCancelable(false)
                            .setPositiveButton(R.string.ok) { _, _ -> }
                            .show()
                    }

                    else -> {}
                }
            }
        }

        viewModel.getUserInfo()
    }

    private fun setView(userInfo: UserInfo) {
        binding.apply {
            Glide.with(ivAAccount)
                .load(userInfo.avatar)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(ivAAccount)
            tvAAccountUserId.text = userInfo.userID
            tvAAccountUsername.text = userInfo.username
            tvAAccountLevel.text = userInfo.userLevel
            tvAAccountEmail.text = userInfo.email
            tvAAccountRegisterDate.text = userInfo.registerDate
            tvAAccountContribution.text = userInfo.contribution
            tvAAccountExperience.text = userInfo.experience
            tvAAccountCurrentPoint.text = userInfo.point
            tvAAccountMaxBookshelfNumber.text = userInfo.maxBookshelfNum
            tvAAccountDailyMaxRecommendNumber.text = userInfo.maxRecommendNum
        }
    }
}