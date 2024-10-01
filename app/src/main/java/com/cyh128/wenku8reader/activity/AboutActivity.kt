package com.cyh128.wenku8reader.activity

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import com.cyh128.wenku8reader.R
import com.cyh128.wenku8reader.util.CheckUpdate
import com.google.android.material.appbar.MaterialToolbar

class AboutActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_about)
        val checkUpdate = findViewById<CardView>(R.id.cardView_act_about_checkUpdate)
        val goToGithub = findViewById<CardView>(R.id.cardView_act_about_goToGithub)
        val version = findViewById<TextView>(R.id.text_act_about_version)
        val toolbar = findViewById<MaterialToolbar>(R.id.toolbar_act_about)
        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener { v: View? ->
            // 退出当前页面
            finish()
        }
        try {
            val packageInfo = applicationContext
                .packageManager
                .getPackageInfo(packageName, 0)
            version.text = packageInfo.versionName
        } catch (e: Exception) {
            version.visibility = View.INVISIBLE
        }
        checkUpdate.setOnClickListener { v: View? ->
            Toast.makeText(this@AboutActivity, "正在检查更新", Toast.LENGTH_SHORT).show()
            Thread {
                try {
                    CheckUpdate.checkUpdate(this@AboutActivity, CheckUpdate.Mode.WITH_TIP)
                } catch (e: Exception) {
                    e.printStackTrace()
                    runOnUiThread {
                        Toast.makeText(
                            this@AboutActivity,
                            "检查更新失败",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }.start()
        }
        goToGithub.setOnClickListener { v: View? ->
            val uri = Uri.parse("https://github.com/15dd/wenku8reader") //设置跳转的网站
            val intent = Intent(Intent.ACTION_VIEW, uri)
            startActivity(intent)
        }
    }
}
