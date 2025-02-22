package com.cyh128.hikari_novel.data.repository

import android.util.Log
import com.cyh128.hikari_novel.data.model.Bookshelf
import com.cyh128.hikari_novel.data.model.BookshelfNovelInfo
import com.cyh128.hikari_novel.data.model.ChapterContentResponse
import com.cyh128.hikari_novel.data.model.CommentResponse
import com.cyh128.hikari_novel.data.model.HomeBlock
import com.cyh128.hikari_novel.data.model.InFiveSecondException
import com.cyh128.hikari_novel.data.model.LoginResponse
import com.cyh128.hikari_novel.data.model.NetworkException
import com.cyh128.hikari_novel.data.model.NovelCoverResponse
import com.cyh128.hikari_novel.data.model.NovelInfo
import com.cyh128.hikari_novel.data.model.ReplyResponse
import com.cyh128.hikari_novel.data.model.SignedInException
import com.cyh128.hikari_novel.data.model.TempSignInException
import com.cyh128.hikari_novel.data.model.UserInfo
import com.cyh128.hikari_novel.data.model.Volume
import com.cyh128.hikari_novel.data.source.local.mmkv.AppConfig
import com.cyh128.hikari_novel.data.source.local.mmkv.LoginInfo
import com.cyh128.hikari_novel.data.source.remote.Network
import com.cyh128.hikari_novel.util.HttpCodeParser
import com.cyh128.hikari_novel.util.Wenku8Parser
import com.cyh128.hikari_novel.util.urlEncode
import rxhttp.awaitResult
import rxhttp.wrapper.entity.KeyValuePair
import java.nio.charset.Charset
import java.util.Locale
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class Wenku8Repository @Inject constructor(
    private val appConfig: AppConfig,
    private val network: Network,
    private val loginInfo: LoginInfo
) {
    var username: String?
        get() = loginInfo.username
        set(value) {
            loginInfo.username = value
        }
    var password: String?
        get() = loginInfo.password
        set(value) {
            loginInfo.password = value
        }
    var cookie: String?
        get() = loginInfo.cookie
        set(value) {
            loginInfo.cookie = value
        }
    var expDate: Long?
        get() = loginInfo.expDate
        set(value) {
            loginInfo.expDate = value
        }

    //登录
    suspend fun login(
        username: String = this.username!!,
        password: String = this.password!!,
        checkcode: String,
        usecookie: String
    ): Result<LoginResponse> {
        network.login("https://${getWenku8Node()}/login.php", username, password, checkcode, usecookie)
            .awaitResult { body ->
                if (body.code() != 200) {
                    //如果code不是200，就说明网络有错误
                    return Result.failure(NetworkException(HttpCodeParser.parser(body.code())))
                } else {
                    val html = String(body.body()!!.bytes(), Charset.forName("GBK"))
                    Log.d("w_8_r",html)
                    val response = LoginResponse.empty()
                    Wenku8Parser.isLoginInfoCorrect(html, response)
                    Wenku8Parser.isLoginSuccessful(html, response)
                    return Result.success(response)
                }
            }.onFailure {
                return Result.failure(NetworkException(it.message))
            }
        throw RuntimeException()
    }

    //根据排名获取小说列表
    suspend fun getNovelByRanking(ranking: String, index: Int): Result<NovelCoverResponse> {
        val requestUrl: String?
        val charset: String?
        when(Locale.getDefault()) {
            Locale.TRADITIONAL_CHINESE -> {
                requestUrl = "https://${getWenku8Node()}/modules/article/toplist.php?sort=$ranking&page=$index&charset=big5"
                charset = "BIG5-HKSCS"
            }
            else -> {
                requestUrl = "https://${getWenku8Node()}/modules/article/toplist.php?sort=$ranking&page=$index&charset=gbk"
                charset = "GBK"
            }
        }
        network.get(requestUrl, cookie!!)
            .awaitResult { body ->
                val html = String(body.body()!!.bytes(), Charset.forName(charset))
                val list = Wenku8Parser.parseToList(html, getWenku8Node())
                val maxNum = Wenku8Parser.getMaxNum(html)
                return Result.success(NovelCoverResponse(maxNum, list))
            }.onFailure {
                return Result.failure(NetworkException(it.message))
            }
        throw RuntimeException()
    }

    //根据分类获取小说列表
    suspend fun getNovelByCategory(
        category: String,
        sort: String,
        index: Int
    ): Result<NovelCoverResponse> {
        val requestUrl: String?
        val charset: String?
        when(Locale.getDefault()) {
            Locale.TRADITIONAL_CHINESE -> {
                requestUrl = "https://${getWenku8Node()}/modules/article/tags.php?t=$category&v=$sort&page=$index&charset=big5"
                charset = "BIG5-HKSCS"
            }
            else -> {
                requestUrl = "https://${getWenku8Node()}/modules/article/tags.php?t=$category&v=$sort&page=$index&charset=gbk"
                charset = "GBK"
            }
        }
        network.get(requestUrl, cookie!!)
            .awaitResult { body ->
                val html = String(body.body()!!.bytes(), Charset.forName(charset))
                val list = Wenku8Parser.parseToList(html, getWenku8Node())
                val maxNum = Wenku8Parser.getMaxNum(html)
                return Result.success(NovelCoverResponse(maxNum, list))
            }.onFailure {
                return Result.failure(NetworkException(it.message))
            }
        throw RuntimeException()
    }

    //获取小说信息
    suspend fun getNovelInfo(url: String): Result<NovelInfo> {
        val requestUrl: String?
        val charset: String?
        when(Locale.getDefault()) {
            Locale.TRADITIONAL_CHINESE -> {
                requestUrl = "$url&charset=big5"
                charset = "BIG5-HKSCS" //不能使用普通的big5编码，不然无法显示日文
            }
            else -> {
                requestUrl = "$url&charset=gbk"
                charset = "GBK"
            }
        }
        network.get(requestUrl, cookie!!)
            .awaitResult { body ->
                //val html = String(body.body()!!.bytes(), Charset.forName(charset))
                val html = String(body.body()!!.bytes(), Charset.forName(charset))
                val result = Wenku8Parser.getNovelInfo(html)
                return Result.success(result)
            }.onFailure {
                return Result.failure(NetworkException(it.message))
            }
        throw RuntimeException()
    }

    //获取章节
    suspend fun getChapter(url: String): Result<List<Volume>> {
        val requestUrl: String?
        val charset: String?
        when(Locale.getDefault()) {
            Locale.TRADITIONAL_CHINESE -> {
                requestUrl = "${url}?charset=big5"
                charset = "BIG5-HKSCS"
            }
            else -> {
                requestUrl = "${url}?charset=gbk"
                charset = "GBK"
            }
        }
        network.get(requestUrl, cookie!!)
            .awaitResult { body ->
                val html = String(body.body()!!.bytes(), Charset.forName(charset))
                val result = Wenku8Parser.getChapter(html)
                return Result.success(result)
            }.onFailure {
                return Result.failure(NetworkException(it.message))
            }
        throw RuntimeException()
    }

    //加入书库
    suspend fun addNovel(aid: String): Result<Boolean> {
        val requestUrl: String?
        val charset: String?
        when(Locale.getDefault()) {
            Locale.TRADITIONAL_CHINESE -> {
                requestUrl = "https://${getWenku8Node()}/modules/article/addbookcase.php?bid=$aid&charset=big5"
                charset = "BIG5-HKSCS"
            }
            else -> {
                requestUrl = "https://${getWenku8Node()}/modules/article/addbookcase.php?bid=$aid&charset=gbk"
                charset = "GBK"
            }
        }
        network.get(requestUrl, cookie!!)
            .awaitResult { body ->
                val html = String(body.body()!!.bytes(), Charset.forName(charset))
                val result = Wenku8Parser.addNovel(html)
                return if (result) Result.success(true) else Result.success(false)
            }.onFailure {
                return Result.failure(NetworkException(it.message))
            }
        throw RuntimeException()
    }

    //移出书库
    suspend fun removeNovel(bid: String): Result<Any?> {
        network.get("https://${getWenku8Node()}/modules/article/bookcase.php?delid=$bid", cookie!!)
            .awaitResult {
                return Result.success(null)
            }.onFailure {
                return Result.failure(NetworkException(it.message))
            }
        throw RuntimeException()
    }

    //从列表移出书库
    suspend fun removeNovelFromList(list: List<String>, classId: Int): Result<Any?> {
        val pairs = mutableListOf<KeyValuePair>()
        list.forEach { pairs.add(KeyValuePair("checkid[]", it, false)) }
        pairs.add(KeyValuePair("classlist", classId, false))
        pairs.add(KeyValuePair("checkall","checkall", false))
        pairs.add(KeyValuePair("newclassid", -1, false))
        pairs.add(KeyValuePair("classid", classId, false))

        network.post("https://${getWenku8Node()}/modules/article/bookcase.php", cookie!!, pairs)
            .awaitResult {
                return Result.success(null)
            }.onFailure {
                return Result.failure(NetworkException(it.message))
            }
        throw RuntimeException()
    }

    //移动到其它书架
    suspend fun moveNovelToOther(list: List<String>, classId: Int, newClassId: Int): Result<Any?> {
        val pairs = mutableListOf<KeyValuePair>()
        list.forEach { pairs.add(KeyValuePair("checkid[]", it, false)) }
        pairs.add(KeyValuePair("classlist", classId, false))
        pairs.add(KeyValuePair("checkall","checkall", false))
        pairs.add(KeyValuePair("newclassid", newClassId, false))
        pairs.add(KeyValuePair("classid", classId, false))

        network.post("https://${getWenku8Node()}/modules/article/bookcase.php", cookie!!, pairs)
            .awaitResult {
                return Result.success(null)
            }.onFailure {
                return Result.failure(NetworkException(it.message))
            }
        throw RuntimeException()
    }

    //获取书架
    suspend fun getBookshelf(classId: Int = 0): Result<Bookshelf> {
        val requestUrl: String?
        val charset: String?
        when(Locale.getDefault()) {
            Locale.TRADITIONAL_CHINESE -> {
                requestUrl = "https://${getWenku8Node()}/modules/article/bookcase.php?classid=$classId&charset=big5"
                charset = "BIG5-HKSCS"
            }
            else -> {
                requestUrl = "https://${getWenku8Node()}/modules/article/bookcase.php?classid=$classId&charset=gbk"
                charset = "GBK"
            }
        }
        network.get(requestUrl, cookie!!)
            .awaitResult { body ->
                val html = String(body.body()!!.bytes(), Charset.forName(charset))
                val res = Wenku8Parser.getBookshelf(html)
                return Result.success(res)
            }.onFailure {
                return Result.failure(NetworkException(it.message))
            }
        throw RuntimeException()
    }

    //获取评论
    suspend fun getComment(aid: String, index: Int): Result<CommentResponse> {
        val requestUrl: String?
        val charset: String?
        when(Locale.getDefault()) {
            Locale.TRADITIONAL_CHINESE -> {
                requestUrl = "https://${getWenku8Node()}/modules/article/reviews.php?aid=$aid&page=$index&charset=big5"
                charset = "BIG5-HKSCS"
            }
            else -> {
                requestUrl = "https://${getWenku8Node()}/modules/article/reviews.php?aid=$aid&page=$index&charset=gbk"
                charset = "GBK"
            }
        }
        network.get(requestUrl, cookie!!)
            .awaitResult { body ->
                val html = String(body.body()!!.bytes(), Charset.forName(charset))
                val result = Wenku8Parser.getComment(html)
                return Result.success(result)
            }.onFailure {
                return Result.failure(NetworkException(it.message))
            }
        throw RuntimeException()
    }

    //获取回复
    suspend fun getReply(url: String, index: Int): Result<ReplyResponse> {
        val requestUrl: String?
        val charset: String?
        when(Locale.getDefault()) {
            Locale.TRADITIONAL_CHINESE -> {
                requestUrl = "$url&page=$index&charset=big5"
                charset = "BIG5-HKSCS"
            }
            else -> {
                requestUrl = "$url&page=$index&charset=gbk"
                charset = "GBK"
            }
        }
        network.get(requestUrl, cookie!!)
            .awaitResult { body ->
                val html = String(body.body()!!.bytes(), Charset.forName(charset))
                val result = Wenku8Parser.getReply(html)
                return Result.success(result)
            }.onFailure {
                return Result.failure(NetworkException(it.message))
            }
        throw RuntimeException()
    }

    suspend fun getRecommend(): Result<List<HomeBlock>> {
        val requestUrl: String?
        val charset: String?
        when(Locale.getDefault()) {
            Locale.TRADITIONAL_CHINESE -> {
                requestUrl = "https://${getWenku8Node()}/index.php?charset=big5"
                charset = "BIG5-HKSCS"
            }
            else -> {
                requestUrl = "https://${getWenku8Node()}/index.php?charset=gbk"
                charset = "GBK"
            }
        }
        network.get(requestUrl, cookie!!)
            .awaitResult { body ->
                val html = String(body.body()!!.bytes(), Charset.forName(charset))
                val result = Wenku8Parser.getRecommend(html)
                return Result.success(result)
            }.onFailure {
                return Result.failure(NetworkException(it.message))
            }
        throw RuntimeException()
    }

    //为小说投票
    suspend fun novelVote(aid: String): Result<String> {
        val requestUrl: String?
        val charset: String?
        when(Locale.getDefault()) {
            Locale.TRADITIONAL_CHINESE -> {
                requestUrl = "https://${getWenku8Node()}/modules/article/uservote.php?id=$aid&charset=big5"
                charset = "BIG5-HKSCS"
            }
            else -> {
                requestUrl = "https://${getWenku8Node()}/modules/article/uservote.php?id=$aid&charset=gbk"
                charset = "GBK"
            }
        }
        network.get(requestUrl, cookie!!)
            .awaitResult { body ->
                val html = String(body.body()!!.bytes(), Charset.forName(charset))
                val result = Wenku8Parser.novelVote(html)
                return Result.success(result)
            }.onFailure {
                return Result.failure(NetworkException(it.message))
            }
        throw RuntimeException()
    }

    //根据标题搜索小说
    suspend fun searchNovelByTitle(title: String, index: Int): Result<NovelCoverResponse> {
        val requestUrl: String?
        val charset: String?
        when(Locale.getDefault()) {
            Locale.TRADITIONAL_CHINESE -> {
                requestUrl = "https://${getWenku8Node()}/modules/article/search.php?searchtype=articlename&searchkey=$title&page=$index&charset=big5"
                charset = "BIG5-HKSCS"
            }
            else -> {
                requestUrl = "https://${getWenku8Node()}/modules/article/search.php?searchtype=articlename&searchkey=$title&page=$index&charset=gbk"
                charset = "GBK"
            }
        }
        network.get(requestUrl, cookie!!)
            .awaitResult { body ->
                val html = String(body.body()!!.bytes(), Charset.forName(charset))
                if (Wenku8Parser.isInFiveSecond(html)) {
                    return Result.failure(InFiveSecondException())
                }
                val isOnlyOne = Wenku8Parser.isSearchResultOnlyOne(html, getWenku8Node())
                if (isOnlyOne != null) {
                    return Result.success(NovelCoverResponse(1, listOf(isOnlyOne)))
                }
                val result = Wenku8Parser.parseToList(html, getWenku8Node())
                return Result.success(NovelCoverResponse(Wenku8Parser.getMaxNum(html), result))
            }.onFailure {
                return Result.failure(NetworkException(it.message))
            }
        throw RuntimeException()
    }

    //根据作者搜索小说
    suspend fun searchNovelByAuthor(author: String, index: Int): Result<NovelCoverResponse> {
        val requestUrl: String?
        val charset: String?
        when(Locale.getDefault()) {
            Locale.TRADITIONAL_CHINESE -> {
                requestUrl = "https://${getWenku8Node()}/modules/article/search.php?searchtype=author&searchkey=$author&page=$index&charset=big5"
                charset = "BIG5-HKSCS"
            }
            else -> {
                requestUrl = "https://${getWenku8Node()}/modules/article/search.php?searchtype=author&searchkey=$author&page=$index&charset=gbk"
                charset = "GBK"
            }
        }
        network.get(requestUrl, cookie!!)
            .awaitResult { body ->
                val html = String(body.body()!!.bytes(), Charset.forName(charset))
                if (Wenku8Parser.isInFiveSecond(html)) {
                    return Result.failure(InFiveSecondException())
                }
                val isOnlyOne = Wenku8Parser.isSearchResultOnlyOne(html, getWenku8Node())
                if (isOnlyOne != null) {
                    return Result.success(
                        NovelCoverResponse(
                            Wenku8Parser.getMaxNum(html),
                            listOf(isOnlyOne)
                        )
                    )
                }
                val result = Wenku8Parser.parseToList(html, getWenku8Node())
                return Result.success(NovelCoverResponse(Wenku8Parser.getMaxNum(html), result))
            }.onFailure {
                return Result.failure(NetworkException(it.message))
            }
        throw RuntimeException()
    }

    //获取用户信息
    suspend fun getUserInfo(): Result<UserInfo> {
        val requestUrl: String?
        val charset: String?
        when(Locale.getDefault()) {
            Locale.TRADITIONAL_CHINESE -> {
                requestUrl = "https://${getWenku8Node()}/userdetail.php?charset=big5"
                charset = "BIG5-HKSCS"
            }
            else -> {
                requestUrl = "https://${getWenku8Node()}/userdetail.php?charset=gbk"
                charset = "GBK"
            }
        }
        network.get(requestUrl, cookie!!)
            .awaitResult { body ->
                val html = String(body.body()!!.bytes(), Charset.forName(charset))
                val result = Wenku8Parser.getUserInfo(html)
                return Result.success(result)
            }.onFailure {
                return Result.failure(NetworkException(it.message))
            }
        throw RuntimeException()
    }

    //获取已完结小说的列表
    suspend fun getCompletionNovel(index: Int): Result<NovelCoverResponse> {
        val requestUrl: String?
        val charset: String?
        when(Locale.getDefault()) {
            Locale.TRADITIONAL_CHINESE -> {
                requestUrl = "https://${getWenku8Node()}/modules/article/articlelist.php?fullflag=1&page=$index&charset=big5"
                charset = "BIG5-HKSCS"
            }
            else -> {
                requestUrl = "https://${getWenku8Node()}/modules/article/articlelist.php?fullflag=1&page=$index&charset=gbk"
                charset = "GBK"
            }
        }
        network.get(requestUrl, cookie!!)
            .awaitResult { body ->
                val html = String(body.body()!!.bytes(), Charset.forName(charset))
                val result = Wenku8Parser.parseToList(html, getWenku8Node())
                return Result.success(
                    NovelCoverResponse(
                        Wenku8Parser.getMaxNum(html),
                        result
                    )
                )
            }.onFailure {
                return Result.failure(NetworkException(it.message))
            }
        throw RuntimeException()
    }

    //获取小说内容
    suspend fun getNovelContent(aid: String, cid: String): Result<ChapterContentResponse> {
        val requestUrl = when(Locale.getDefault()) {
            Locale.TRADITIONAL_CHINESE -> "action=book&do=text&aid=$aid&cid=$cid&t=1"
            else -> "action=book&do=text&aid=$aid&cid=$cid&t=0"
        }
        network.getFromAppWenku8Com(requestUrl)
            .awaitResult {
                val image = Wenku8Parser.getImageFromContent(it)
                var content = it
                content = content.replace(
                    Regex("<!--image-->.*?<!--image-->"),
                    ""
                ) //删除小说内容中的image标签

                return Result.success(ChapterContentResponse(content, image))
            }.onFailure {
                return Result.failure(NetworkException(it.message))
            }
        throw RuntimeException()
    }

    //设置网页版wenku8节点
    fun setWenku8Node(node: String) {
        appConfig.node = node
    }

    //获取网页版wenku8节点
    fun getWenku8Node() = appConfig.node

    //在wenku8.com上签到
    suspend fun sign(): Result<Any?> {
        network.getFromAppWenku8Com("action=block&do=sign")
            .awaitResult {
                return when (it.trim()) {
                    "9" -> Result.failure(SignedInException())
                    else -> Result.success(null)
                }
            }.onFailure {
                return Result.failure(NetworkException(it.message))
            }
        throw RuntimeException()
    }

    //在wenku8.com上登录
    suspend fun loginWenku8Com(): Result<Any?> {
        if (username == null || password == null) return Result.failure(TempSignInException())
        val un = username!!.urlEncode("utf-8")
        val pw = password!!.urlEncode("utf-8")
        network.getFromAppWenku8Com("action=login&username=$un&password=$pw")
            .awaitResult {
                return Result.success(null)
            }.onFailure {
                return Result.failure(NetworkException(it.message))
            }
        throw RuntimeException()
    }
}