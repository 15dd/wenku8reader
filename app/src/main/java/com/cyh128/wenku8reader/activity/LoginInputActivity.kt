package com.cyh128.wenku8reader.activity

import android.content.ContentValues
import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.CheckBox
import android.widget.CompoundButton
import androidx.appcompat.app.AppCompatActivity
import com.cyh128.wenku8reader.R
import com.cyh128.wenku8reader.util.GlobalConfig
import com.cyh128.wenku8reader.util.LoginWenku8
import com.google.android.material.button.MaterialButton
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout

class LoginInputActivity : AppCompatActivity() {
    private var isRemember: Boolean = false
    private lateinit var usernameLayout: TextInputLayout
    private lateinit var passwordLayout: TextInputLayout
    private lateinit var username: TextInputEditText
    private lateinit var password: TextInputEditText
    private lateinit var signIn: MaterialButton
    private lateinit var str_username: String
    private lateinit var str_password: String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login_input)
        //        以下两行会导致内存泄漏，待改进
//        CircularProgressIndicatorSpec spec = new CircularProgressIndicatorSpec(this, null, 0, com.google.android.material.R.style.Widget_Material3_CircularProgressIndicator_ExtraSmall);
//        Drawable drawable = IndeterminateDrawable.createCircularDrawable(this, spec);
        signIn = findViewById(R.id.confirm_login)
        signIn.setOnClickListener {
            signIn.isClickable = false
            //            signIn.setIcon(drawable);
            //获取输入的字符，判断是否正确
            str_username = username.text.toString()
            str_password = password.text.toString()
            if (str_username.trim { it <= ' ' }.isEmpty() || str_password.trim { it <= ' ' }.isEmpty()
            ) { //判断输入的内容是否为空
                usernameLayout.error = "用户名和密码不能为空"
                passwordLayout.error = "用户名和密码不能为空"
                signIn.isClickable = true
                signIn.icon = null
                return@setOnClickListener
            }

            //尝试登录
            Thread {
                try {
                    val flag: Boolean = LoginWenku8.login(str_username, str_password)
                    if (flag) {
                        val toMainAppUI =
                            Intent(this@LoginInputActivity, AppActivity::class.java)
                        startActivity(toMainAppUI)
                        if (isRemember) {
                            //当设置了保存用户名和密码时，将其放入数据库中
                            GlobalConfig.db.execSQL("CREATE TABLE IF NOT EXISTS user_info(_id INTEGER PRIMARY KEY autoincrement,username TEXT,password TEXT)")
                            val values = ContentValues()
                            values.put("username", str_username)
                            values.put("password", str_password)
                            GlobalConfig.db.insert("user_info", null, values)
                        }
                        Log.e("tag", "finish")
                        finish()
                    } else {
                        runOnUiThread {
                            signIn.isClickable = true
                            signIn.icon = null
                            usernameLayout.error = "用户名或密码错误"
                            passwordLayout.error = "用户名或密码错误"
                        }
                    }
                } catch (e: Exception) {
                    runOnUiThread {
                        signIn.isClickable = true
                        signIn.icon = null
                        MaterialAlertDialogBuilder(this@LoginInputActivity)
                            .setCancelable(false) //禁止点击其他区域
                            .setTitle("网络错误")
                            .setMessage("可能是以下原因造成的:\n\n1 -> 请检查是否正在连接VPN或代理服务器\n2 -> 未连接上网络\n3 -> 服务器(wenku8.cc)出错，(此网站有时会登不上去)\n\n请稍后再试")
                            .setPositiveButton("明白", null)
                            .setIcon(R.drawable.warning)
                            .show()
                    }
                }
            }.start()
        }
        val ckb: CheckBox = findViewById(R.id.rememberMe)
        ckb.setOnCheckedChangeListener { _: CompoundButton?, isChecked: Boolean ->
            isRemember = isChecked
        } //是否保存用户名和密码

        usernameLayout = findViewById(R.id.textfield_username)
        passwordLayout = findViewById(R.id.textfield_password)
        username = findViewById(R.id.username)
        password = findViewById(R.id.password)
        initTextFieldListener() //负责清空错误信息
    }

    private fun initTextFieldListener() { //负责清空错误信息
        username.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(
                s: CharSequence,
                start: Int,
                count: Int,
                after: Int
            ) {
                usernameLayout.error = null
                passwordLayout.error = null
            }

            override fun onTextChanged(
                s: CharSequence,
                start: Int,
                before: Int,
                count: Int
            ) {
            }

            override fun afterTextChanged(s: Editable) {}
        })
        password.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(
                s: CharSequence,
                start: Int,
                count: Int,
                after: Int
            ) {
                usernameLayout.error = null
                passwordLayout.error = null
            }

            override fun onTextChanged(
                s: CharSequence,
                start: Int,
                before: Int,
                count: Int
            ) {
            }

            override fun afterTextChanged(s: Editable) {}
        })
    }
}
