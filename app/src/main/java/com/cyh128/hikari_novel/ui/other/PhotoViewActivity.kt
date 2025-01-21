package com.cyh128.hikari_novel.ui.other

import android.graphics.drawable.Drawable
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import androidx.core.view.WindowCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.cyh128.hikari_novel.base.BaseActivity
import com.cyh128.hikari_novel.databinding.ActivityPhotoViewBinding
import com.cyh128.hikari_novel.util.openUrl

class PhotoViewActivity : BaseActivity<ActivityPhotoViewBinding>() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        fullScreen()
        WindowCompat.setDecorFitsSystemWindows(window, false)

        val url = intent.getStringExtra("url")!!

        binding.bAPhotoView.setOnClickListener { finish() }
        binding.bAPhotoWeb.setOnClickListener {
            openUrl(url)
        }

        Glide.with(binding.pvAPhotoView)
            .load(url)
            .diskCacheStrategy(DiskCacheStrategy.ALL)
            .addListener(object : RequestListener<Drawable> {
                override fun onLoadFailed(
                    e: GlideException?,
                    model: Any?,
                    target: Target<Drawable>,
                    isFirstResource: Boolean
                ): Boolean {
                    return false
                }

                override fun onResourceReady(
                    resource: Drawable,
                    model: Any,
                    target: Target<Drawable>?,
                    dataSource: DataSource,
                    isFirstResource: Boolean
                ): Boolean {
                    binding.cpiAPhotoView.hide()
                    return false
                }

            }).into(binding.pvAPhotoView)
    }

    private fun fullScreen() {
        // 设置状态栏和导航栏为透明
        window.decorView.systemUiVisibility = (
            View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                  or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                  or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                  or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                  or View.SYSTEM_UI_FLAG_FULLSCREEN
                  or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
        )

        window.statusBarColor = android.graphics.Color.TRANSPARENT
        window.navigationBarColor = android.graphics.Color.TRANSPARENT

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            window.attributes.layoutInDisplayCutoutMode = WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES
        }
    }

}