package com.cyh128.hikari_novel.util

import com.cyh128.hikari_novel.R
import com.cyh128.hikari_novel.data.model.BookshelfNovelInfo
import com.cyh128.hikari_novel.data.model.Chapter
import com.cyh128.hikari_novel.data.model.Comment
import com.cyh128.hikari_novel.data.model.CommentResponse
import com.cyh128.hikari_novel.data.model.HomeBlock
import com.cyh128.hikari_novel.data.model.LoginResponse
import com.cyh128.hikari_novel.data.model.NovelCover
import com.cyh128.hikari_novel.data.model.NovelInfo
import com.cyh128.hikari_novel.data.model.Reply
import com.cyh128.hikari_novel.data.model.ReplyResponse
import com.cyh128.hikari_novel.data.model.UserInfo
import com.cyh128.hikari_novel.data.model.Volume
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element
import org.jsoup.select.Elements

object Wenku8Parser {
    private const val TAG = "Wenku8Parser"

    fun isInFiveSecond(html: String): Boolean {
        val document = Jsoup.parse(html)
        val a = document.getElementsByClass("blocktitle")
        val t: String = try {
            a.first()!!.text()
        } catch (e: Exception) {
            return false
        }
        return t == "出现错误！" || t == "出現錯誤！"
    }

    //判断搜索结果是否只有一个
    fun isSearchResultOnlyOne(
        html: String,
        wenku8Node: String
    ): NovelCover? {
        try {
            val document = Jsoup.parse(html)
            val a = document.getElementById("content")
            val b = a!!.getElementsByTag("div")[1]
            val c = b.select("span[style=width:145px;display:inline-block;]")[1]
            val bookUrl = c.selectFirst("a")!!.attr("href")
            val t2 = a.getElementsByTag("table").eq(0)
            val title = t2.select("span").eq(0).select("b").eq(0).text()
            val imgUrl = a.select("img").eq(0).attr("src")
            val aid = bookUrl.substring(bookUrl.indexOf("bid=") + 4)
            return NovelCover(
                title,
                imgUrl,
                "https://$wenku8Node/book/$aid.htm",
                aid
            )
        } catch (e: Exception) {
            return null
        }
    }

    //将相关的html解析成List<NovelCover>
    fun parseToList(html: String, node: String): List<NovelCover> {
        val result = ArrayList<NovelCover>()
        val document: Document = Jsoup.parse(html)
        val bookT1: Element = document.getElementById("content")!!
        val bookT2: Elements = bookT1.getElementsByAttributeValue(
            "style",
            "width:373px;height:136px;float:left;margin:5px 0px 5px 5px;"
        )
        for (novelItem in bookT2) {
            var img: String = novelItem.getElementsByTag("img").attr("src")
                .replace("http://", "https://") //http容易加载失败，将其改为https
            val title: String = novelItem.getElementsByTag("a").attr("title")
            val detailUrl =
                "https://$node" + novelItem.getElementsByTag("div").eq(0).select("a").eq(0)
                    .attr("href")
            if (img == "/images/noimg.jpg") img =
                "https://$node/modules/article/images/nocover.jpg"
            val aid = try {
                detailUrl.substring(detailUrl.indexOf("book/") + 5, detailUrl.indexOf(".htm"))
            } catch (e: Exception) {
                detailUrl.substring(detailUrl.indexOf("aid=") + 4, detailUrl.indexOf("&bid="))
            }
            result.add(
                NovelCover(
                    title,
                    img,
                    detailUrl,
                    aid
                )
            )
        }
        return result
    }

    //获取总页数
    fun getMaxNum(html: String): Int {
        val document: Document = Jsoup.parse(html)
        val lastPageT1: Elements = document.getElementsByClass("last")
        var pageCount = 0
        for (temp in lastPageT1) {
            pageCount = temp.text().toInt()
        }
        return pageCount
    }

