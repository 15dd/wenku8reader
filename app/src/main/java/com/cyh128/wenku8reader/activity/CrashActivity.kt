package com.cyh128.wenku8reader.activity

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.cyh128.wenku8reader.R
import com.developer.crashx.CrashActivity

class CrashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_crash)
        val details = findViewById<TextView>(R.id.errordetails)
        details.text = CrashActivity.getStackTraceFromIntent(intent)
        val report = findViewById<Button>(R.id.button_act_crash_report)
        report.setOnClickListener { v: View? ->
            val uri = Uri.parse("https://github.com/15dd/wenku8reader/issues") //设置跳转的网站
            val intent = Intent(Intent.ACTION_VIEW, uri)
            startActivity(intent)
        }
        val restart = findViewById<Button>(R.id.button_act_crash_restart)
        restart.setOnClickListener { v: View? ->
            val intent = packageManager.getLaunchIntentForPackage(packageName)
            intent!!.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            startActivity(intent)
            finish()
        }
    }
}
