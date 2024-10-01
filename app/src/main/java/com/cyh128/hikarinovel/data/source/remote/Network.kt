package com.cyh128.hikarinovel.data.source.remote

import com.cyh128.hikarinovel.util.Base64Helper
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
            .toAwait<ResponseBody>()
            .toAwaitOkResponse()

    fun getData(url: String): CallAwait<OkResponse<ResponseBody?>> =
        RxHttp.get(url).toAwait<ResponseBody>().toAwaitOkResponse()

    fun getDataFromAppWenku8Com(request: String): CallAwait<String> {
        //val request = "action=book&do=text&aid=2906&cid=117212&t=0"
        return RxHttp
            .postForm("http://app.wenku8.com/android.php")
            .add("appver", "1.18")
            .add("request", Base64Helper.encodeBase64(request))
            .add("timetoken", "${System.currentTimeMillis()}")
            .toAwait<String>()
    }
}