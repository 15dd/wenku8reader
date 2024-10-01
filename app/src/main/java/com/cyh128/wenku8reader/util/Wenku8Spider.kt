package com.cyh128.wenku8reader.util

import android.util.Log
import com.cyh128.wenku8reader.bean.BookListBean
import com.cyh128.wenku8reader.bean.BookcaseBean
import com.cyh128.wenku8reader.bean.ContentsCcssBean
import com.cyh128.wenku8reader.bean.ContentsVcssBean
import org.jsoup.Jsoup
import java.io.IOException
import java.net.URLEncoder

object Wenku8Spider {
    @JvmStatic
    @Throws(IOException::class)
    fun getNovelByType(type: String?, pageindex: Int): List<BookListBean> {
        var url = ""
        when (type) {
            "toplist" ->                 //总排行
                url = String.format(
                    "https://${GlobalConfig.domain}/modules/article/toplist.php?sort=allvisit&page=%d",
                    pageindex
                )

            "lastupdate" ->                 //今日更新
                url = String.format(
                    "https://${GlobalConfig.domain}/modules/article/toplist.php?sort=lastupdate&page=%d",
                    pageindex
                )

            "articlelist" ->                 //全部轻小说
                url = String.format(
                    "https://${GlobalConfig.domain}/modules/article/articlelist.php?page=%d",
                    pageindex
                )

            "postdate" ->                 //最新入库
                url = String.format(
                    "https://${GlobalConfig.domain}/modules/article/toplist.php?sort=postdate&page=%d",
                    pageindex
                )

            "allvote" ->                 //总推荐
                url = String.format(
                    "https://${GlobalConfig.domain}/modules/article/toplist.php?sort=allvote&page=%d",
                    pageindex
                )

            "dayvisit" ->                 //日排行
                url = String.format(
                    "https://${GlobalConfig.domain}/modules/article/toplist.php?sort=dayvisit&page=%d",
                    pageindex
                )

            "dayvote" ->                 //日推荐
                url = String.format(
                    "https://${GlobalConfig.domain}/modules/article/toplist.php?sort=dayvote&page=%d",
                    pageindex
                )

            "monthvisit" ->                 //月排行
                url = String.format(
                    "https://${GlobalConfig.domain}/modules/article/toplist.php?sort=monthvisit&page=%d",
                    pageindex
                )

            "monthvote" ->                 //月推荐
                url = String.format(
                    "https://${GlobalConfig.domain}/modules/article/toplist.php?sort=monthvote&page=%d",
                    pageindex
                )

            "weekvisit" ->                 //周排行
                url = String.format(
                    "https://${GlobalConfig.domain}/modules/article/toplist.php?sort=weekvisit&page=%d",
                    pageindex
                )

            "weekvote" ->                 //周推荐
                url = String.format(
                    "https://${GlobalConfig.domain}/modules/article/toplist.php?sort=weekvote&page=%d",
                    pageindex
                )

            "goodnum" ->                 //总推荐
                url = String.format(
                    "https://${GlobalConfig.domain}/modules/article/toplist.php?sort=goodnum&page=%d",
                    pageindex
                )

            "size" ->                 //字数排行
                url = String.format(
                    "https://${GlobalConfig.domain}/modules/article/toplist.php?sort=size&page=%d",
                    pageindex
                )
        }
        return parseNovelList(LoginWenku8.getPageHtml(url))
    }

