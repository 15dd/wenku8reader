package com.cyh128.hikari_novel.data.model

//网络错误异常
class NetworkException(message: String?): Exception(message)

//在五秒限制内异常
class InFiveSecondException: Exception()

//已签到异常
class SignedInException: Exception()

//临时登录签到异常
class TempSignInException: Exception()