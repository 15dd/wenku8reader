package com.cyh128.hikari_novel.data.source.remote

import com.cyh128.hikari_novel.util.Base64Helper
import okhttp3.ResponseBody
import rxhttp.toAwait
import rxhttp.wrapper.coroutines.CallAwait
import rxhttp.wrapper.entity.OkResponse
import rxhttp.wrapper.param.RxHttp
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class Network @Inject constructor() {
    fun login(url: String, username: String, password: String): CallAwait<OkResponse<ResponseBody?>> =
        RxHttp.postForm(url)
            .add("username", username)
            .add("password", password)
            .add("action", "login")
            .addHeader("User-Agent","Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/130.0.6723.70 Safari/537.36")
            .toAwait<ResponseBody>()
            .toAwaitOkResponse()

    fun getData(url: String): CallAwait<OkResponse<ResponseBody?>> =
        RxHttp.get(url)
            .addHeader("User-Agent","Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/130.0.6723.70 Safari/537.36")
            .toAwait<ResponseBody>().toAwaitOkResponse()

    fun getDataFromAppWenku8Com(request: String): CallAwait<String> {
        //val request = "action=book&do=text&aid=2906&cid=117212&t=0"
        return RxHttp
            .postForm("http://app.wenku8.com/android.php")
            .add("appver", "1.18")
            .add("request", Base64Helper.encodeBase64(request))
            .add("timetoken", "${System.currentTimeMillis()}")
            .addHeader("User-Agent","Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/130.0.6723.70 Safari/537.36")
            .toAwait<String>()
    }
}