    @JvmStatic
    @Throws(IOException::class)
    fun getContents(url: String): List<Any> { //获取小说目录
        val contentsList: MutableList<Any> = ArrayList()
        val vcssClassList: MutableList<ContentsVcssBean> = ArrayList()
        val ccssClassList: MutableList<List<ContentsCcssBean>> = ArrayList()
        val document = Jsoup.parse(LoginWenku8.getPageHtml(url))
        val t1 = document.getElementById("content")
        val t2 = t1!!.getElementsByAttributeValue(
            "style",
            "width:100px;height:35px;margin:0px;padding:0px;"
        )
        val ContentsUrl = "https://${GlobalConfig.domain}" + t2.eq(0).select("a").attr("href")
        val document2 = Jsoup.parse(LoginWenku8.getPageHtml(ContentsUrl))
        val c1 = document2.getElementsByTag("td")

        /*
        大体思路:
        章节名和卷名都被<td>标签包着，章节名的class为ccss，卷名的class为vcss，这个可以打开f12自己看，先尝试用jsoup的select判断该<td>的class是不是卷名(vcss)，如果不是就是章节名(ccss)，这样就能获取到所以的卷名和章节名了
        接下来就是区分哪些章节名是它所属的卷名的。比如卷名是1，这个卷名内的章节名是1t1,1t2,1t3，如何在一堆数据中对应数据呢(类似这样：1,1t1,2,1t2,2t1)
        我的想法是：在获取完第一个卷名(例如：1)和它所属的章节名列表(例如：1t1,1t2)后，再获取到第二个卷名时，将之前的章节名列表( List<ContentsCcssClass> )添加到总的
        章节名列表( List<List<ContentsCcssClass>> )，再把之前的章节名列表清空，方便第二个卷名所属的章节名的爬取，这样就完成了卷名和章节名数据的对应。
        至于为什么卷名列表是 List<ContentsCcssClass> ，而章节名列表是 List<List<ContentsCcssClass>>，这是为了方便对应卷名和章节名数据
        比如我想获取第一卷的卷名和它的第一个章节名就可以这么做 -> 卷名：List<卷名类>.get(1).name, 章节名：List<List<章节类>>.get(1).get(1).name
         */
        var ccss: MutableList<ContentsCcssBean> = ArrayList()
        var isFirst = true
        for (a in c1) {
            var volumeTitle: String?
            var chapterTitle: String
            var chapterHtml: String
            try { //测试<td>是不是卷名(vcss)
                volumeTitle = a.selectFirst("td[class=vcss]")!!.text()
                vcssClassList.add(ContentsVcssBean(volumeTitle)) //如果是卷名就将其添加进vcssClassList
                if (!isFirst) { //防止在获取第一个卷名时，就添加章节名导致的null
                    ccssClassList.add(ccss) //当爬到卷名时，将上一个卷名所属的章节名list添加进ccssClassList
                    ccss = ArrayList() //替换为一个新的list，进行下一次添加
                }
                isFirst = false //设置不是第一次爬卷名了
            } catch (e: NullPointerException) { //如果该<td>的内容不是卷名(vcss)，而是章节名(ccss)，就会报空指针异常，那么该<td>就是章节名
                val a1 = a.getElementsByClass("ccss")
                for (a2 in a1) {
                    chapterTitle = a2.select("a").text()
                    chapterHtml = a2.select("a").attr("href")
                    if (chapterTitle.trim { it <= ' ' }.isEmpty()) { //防止爬取到空数据
                        continue
                    }
                    ccss.add(ContentsCcssBean(chapterTitle, chapterHtml)) //将单个章节名添加进它所属的卷名的章节名list
                }
            }
        }
        //防止获取完最后一卷的章节名时，由于没有下一个卷名而无法执行第84行的代码所导致的null问题
        //程序执行到这里就说明，最后一卷下的几个章节名已经获取完成了，接下来就是代替第84行的作用将这几个章节名添加进去了
        ccssClassList.add(ccss)
        contentsList.add(vcssClassList)
        contentsList.add(ccssClassList)
        return contentsList
    }