    //判断用户名和密码是否正确
    fun isUsernameOrPasswordCorrect(html: String): LoginResponse {
        val isUsernameCorrect: Boolean?
        val isPasswordCorrect: Boolean?

        val document: Document = Jsoup.parse(html)

        var a: Element?
        var t: String? = null
        try {
            try {
                a = document.getElementsByClass("blockcontent")[0]
                t = a.getElementsByTag("div")[0].text()
            } catch (_: Exception) {
            }
            try {
                a = document.getElementById("content")
                t = a.select("caption")[0].text()
            } catch (_: Exception) {
            }
        } catch (_: Exception) {
            return LoginResponse(isUsernameCorrect = true, isPasswordCorrect = true)
        }

        if (t!!.contains("用户不存在")) {
            isUsernameCorrect = false
            isPasswordCorrect = true
        } else if (t.contains("密码错误")) {
            isUsernameCorrect = true
            isPasswordCorrect = false
        } else if (t.contains("用户登录")) {
            isUsernameCorrect = false
            isPasswordCorrect = false
        } else {
            isUsernameCorrect = true
            isPasswordCorrect = true
        }
        return LoginResponse(isUsernameCorrect, isPasswordCorrect)
    }

    fun getNovelInfo(html: String): NovelInfo {
        var isOffShelves = false //是否下架

        val document = Jsoup.parse(html)
        val t1 = document.getElementById("content")
        val t2 = t1!!.getElementsByTag("table").eq(0)
        val title = t2.select("span").eq(0).select("b").eq(0).text()
        val author = t2.select("tr").eq(2).select("td").eq(1).text().substring(5)
        val status = t2.select("tr").eq(2).select("td").eq(2).text().substring(5)
        var finUpdate = try {
            t2.select("tr").eq(2).select("td").eq(3).text().substring(5) + ResourceUtil.getString(R.string.update)
        } catch (_: Exception) {
            isOffShelves = true
            ResourceUtil.getString(R.string.off_shelf)
        }
        var imgUrl = t1.select("img").eq(0).attr("src")
        var introduce = t1.select("table").eq(2).select("td").eq(1).select("span").eq(5).html()
        val tag = t1.select("table").eq(2).select("td")[1].select("span")[0].text()
        var tempHeat = t1.select("table").eq(2).select("td")[1].select("span")[1].text()
        val isAnimated: Boolean = try {
            t1.select("table").eq(2).select("td")[0].select("span")[0].text()
            true
        } catch (e: Exception) {
            //本作未动画化
            false
        }
        if (imgUrl.startsWith("http://")) { //http容易加载失败，将其改为https
            imgUrl = imgUrl.replace("http://", "https://")
        }
        if (isOffShelves) { //如果此小说下架
            introduce = tempHeat
            tempHeat = ResourceUtil.getString(R.string.no_heat_msg)
        } else {
            finUpdate = TimeUtil.dateToText2(finUpdate.substringBefore(ResourceUtil.getString(R.string.update))) + ResourceUtil.getString(R.string.update)
        }

        val trending = try {
            ResourceUtil.getString(R.string.rising_rate) + tempHeat.substring(18, 20)
        } catch (e: Exception) {
            ResourceUtil.getString(R.string.rising_rate) + tempHeat.substring(18, 19)
        }

        return NovelInfo(
            title,
            author,
            status,
            finUpdate,
            imgUrl,
            introduce,
            tag.replaceRange(0, 7, "").split(" "),
            ResourceUtil.getString(R.string.heat) + tempHeat.substring(5, 7),
            trending,
            isAnimated
        )
    }

