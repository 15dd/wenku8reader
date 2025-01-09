package com.cyh128.hikari_novel.base

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import androidx.appcompat.app.AppCompatActivity
import androidx.viewbinding.ViewBinding
import com.cyh128.hikari_novel.data.model.Event
import com.cyh128.hikari_novel.util.LanguageHelper
import com.cyh128.hikari_novel.util.ThemeHelper
import com.drake.channel.receiveEvent
import java.lang.reflect.ParameterizedType

abstract class BaseActivity<VB : ViewBinding> : AppCompatActivity() {
    lateinit var binding: VB

    override fun onCreate(savedInstanceState: Bundle?) {
        LanguageHelper.initLanguage()
        ThemeHelper.initActivityThemeAndDarkMode(this)

        receiveEvent<Event>("event_theme_changed", "event_language_changed") {
            recreateActivity()
        }

        super.onCreate(savedInstanceState)

        val type = javaClass.genericSuperclass as ParameterizedType
        val aClass = type.actualTypeArguments[0] as Class<*>
        val method = aClass.getDeclaredMethod("inflate", LayoutInflater::class.java)
        @Suppress("UNCHECKED_CAST")
        binding = method.invoke(null, layoutInflater) as VB
        setContentView(binding.root)
    }

    private fun recreateActivity() {
        val intent = Intent(this, this::class.java)
        startActivity(intent)
        finish()
    }

}