    @JvmStatic
    @Throws(IOException::class)
    fun getNovelDetail(url: String): List<String> {
        val container: MutableList<String> = ArrayList()
        val document = Jsoup.parse(LoginWenku8.getPageHtml(url))
        val t1 = document.getElementById("content")
        val t2 = t1!!.getElementsByTag("table").eq(0)
        val title = t2.select("span").eq(0).select("b").eq(0).text()
        val author = t2.select("tr").eq(2).select("td").eq(1).text()
        val status = t2.select("tr").eq(2).select("td").eq(2).text()
        val update = t2.select("tr").eq(2).select("td").eq(3).text()
        var imgUrl = t1.select("img").eq(0).attr("src")
        val introduce = t1.select("table").eq(2).select("td").eq(1).select("span").eq(5).html()
        val comment = t1.select("table").eq(5).select("a").attr("href")
        val tag = t1.select("table").eq(2).select("td")[1].select("span")[0].text()
        val popular = t1.select("table").eq(2).select("td")[1].select("span")[1].text()
        val anime: String = try {
            t1.select("table").eq(2).select("td")[0].select("span")[0].text()
        } catch (e: Exception) {
            "本作未动画化"
        }
        if (imgUrl.startsWith("http://")) { //http容易(glide)加载失败，将其改为https
            imgUrl = imgUrl.replace("http://", "https://")
        }
        container.add(title)
        container.add(author)
        container.add(status)
        container.add(update)
        container.add(imgUrl)
        container.add(introduce)
        container.add(comment)
        container.add(tag)
        container.add(popular)
        container.add(anime)
        return container
    }

    @JvmStatic
    @Throws(IOException::class)
    fun searchNovel(
        searchtype: String,
        searchContent: String?,
        pageindex: Int
    ): List<BookListBean> { //按作者搜索或按作品名搜索
        val list: MutableList<BookListBean> = ArrayList()
        var html: String? = null
        val url: String = if (searchtype == "articlename") {
            String.format(
                "https://${GlobalConfig.domain}/modules/article/search.php?searchtype=articlename&searchkey=%s&page=%d",
                URLEncoder.encode(searchContent, "gbk"),
                pageindex
            )
        } else {
            String.format(
                "https://${GlobalConfig.domain}/modules/article/search.php?searchtype=author&searchkey=%s&page=%d",
                URLEncoder.encode(searchContent, "gbk"),
                pageindex
            )
        }
        return try {
            html = LoginWenku8.getPageHtml(url)
            val document = Jsoup.parse(html)
            val a = document.getElementById("content")
            val b = a!!.select("span[style=width:180px;display:inline-block;]")[1]
            var bookUrl = b.selectFirst("a")!!.attr("href")
            bookUrl = String.format(
                "https://${GlobalConfig.domain}/book/%s.htm",
                bookUrl.substring(bookUrl.indexOf("bid=") + 4)
            )
            Log.d("debug", "search bookurl$bookUrl")
            list.add(BookListBean(bookUrl))
            list
        } catch (e: Exception) {
            list.addAll(parseNovelList(html!!))
            list
        }
    }

    @JvmStatic
    @get:Throws(IOException::class)
    val bookcase: MutableList<BookcaseBean>
        get() {
            val temp: MutableList<BookcaseBean> = ArrayList()
            var bookIndex = 0
            var totalBookcase = 0
            val url = "https://${GlobalConfig.domain}/modules/article/bookcase.php"
            val document = Jsoup.parse(LoginWenku8.getPageHtml(url))
            val a = document.getElementById("content")
            val b = a!!.getElementsByTag("tr")
            for (c in b) {
                if (c.hasAttr("align")) {
                    continue
                }
                if (c.getElementsByTag("td").eq(0).hasClass("foot")) {
                    continue
                }
                val bid = c.getElementsByTag("td").eq(0).select("input").attr("value")
                val bookUrl = c.getElementsByTag("td").eq(1).select("a").attr("href")
                val title = c.getElementsByTag("td").eq(1).select("a").text()
                val author = c.getElementsByTag("td").eq(2).select("a").text()
                val lastChapter = c.getElementsByTag("td").eq(3).select("a").text()
                val aid = bookUrl.substring(bookUrl.indexOf("aid=") + 4, bookUrl.indexOf("&"))
                val imgUrl: String = if (aid.length <= 3) {
                    String.format("https://img.wenku8.com/image/%s/%s/%ss.jpg", 0, aid, aid)
                } else {
                    String.format("https://img.wenku8.com/image/%s/%s/%ss.jpg", aid[0], aid, aid)
                }
                bookIndex += 1
                val bcc = BookcaseBean(bid, aid, bookUrl, title, author, lastChapter, imgUrl)
                bcc.info
                temp.add(bcc)
            }
            totalBookcase = bookIndex
            println("当前：$totalBookcase")
            return temp
        }

