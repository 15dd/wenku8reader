package com.cyh128.wenku8reader.util

import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import com.alibaba.fastjson2.JSON
import com.cyh128.wenku8reader.R
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.IOException
import java.nio.charset.StandardCharsets

object CheckUpdate {
    @Throws(IOException::class)
    fun checkUpdate(context: Context, mode: Mode) {
        val gitVersion = githubLatestReleaseVersion
        val thisVersion = getVersion(context)
        if (gitVersion != thisVersion) {
            Handler(Looper.getMainLooper()).post {
                MaterialAlertDialogBuilder(context)
                    .setIcon(R.drawable.new_update)
                    .setTitle("有新版本")
                    .setMessage("强烈建议您下载最新版本\n您可以前往Github下载最新版本")
                    .setCancelable(false)
                    .setNegativeButton("不更新", null)
                    .setPositiveButton("前往Github下载更新") { dialog: DialogInterface?, which: Int ->
                        val uri = Uri.parse("https://github.com/15dd/wenku8reader/releases")
                        val intent = Intent(Intent.ACTION_VIEW, uri)
                        context.startActivity(intent)
                    }
                    .show()
            }
        } else {
            if (mode == Mode.WITH_TIP) {
                Handler(Looper.getMainLooper()).post {
                    Toast.makeText(
                        context,
                        "已经是最新版本",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    @get:Throws(IOException::class)
    private val githubLatestReleaseVersion: String?
        get() {
            val okHttpClient: OkHttpClient = OkHttpClient.Builder().build()
            val request: Request = Request.Builder()
                .url("https://api.github.com/repos/15dd/wenku8reader/releases/latest")
                .get()
                .build()
            val response = okHttpClient.newCall(request).execute()
            val info = String(response.body.bytes(), StandardCharsets.UTF_8)
            if (info.trim { it <= ' ' }.isNotEmpty()) {
                val map = JSON.parse(info) as Map<*, *>
                var ver = map["name"].toString()
                ver = ver.substring(8)
                return ver
            }
            return null
        }

    private fun getVersion(context: Context): String? {
        val manager = context.packageManager
        var name: String? = null
        try {
            val info = manager.getPackageInfo(context.packageName, 0)
            name = info.versionName
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
        }
        return name
    }

    //检查是否有更新
    enum class Mode {
        WITH_TIP,
        WITHOUT_TIP
    }
}