    fun getChapter(html: String): List<Volume> { //获取小说所有章节信息
        val vcsslist: MutableList<Volume> = mutableListOf()

        val document2 = Jsoup.parse(html)
        val c1 = document2.getElementsByTag("td")

        var tempVcss = Volume.empty()
        var tempCcssList: MutableList<Chapter> = mutableListOf()
        var isFirst = true //防止添加空数据
        for (a in c1) {
            var vcssTitle: String
            var ccssTitle: String
            var ccssHtml: String
            try { //测试<td>是不是卷名(vcss)
                vcssTitle = a.selectFirst("td[class=vcss]")!!.text()

                if (!isFirst) {
                    tempVcss.chapters = tempCcssList
                    vcsslist.add(tempVcss)
                    tempVcss = Volume.empty() //清除先前的数据
                    tempCcssList = mutableListOf() //清除先前的数据
                } else isFirst = false

                tempVcss.volumeTitle = vcssTitle //添加卷名
            } catch (e: NullPointerException) { //如果该<td>的内容不是卷名(vcss)，而是章节名(ccss)，就会报空指针异常，那么该<td>就是章节名
                val a1 = a.getElementsByClass("ccss")
                for (a2 in a1) {
                    ccssTitle = a2.select("a").text()
                    ccssHtml = a2.select("a").attr("href")
                    if (ccssTitle.trim { it <= ' ' }.isEmpty()) continue //防止爬取到空数据
                    tempCcssList.add(
                        Chapter(
                            ccssTitle,
                            ccssHtml,
                            ccssHtml.substringAfter("&cid=")
                        )
                    )
                }
            }
        }
        //添加最后一次for循环没有添加的tempCcssList
        tempVcss.chapters = tempCcssList
        vcsslist.add(tempVcss)

        return vcsslist
    }

    //添加小说到书架
    fun addNovel(html: String): Boolean {
        return !html.contains("出现错误！") || !html.contains("出現錯誤！")
    }

