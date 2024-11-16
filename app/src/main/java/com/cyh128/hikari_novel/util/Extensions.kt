package com.cyh128.hikari_novel.util

import android.content.Intent
import android.content.res.Configuration
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.yariksoffice.lingver.Lingver
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.net.URLEncoder
import java.util.Locale

inline fun <reified T: AppCompatActivity> AppCompatActivity.startActivity(
    configIntent: Intent.() -> Unit = {}
) {
    startActivity(
        Intent(this, T::class.java).apply(configIntent)
    )
}

inline fun <reified T: AppCompatActivity> Fragment.startActivity(
    configIntent: Intent.() -> Unit = {}
) {
    startActivity(
        Intent(requireContext(), T::class.java).apply(configIntent)
    )
}

fun AppCompatActivity.openUrl(url: String) {
    startActivity(
        Intent(Intent.ACTION_VIEW, Uri.parse(url))
    )
}

fun Fragment.openUrl(url: String) {
    startActivity(
        Intent(Intent.ACTION_VIEW, Uri.parse(url))
    )
}

fun AppCompatActivity.launchWithLifecycle(
    dispatcher: CoroutineDispatcher = Dispatchers.Main,
    function: suspend () -> Unit
) {
    lifecycleScope.launch(dispatcher) {
        repeatOnLifecycle(Lifecycle.State.STARTED) {
            function()
        }
    }
}

fun Fragment.launchWithLifecycle(
    dispatcher: CoroutineDispatcher = Dispatchers.Main,
    function: suspend () -> Unit
) {
    viewLifecycleOwner.lifecycleScope.launch(dispatcher) {
        repeatOnLifecycle(Lifecycle.State.STARTED) {
            function()
        }
    }
}

fun AppCompatActivity.getIsInDarkMode() =
    when (resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) {
        Configuration.UI_MODE_NIGHT_YES -> true
        Configuration.UI_MODE_NIGHT_NO -> false
        else -> false
    }

fun Fragment.getIsInDarkMode() =
    when (resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) {
        Configuration.UI_MODE_NIGHT_YES -> true
        Configuration.UI_MODE_NIGHT_NO -> false
        else -> false
    }

fun String.urlEncode(enc: String? = null): String {
    if (enc != null) return URLEncoder.encode(this, enc)
    return when (Lingver.getInstance().getLocale()) {
        Locale.TRADITIONAL_CHINESE -> URLEncoder.encode(this, "BIG5-HKSCS")
        else -> URLEncoder.encode(this, "GBK")
    }
}