    @JvmStatic
    @Throws(IOException::class)
    fun removeBook(bid: Int) {
        val url = String.format("https://${GlobalConfig.domain}/modules/article/bookcase.php?delid=%d", bid)
        LoginWenku8.getPageHtml(url)
    }

    @JvmStatic
    @Throws(IOException::class)
    fun addBook(aid: Int): Boolean {
        val addUrl = String.format(
            "https://${GlobalConfig.domain}/modules/article/addbookcase.php?bid=%d",
            Integer.valueOf(aid)
        )
        val html = LoginWenku8.getPageHtml(addUrl)
        if (html.contains("出现错误！")) {
            return false
        }
        Log.d("debug", addUrl)
        return true
    }

    //https://blog.csdn.net/winerpro/article/details/127831193
    @JvmStatic
    @Throws(IOException::class)
    fun Content(url: String, index: String?): List<List<String>> {
        val allContent: MutableList<List<String>> = ArrayList()
        val html: MutableList<String> = ArrayList()
        val imgUrl: MutableList<String> = ArrayList()
        val document = Jsoup.parse(LoginWenku8.getPageHtml(url))
        Log.d("tag", "wenku8spider url:$url")
        val a = document.getElementById("content")
        var contentUrl = "https://${GlobalConfig.domain}" + a!!.selectFirst("div[style=text-align:center]")!!.getElementsByTag("a").eq(0).attr("href")
        contentUrl = contentUrl.replace("index.htm", index!!)
        val document1 = Jsoup.parse(LoginWenku8.getPageHtml(contentUrl))
        val b = document1.getElementById("content")!! //获取文字，如果有的话

        val c = b.getElementsByTag("img")
        for (d in c) { //获取图片，如果有的话
            val src = d.attr("src")
            imgUrl.add(src)
        }

        b.select("div[class=divimage]").forEachIndexed { i, element ->
            element.text(">>>>>【（插图$i）图片在下一页】")
        }
        html.add(b.html())

        allContent.add(html)
        allContent.add(imgUrl)
        return allContent
    }

    @JvmStatic
    fun parseNovelList(resHtml: String): List<BookListBean> {
        //该函数可以用于解析小说列表
        val NLC: MutableList<BookListBean> = ArrayList()
        val document = Jsoup.parse(resHtml)
        val lastPageT1 = document.getElementsByClass("last") //获取总页数
        var totalPage = 0
        for (temp in lastPageT1) {
            totalPage = temp.text().toInt()
        }
        val bookT1 = document.getElementById("content")
        val bookT2 = bookT1!!.getElementsByAttributeValue(
            "style",
            "width:373px;height:136px;float:left;margin:5px 0px 5px 5px;"
        )
        for (temp in bookT2) {
            var pic = temp.getElementsByTag("img").attr("src").replace("http://", "https://") //http容易(glide)加载失败，将其改为https
            val bookTitle = temp.getElementsByTag("a").attr("title")
            val Author = temp.getElementsByTag("p").eq(0).text()
            val other = temp.getElementsByTag("p").eq(1).text()
            val tags = temp.getElementsByTag("span").eq(1).text()
            val bookUrl = "https://${GlobalConfig.domain}" + temp.getElementsByTag("div").eq(0).select("a").eq(0).attr("href")
            if (pic == "/images/noimg.jpg") {
                pic = "https://${GlobalConfig.domain}/modules/article/images/nocover.jpg"
            }
            val nlc = BookListBean(pic, bookTitle, Author, other, tags, bookUrl, totalPage)
            NLC.add(nlc)
            nlc.info
        }
        return NLC
    }

