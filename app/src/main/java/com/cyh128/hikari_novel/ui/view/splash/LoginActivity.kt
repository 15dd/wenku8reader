package com.cyh128.hikari_novel.ui.view.splash

import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.core.view.WindowCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.cyh128.hikari_novel.R
import com.cyh128.hikari_novel.base.BaseActivity
import com.cyh128.hikari_novel.data.model.Event
import com.cyh128.hikari_novel.databinding.ActivityLoginBinding
import com.cyh128.hikari_novel.ui.view.main.MainActivity
import com.cyh128.hikari_novel.util.openUrl
import com.cyh128.hikari_novel.util.startActivity
import com.drake.channel.receiveEvent
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.progressindicator.CircularProgressIndicatorSpec
import com.google.android.material.progressindicator.IndeterminateDrawable
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

@AndroidEntryPoint
class LoginActivity : BaseActivity<ActivityLoginBinding>() {
    private val viewModel by lazy { ViewModelProvider(this)[LoginViewModel::class.java] }

    private lateinit var spec: CircularProgressIndicatorSpec
    private lateinit var progressIndicatorDrawable: IndeterminateDrawable<CircularProgressIndicatorSpec>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        WindowCompat.setDecorFitsSystemWindows(window, false)

        spec = CircularProgressIndicatorSpec(this, null, 0)
        progressIndicatorDrawable = IndeterminateDrawable.createCircularDrawable(this, spec)

        if (intent?.getBooleanExtra("isShowTip", false) == true) {
            MaterialAlertDialogBuilder(this)
                .setTitle(R.string.login_failed)
                .setMessage(R.string.login_failed_tip)
                .setCancelable(false)
                .setPositiveButton(R.string.ok) { _, _ -> }
                .show()
        }

        receiveEvent<Event>("event_login_activity") { event ->
            when (event) {
                Event.LogInSuccessEvent -> {
                    viewModel.refreshBookshelfList() //等待书架信息获取完成
                    Toast.makeText(
                        this@LoginActivity,
                        R.string.login_successful,
                        Toast.LENGTH_SHORT
                    ).show()
                    if (binding.cbALogin.isChecked) {
                        viewModel.saveLoginInfo(
                            binding.tietALoginUsername.text.toString(),
                            binding.tietALoginPassword.text.toString()
                        )
                    }
                    startActivity<MainActivity> {
                        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                        putExtra("isLoggedIn", true)
                    }
                }

                Event.LogInFailureEvent -> {
                    isEnableLoginButton(true)
                    clearError()
                }

                is Event.NetworkErrorEvent -> {
                    MaterialAlertDialogBuilder(this@LoginActivity)
                        .setTitle(R.string.network_error)
                        .setIcon(R.drawable.ic_error)
                        .setTitle(R.string.network_error)
                        .setMessage(event.msg)
                        .setPositiveButton(R.string.ok, null)
                        .show()
                    isEnableLoginButton(true)
                }

                else -> {}
            }
        }

        initListener()
    }

    private fun initListener() {
        viewModel.isUsernameCorrect.observe(this) {
            if (!it) binding.tfALoginUsername.error = getString(R.string.username_error)
        }

        viewModel.isPasswordCorrect.observe(this) {
            if (!it) binding.tfALoginPassword.error = getString(R.string.password_error)
        }

        //登录按钮执行
        binding.bALoginLogin.setOnClickListener {
            isEnableLoginButton(false)
            val username = binding.tietALoginUsername.text.toString()
            val password = binding.tietALoginPassword.text.toString()
            if (username.isEmpty()) {
                binding.tfALoginUsername.error = getString(R.string.not_null)
                clearError()
                isEnableLoginButton(true)
                return@setOnClickListener
            } else if (password.isEmpty()) {
                binding.tfALoginPassword.error = getString(R.string.not_null)
                clearError()
                isEnableLoginButton(true)
                return@setOnClickListener
            }
            viewModel.login(username, password)
        }

        //注册功能
        binding.bALoginRegister.setOnClickListener {
            MaterialAlertDialogBuilder(this)
                .setTitle(R.string.register_msg1)
                .setMessage(R.string.register_msg2)
                .setCancelable(false)
                .setNegativeButton(R.string.cancel, null)
                .setPositiveButton(
                    R.string.go_to_register
                ) { _: DialogInterface?, _: Int ->
                    openUrl("https://www.wenku8.cc/register.php")
                }
                .show()
        }

        binding.bALoginDomain.setOnClickListener {
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
                        0 -> viewModel.setWenku8Node("www.wenku8.cc")
                        1 -> viewModel.setWenku8Node("www.wenku8.net")
                    }
                    dialog.dismiss()
                }
                .show()
        }
    }

    //设置登录按钮是否可用，防止多次点击导致重复登录
    //并设置相应样式
    private fun isEnableLoginButton(value: Boolean) {
        binding.bALoginLogin.isEnabled = value
        if (value) {
            binding.bALoginLogin.setIconResource(R.drawable.ic_login)
            binding.bALoginLogin.text = getString(R.string.login)
        } else {
            binding.bALoginLogin.icon = progressIndicatorDrawable
            binding.bALoginLogin.text = getString(R.string.logging)
        }
    }

    //清除文本框错误信息
    private val clearErrorMutex = Mutex()
    private fun clearError() {
        lifecycleScope.launch {
            if (clearErrorMutex.isLocked) return@launch
            clearErrorMutex.withLock {
                delay(2000)
                binding.tfALoginUsername.error = null
                binding.tfALoginPassword.error = null
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        //停止动画，防止内存泄漏
        spec.hideAnimationBehavior
        progressIndicatorDrawable.hideNow()
    }

}