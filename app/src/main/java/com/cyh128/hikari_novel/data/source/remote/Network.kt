package com.cyh128.hikari_novel.data.source.remote

import com.cyh128.hikari_novel.util.Base64Helper
import okhttp3.ResponseBody
import rxhttp.toAwait
import rxhttp.wrapper.coroutines.CallAwait
import rxhttp.wrapper.entity.KeyValuePair
import rxhttp.wrapper.entity.OkResponse
import rxhttp.wrapper.param.RxHttp
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class Network @Inject constructor() {

    fun login(
        url: String,
        username: String,
        password: String,
        checkcode: String,
        usecookie: String
    ): CallAwait<OkResponse<ResponseBody?>> =
        RxHttp.postForm(url)
            .add("username", username)
            .add("password", password)
            .add("checkcode", checkcode)
            .add("usecookie", usecookie)
            .add("action", "login")
            .addHeader("User-Agent","Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/130.0.6723.70 Safari/537.36")
            .toAwait<ResponseBody>()
            .toAwaitOkResponse()

    fun post(url: String, cookie: String, pairs: List<KeyValuePair>): CallAwait<OkResponse<ResponseBody?>> {
        val rxHttpFormParam = RxHttp.postForm(url)
            .addHeader("Cookie", cookie)
            .addHeader("User-Agent","Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/130.0.6723.70 Safari/537.36")
        pairs.forEach { rxHttpFormParam.add(it.key, it.value) }
        return rxHttpFormParam.toAwait<ResponseBody>().toAwaitOkResponse()
    }

    fun get(url: String, cookie: String): CallAwait<OkResponse<ResponseBody?>> =
        RxHttp.get(url)
            .addHeader("Cookie", cookie)
            .addHeader("User-Agent","Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/130.0.6723.70 Safari/537.36")
            .toAwait<ResponseBody>()
            .toAwaitOkResponse()

    fun getWithoutCookie(url: String): CallAwait<OkResponse<ResponseBody?>> =
        RxHttp.get(url)
            .addHeader("User-Agent","Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/130.0.6723.70 Safari/537.36")
            .toAwait<ResponseBody>()
            .toAwaitOkResponse()

    fun getFromAppWenku8Com(request: String): CallAwait<String> {
        //val request = "action=book&do=text&aid=2906&cid=117212&t=0"
        return RxHttp
            .postForm("http://app.wenku8.com/android.php")
            .addHeader("User-Agent","Dalvik/2.1.0 (Linux; U; Android 15; 23114RD76B Build/AQ3A.240912.001)")
            .add("appver", "1.21")
            .add("request", Base64Helper.encodeBase64(request))
            .add("timetoken", "${System.currentTimeMillis()}")
            .toAwait<String>()
    }
}