package com.cyh128.wenku8reader.activity

import android.app.UiModeManager
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.cyh128.wenku8reader.R
import com.github.chrisbanes.photoview.PhotoView
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class PhotoViewActivity : AppCompatActivity() {
    private lateinit var photoView: PhotoView
    private val options: RequestOptions = RequestOptions()
        .skipMemoryCache(true)
        .diskCacheStrategy(DiskCacheStrategy.ALL)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_photo_view)
        photoView = findViewById(R.id.photoview_act_photo_view)
        val toolbar: Toolbar = findViewById(R.id.toolbar_act_photo_view)
        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener { v: View? ->
            // 退出当前页面
            finish()
        }
        val isNigntMode: Boolean?
        val uiModeManager: UiModeManager = getSystemService(UI_MODE_SERVICE) as UiModeManager
        isNigntMode = uiModeManager.nightMode == UiModeManager.MODE_NIGHT_YES
        val intent: Intent = intent
        val url: String? = intent.getStringExtra("url")
        try {
            if (isNigntMode) {
                Glide.with(this@PhotoViewActivity).load(url)
                    .placeholder(R.drawable.image_loading_small_night).apply(options)
                    .into(photoView)
            } else {
                Glide.with(this@PhotoViewActivity).load(url)
                    .placeholder(R.drawable.image_loading_small_day).apply(options).into(photoView)
            }
        } catch (e: Exception) {
            runOnUiThread {
                MaterialAlertDialogBuilder(this@PhotoViewActivity)
                    .setTitle("网络超时")
                    .setMessage("连接超时，可能是服务器出错了或者您正在连接VPN或代理服务器，请稍后再试")
                    .setIcon(R.drawable.timeout)
                    .setCancelable(false)
                    .setPositiveButton(
                        "明白"
                    ) { dialog: DialogInterface?, which: Int -> finish() }
                    .show()
            }
        }
    }
}
