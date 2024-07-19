package com.cyh128.wenku8reader.util

import okhttp3.FormBody
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import org.jsoup.Jsoup
import java.io.IOException

object LoginWenku8 {
    private const val ua =
        "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/112.0.0.0 Safari/537.36 Edg/112.0.1722.68"
    private val okHttpClient: OkHttpClient = OkHttpClient.Builder().build()
    private var cookie = ""

    @JvmStatic
    @Throws(IOException::class)
    fun getPageHtml(url: String): String {
        val request: Request = Request.Builder()
            .url(url)
            .addHeader("cookie", cookie)
            .get()
            .build()
        val response = okHttpClient.newCall(request).execute()
        return String(response.body.bytes(), charset("gbk"))
    }

    @Throws(IOException::class)
    fun login(username: String, password: String): Boolean {
        val paramsMap = HashMap<String, String>()
        paramsMap["username"] = username
        paramsMap["password"] = password
        paramsMap["action"] = "login"
        paramsMap["Content-Type"] = "application/x-www-form-urlencoded"
        paramsMap["User-Agent"] = ua
        paramsMap["X-Requested-With"] = "XMLHttpRequest"
        val builder = FormBody.Builder()
        for (key in paramsMap.keys) {
            //追加表单信息
            builder.add(key, paramsMap[key]!!)
        }
        val formBody: RequestBody = builder.build()
        val request: Request = Request.Builder().url("https://${GlobalConfig.domain}/login.php").post(formBody).build()
        val response = okHttpClient.newCall(request).execute()
        val html = String(response.body.bytes(), charset("gbk"))
        if (!isCorrectUsernameOrPassword(html)) { //判断密码正确
            println("error")
            return false
        }

        //保存获取到的cookie==============================================================================
        val headers = response.headers
        var repeat = false
        val cookies = headers.values("Set-Cookie")
        for (a in cookies) {
            if (a.substring(0, a.indexOf("=")) == "PHPSESSID" && !repeat) { //防止重复PHPSESSID
                repeat = true
                continue
            }
            println(a)
            val temp = a.substring(0, a.indexOf(";"))
            //            System.out.println("截取_之前字符串:"+temp);
            cookie += "$temp;"
        }
        //==============================================================================================
        return true
    }

    private fun isCorrectUsernameOrPassword(html: String): Boolean { //判断密码是否正确
        val document = Jsoup.parse(html)
        val a = document.getElementsByClass("blocktitle")
        val t: String = try {
            a.first()!!.text()
        } catch (e: Exception) {
            return true
        }
        return t != "出现错误！"
    //            Log.e("debug","错误");
    //            Elements b = document.getElementsByClass("blockcontent");
    //            b.first().getElementsByTag("br");
    //            Log.d("debug",b.text().replace(" 请 返 回 并修正 [关闭本窗口]",""));

    }
}