    @JvmStatic
    @Throws(IOException::class)
    fun getComment(url: String, index: Int): List<List<String>> {
        var url = url
        val allComment: MutableList<List<String>> = ArrayList()
        url += "&page=$index"
        val document = Jsoup.parse(LoginWenku8.getPageHtml(url))
        val a = document.getElementById("content")
        val b = a!!.select("table")[2]
        val c = b.getElementsByTag("tr")
        val lastPage = a.getElementById("pagelink")!!.getElementsByClass("last").text()
        println(lastPage)
        for (d in c) {
            if (d.hasAttr("align")) {
                continue
            }
            val Comment: MutableList<String> = ArrayList()
            val detail = d.select("td")[0].select("a").attr("href")
            val comment = d.select("td")[0].select("a").text()
            val viewData = d.select("td")[1].text()
            val user = d.select("td")[2].select("a").text()
            val date = d.select("td")[3].text()
            Comment.add(lastPage)
            Comment.add(detail)
            Comment.add(viewData)
            Comment.add(user)
            Comment.add(date)
            Comment.add(comment)
            allComment.add(Comment)
        }
        return allComment
    }

    @JvmStatic
    @Throws(IOException::class)
    fun getCommentInComment(url: String, index: Int): List<List<String>> {
        var url = url
        val allComment: MutableList<List<String>> = ArrayList()
        url += "&page=$index"
        val document = Jsoup.parse(LoginWenku8.getPageHtml(url))
        val a = document.getElementById("content")
        val b = a!!.getElementsByTag("table")
        val d = b.select("table[cellpadding=3]")[1]
        val lastPage = d.getElementsByClass("last").text()
        println(lastPage)
        var count = 0
        for (c in b) {
            count++
            if (count < 4) {
                continue
            } else if (count == b.size - 1) {
                break
            }
            val Comment: MutableList<String> = ArrayList()
            val user = c.select("td")[0].selectFirst("a")!!.text()
            var date = c.select("td")[1].select("div")[1].text()
            date = date.substring(0, date.indexOf("|") - 1)
            val comment = c.select("td")[1].select("div")[2].text()
            Comment.add(user)
            Comment.add(date)
            Comment.add(comment)
            Comment.add(lastPage)
            allComment.add(Comment)
        }
        return allComment
    }

    @JvmStatic
    @get:Throws(IOException::class)
    val userInfo: List<String>
        get() {
            val userInfo: MutableList<String> = ArrayList()
            val document =
                Jsoup.parse(LoginWenku8.getPageHtml("https://${GlobalConfig.domain}/userdetail.php"))
            val a = document.getElementById("content")
            val b = a!!.selectFirst("tbody")
            val avatar = b!!.select("tr")[0].select("td")[2].selectFirst("img")!!
                .attr("src")
            val userID = b.select("tr")[0].select("td")[1].text()
            val userName = b.select("tr")[2].select("td")[1].text()
            val userLevel = b.select("tr")[4].select("td")[1].text()
            val email = b.select("tr")[7].selectFirst("a")!!.text()
            val signUpDate = b.select("tr")[12].select("td")[1].text()
            val contribution = b.select("tr")[13].select("td")[1].text()
            val experience = b.select("tr")[14].select("td")[1].text()
            val score = b.select("tr")[15].select("td")[1].text()
            val maxBookcase = b.select("tr")[18].select("td")[1].text()
            val maxRecommend = b.select("tr")[19].select("td")[1].text()
            userInfo.add(avatar)
            userInfo.add(userID)
            userInfo.add(userName)
            userInfo.add(userLevel)
            userInfo.add(email)
            userInfo.add(signUpDate)
            userInfo.add(contribution)
            userInfo.add(experience)
            userInfo.add(score)
            userInfo.add(maxBookcase)
            userInfo.add(maxRecommend)
            return userInfo
        }

    @JvmStatic
    @Throws(IOException::class)
    fun bookVote(aid: Int): String {
        val url = "https://${GlobalConfig.domain}/modules/article/uservote.php?id=$aid"
        val document = Jsoup.parse(LoginWenku8.getPageHtml(url))
        return document.getElementsByClass("blockcontent")[0].select("div[style=padding:10px]")[0].text()
    }
}