    //获取我的书架
    fun getBookshelf(html: String): List<BookshelfNovelInfo> {
        val bni: MutableList<BookshelfNovelInfo> = mutableListOf()
        val document = Jsoup.parse(html)
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
            val aid = bookUrl.substring(bookUrl.indexOf("aid=") + 4, bookUrl.indexOf("&"))
            val imgUrl: String = if (aid.length <= 3) {
                String.format("https://img.wenku8.com/image/%s/%s/%ss.jpg", 0, aid, aid)
            } else {
                String.format("https://img.wenku8.com/image/%s/%s/%ss.jpg", aid[0], aid, aid)
            }
            bni.add(BookshelfNovelInfo(bid, aid, bookUrl, title, imgUrl))
        }
        return bni
    }

    //获取评论
    fun getComment(html: String): CommentResponse {
        val commentResponse = CommentResponse.empty()

        val document = Jsoup.parse(html)
        val a = document.getElementById("content")
        val b = a!!.select("table")[2]
        val c = b.getElementsByTag("tr")
        val pageCount = a.getElementById("pagelink")!!.getElementsByClass("last").text()
        val tempC: MutableList<Comment> = mutableListOf()
        for (d in c) {
            if (d.hasAttr("align")) continue
            val reply = d.select("td")[0].select("a").attr("href")
            val content = d.select("td")[0].select("a").text()
            val viewAndReplyCount = d.select("td")[1].text()
            val userName = d.select("td")[2].select("a").text()
            val time = d.select("td")[3].text()
            tempC.add(
                Comment(
                    reply,
                    content,
                    viewAndReplyCount.substring(viewAndReplyCount.indexOf("/") + 1),
                    viewAndReplyCount.substring(0, viewAndReplyCount.indexOf("/")),
                    userName,
                    TimeUtil.dateToText1(time)
                )
            )
        }
        commentResponse.list.addAll(tempC)
        commentResponse.maxNum = pageCount.toInt()
        return commentResponse
    }


    //获取回复
    fun getReply(html: String): ReplyResponse {
        val replyResponse = ReplyResponse.empty()

        val document = Jsoup.parse(html)
        val a = document.getElementById("content")
        val b = a!!.getElementsByTag("table")
        val d = b.select("table[cellpadding=3]")[1]
        val pageCount = d.getElementsByClass("last").text()
        val tempR: MutableList<Reply> = ArrayList()
        var count = 0
        for (c in b) {
            count++
            if (count < 4) {
                continue
            } else if (count == b.size - 1) {
                break
            }
            val userName = c.select("td")[0].selectFirst("a")!!.text()
            val time = c.select("td")[1].select("div")[1].text().apply {
                substring(0, this.indexOf("|") - 1)
            }
            val content = c.select("td")[1].select("div")[2].text()

            tempR.add(Reply(content, userName, TimeUtil.dateToText1(time)))
        }
        replyResponse.curPage.addAll(tempR)
        replyResponse.maxNum = pageCount.toInt()

        return replyResponse
    }

    fun novelVote(html: String): String =
        Jsoup.parse(html)
            .getElementsByClass("blockcontent")[0].select("div[style=padding:10px]")[0].text()

    fun getUserInfo(html: String): UserInfo {
        val document = Jsoup.parse(html)
        val a = document.getElementById("content")
        val b = a!!.selectFirst("tbody")
        val avatar = b!!.select("tr")[0].select("td")[2].selectFirst("img")!!.attr("src")
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
        return UserInfo(
            avatar,
            userID,
            userName,
            userLevel,
            email,
            signUpDate,
            contribution,
            experience,
            score,
            maxBookcase,
            maxRecommend
        )
    }

    fun getRecommend(html: String): List<HomeBlock> {
        val document = Jsoup.parse(html)
        val a = document.getElementById("centers")

        val homeBlockList = ArrayList<HomeBlock>()

        for (i in 1..3) {
            val blockList = ArrayList<NovelCover>()
            val block = a!!.getElementsByClass("block")[i]
            var blockTitle = block.getElementsByClass("blocktitle")[0].text()
            if (i == 1) blockTitle = blockTitle.substringBefore("(")
            val tempBlock1Content =
                block.select("div[style=float: left;text-align:center;width: 95px; height:155px;overflow:hidden;]")
            for (j in tempBlock1Content) {
                val title = j.getElementsByTag("a")[1].text()
                var img = j.getElementsByTag("img")[0].attr("src")
                if (img.substring(0, 5) != "https") {
                    img = img.replace("http", "https")
                }
                val url = j.getElementsByTag("a")[0].attr("href")
                val aid = url.substring(url.indexOf("book/") + 5, url.indexOf(".htm"))
                blockList.add(NovelCover(title, img, url, aid))
            }
            homeBlockList.add(HomeBlock(blockTitle, blockList))
        }

        val regex = Regex("^(http|https)://[^\\s/$.?#].[^\\s]*$")
        for (i in 2..3) {
            val b = document.select("div[class=main]")[i]
            val blockList = ArrayList<NovelCover>()
            var blockTitle = b.select("div[class=blocktitle]")[0].text()
            if (i == 3) blockTitle = blockTitle.substringBefore("(")
            val tempBlock1Content = b.select("div[style=float: left;text-align:center;width: 95px; height:155px;overflow:hidden;]")
            for (j in tempBlock1Content) {
                var title: String
                var img: String
                var url: String
                var aid: String
                try { //防止因为wenku8官网显示出错导致软件直接crash
                    title = j.getElementsByTag("a")[1].text()
                    img = j.getElementsByTag("img")[0].attr("src")

                    if (!img.matches(regex)) throw IllegalArgumentException()

                    if (img.substring(0, 5) != "https") {
                        img = img.replace("http", "https")
                    }
                    url = j.getElementsByTag("a")[0].attr("href")

                    aid = url.substring(url.indexOf("book/") + 5, url.indexOf(".htm"))
                } catch (e: Exception) {
                    continue
                }
                blockList.add(NovelCover(title, img, url, aid))
            }
            homeBlockList.add(HomeBlock(blockTitle, blockList))
        }

        return homeBlockList
    }

    //获取小说内容中的图像url
    fun getImageFromContent(content: String): List<String> {
        val regex = "<!--image-->(.*?)<!--image-->".toRegex()
        val images = mutableListOf<String>()
        regex.findAll(content).forEach { matchResult ->
            val url = matchResult.groupValues[1].trim()
            images.add(url)
        }
        return images
    }
}