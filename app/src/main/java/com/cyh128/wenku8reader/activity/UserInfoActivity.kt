package com.cyh128.wenku8reader.activity

import android.content.Intent
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.os.Bundle
import android.util.Log
import android.util.TypedValue
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.cyh128.wenku8reader.R
import com.cyh128.wenku8reader.util.Wenku8Spider
import com.google.android.material.appbar.MaterialToolbar
import java.io.IOException

class UserInfoActivity() : AppCompatActivity() {
    private lateinit var avatar: ImageView
    private lateinit var userID: TextView
    private lateinit var userName: TextView
    private lateinit var userLevel: TextView
    private lateinit var email: TextView
    private lateinit var signUpDate: TextView
    private lateinit var contribution: TextView
    private lateinit var experience: TextView
    private lateinit var score: TextView
    private lateinit var maxBookcase: TextView
    private lateinit var maxRecommed: TextView
    private lateinit var logout: Button
    private var userInfo: List<String>? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_userinfo)
        avatar = findViewById(R.id.image_act_userinfo)
        userID = findViewById(R.id.text_act_userinfo_userID)
        userName = findViewById(R.id.text_act_userinfo_userName)
        userLevel = findViewById(R.id.text_act_userinfo_userLevel)
        email = findViewById(R.id.text_act_userinfo_email)
        signUpDate = findViewById(R.id.text_act_userinfo_signUpDate)
        contribution = findViewById(R.id.text_act_userinfo_contribution)
        experience = findViewById(R.id.text_act_userinfo_experience)
        score = findViewById(R.id.text_act_userinfo_score)
        maxBookcase = findViewById(R.id.text_act_userinfo_maxBookcase)
        maxRecommed = findViewById(R.id.text_act_userinfo_maxRecommend)
        logout = findViewById(R.id.button_act_userinfo_logout)

        //设置logout按钮的背景色为红色
        val typedValue: TypedValue = TypedValue()
        theme.resolveAttribute(com.google.android.material.R.attr.colorError, typedValue, true)
        logout.setBackgroundColor(typedValue.data)
        val toolbar: MaterialToolbar = findViewById(R.id.toolbar_act_userinfo)
        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener { v: View? ->
            // 退出当前页面
            finish()
        }
        logout.setOnClickListener { v: View? ->
            val db: SQLiteDatabase = openOrCreateDatabase("info.db", MODE_PRIVATE, null)
            val cursor: Cursor = db.query("user_info", null, null, null, null, null, null)
            if (cursor.moveToNext()) {
                db.execSQL("DROP TABLE user_info")
                cursor.close()
            } else {
                Log.d("debug", "数据库没有table")
            }
            val intent: Intent = Intent(this, LoginInputActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
        }
        Thread {
            try {
                userInfo = Wenku8Spider.userInfo
                setData()
            } catch (e: IOException) {
                e.printStackTrace()
                throw RuntimeException(e)
            }
        }.start()
    }

    private fun setData() {
        runOnUiThread {
            Glide.with(this@UserInfoActivity).load(userInfo!![0]).into((avatar))
            userID.text = userInfo!![1]
            userName.text = userInfo!![2]
            userLevel.text = userInfo!![3]
            email.text = userInfo!![4]
            signUpDate.text = userInfo!![5]
            contribution.text = userInfo!![6]
            experience.text = userInfo!![7]
            score.text = userInfo!![8]
            maxBookcase.text = userInfo!![9]
            maxRecommed.text = userInfo!![10]
        }
    }
}
