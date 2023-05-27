package com.cyh128.wenku8reader.util;


import static org.jsoup.Jsoup.parse;

import android.util.Log;

import com.cyh128.wenku8reader.classLibrary.BookListClass;
import com.cyh128.wenku8reader.classLibrary.BookcaseClass;
import com.cyh128.wenku8reader.classLibrary.ContentsCcssClass;
import com.cyh128.wenku8reader.classLibrary.ContentsVcssClass;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

public class Wenku8Spider {
    public static List<BookListClass> getNovelByType(String type, int pageindex) throws IOException {
        String url = "";
        switch (type) {
            case "toplist":
                //总排行
                url = String.format("https://www.wenku8.net/modules/article/toplist.php?sort=allvisit&page=%d", pageindex);
                break;
            case "lastupdate":
                //今日更新
                url = String.format("https://www.wenku8.net/modules/article/toplist.php?sort=lastupdate&page=%d", pageindex);
                break;
            case "articlelist":
                //全部轻小说
                url = String.format("https://www.wenku8.net/modules/article/articlelist.php?page=%d", pageindex);
                break;
            case "postdate":
                //最新入库
                url = String.format("https://www.wenku8.net/modules/article/toplist.php?sort=postdate&page=%d", pageindex);
                break;
            case "allvote":
                //总推荐
                url = String.format("https://www.wenku8.net/modules/article/toplist.php?sort=allvote&page=%d", pageindex);
                break;
            case "dayvisit":
                //日排行
                url = String.format("https://www.wenku8.net/modules/article/toplist.php?sort=dayvisit&page=%d", pageindex);
                break;
            case "dayvote":
                //日推荐
                url = String.format("https://www.wenku8.net/modules/article/toplist.php?sort=dayvote&page=%d", pageindex);
                break;
        }

        return parseNovelList(loginWenku8.getPageHtml(url));
    }

    public static List<Object> getContents(String url) throws IOException { //获取小说目录
        List<Object> contentsList = new ArrayList<>();
        List<ContentsVcssClass> vcssClassList = new ArrayList<>();
        List<List<ContentsCcssClass>> ccssClassList = new ArrayList<>();

        Document document = parse(loginWenku8.getPageHtml(url));
        Element t1 = document.getElementById("content");
        Elements t2 = t1.getElementsByAttributeValue("style", "width:100px;height:35px;margin:0px;padding:0px;");
        String ContentsUrl = t2.eq(0).select("a").attr("href");
        Document document2 = parse(loginWenku8.getPageHtml(ContentsUrl));
        Elements c1 = document2.getElementsByTag("td");

        /*
        大体思路:
        章节名和卷名都被<td>标签包着，章节名的class为ccss，卷名的class为vcss，这个可以打开f12自己看，先尝试用jsoup的select判断该<td>的class是不是卷名(vcss)，如果不是就是章节名(ccss)，这样就能获取到所以的卷名和章节名了
        接下来就是区分哪些章节名是它所属的卷名的。比如卷名是1，这个卷名内的章节名是1t1,1t2,1t3，如何在一堆数据中对应数据呢(类似这样：1,1t1,2,1t2,2t1)
        我的想法是：在获取完第一个卷名(例如：1)和它所属的章节名列表(例如：1t1,1t2)后，再获取到第二个卷名时，将之前的章节名列表( List<ContentsCcssClass> )添加到总的
        章节名列表( List<List<ContentsCcssClass>> )，再把之前的章节名列表清空，方便第二个卷名所属的章节名的爬取，这样就完成了卷名和章节名数据的对应。
        至于为什么卷名列表是 List<ContentsCcssClass> ，而章节名列表是 List<List<ContentsCcssClass>>，这是为了方便对应卷名和章节名数据
        比如我想获取第一卷的卷名和它的第一个章节名就可以这么做 -> 卷名：List<卷名类>.get(1).name, 章节名：List<List<章节类>>.get(1).get(1).name
         */
        List<ContentsCcssClass> ccss = new ArrayList<>();
        boolean isFirst = true;
        for (Element a : c1) {
            String volumeTitle;
            String chapterTitle;
            String chapterHtml;

            try {//测试<td>是不是卷名(vcss)
                volumeTitle = a.selectFirst("td[class=vcss]").text();
                vcssClassList.add(new ContentsVcssClass(volumeTitle)); //如果是卷名就将其添加进vcssClassList
                if (!isFirst) { //防止在获取第一个卷名时，就添加章节名导致的null
                    ccssClassList.add(ccss); //当爬到卷名时，将上一个卷名所属的章节名list添加进ccssClassList
                    ccss = new ArrayList<>(); //替换为一个新的list，进行下一次添加
                }
                isFirst = false; //设置不是第一次爬卷名了
            } catch (NullPointerException e) { //如果该<td>的内容不是卷名(vcss)，而是章节名(ccss)，就会报空指针异常，那么该<td>就是章节名
                Elements a1 = a.getElementsByClass("ccss");
                for (Element a2 : a1) {
                    chapterTitle = a2.select("a").text();
                    chapterHtml = a2.select("a").attr("href");
                    if (chapterTitle.trim().length() == 0) {//防止爬取到空数据
                        continue;
                    }
                    ccss.add(new ContentsCcssClass(chapterTitle, chapterHtml));//将单个章节名添加进它所属的卷名的章节名list
                }
            }
        }
        //防止获取完最后一卷的章节名时，由于没有下一个卷名而无法执行第84行的代码所导致的null问题
        //程序执行到这里就说明，最后一卷下的几个章节名已经获取完成了，接下来就是代替第84行的作用将这几个章节名添加进去了
        ccssClassList.add(ccss);

        contentsList.add(vcssClassList);
        contentsList.add(ccssClassList);
        return contentsList;
    }

    public static List<String> getNovelDetail(String url) throws IOException {
        List<String> container = new ArrayList<>();

        Document document = parse(loginWenku8.getPageHtml(url));
        Element t1 = document.getElementById("content");
        Elements t2 = t1.getElementsByTag("table").eq(0);
        String title = t2.select("span").eq(0).select("b").eq(0).text();
        String author = t2.select("tr").eq(2).select("td").eq(1).text();
        String status = t2.select("tr").eq(2).select("td").eq(2).text();
        String update = t2.select("tr").eq(2).select("td").eq(3).text();
        String imgUrl = t1.select("img").eq(0).attr("src");
        String introduce = t1.select("table").eq(2).select("td").eq(1).select("span").eq(5).html();
        String comment = t1.select("table").eq(5).select("a").attr("href");

        if (imgUrl.startsWith("http://")) { //http容易(glide)加载失败，将其改为https
            imgUrl = imgUrl.replace("http://", "https://");
        }

        container.add(title);
        container.add(author);
        container.add(status);
        container.add(update);
        container.add(imgUrl);
        container.add(introduce);
        container.add(comment);
        return container;
    }

    public static List<BookListClass> searchNovel(String searchtype, String searchContent, int pageindex) throws IOException { //按作者搜索或按作品名搜索
        List<BookListClass> list = null;
        if (searchtype.equals("articlename")) {
            String url = String.format("https://www.wenku8.net/modules/article/search.php?searchtype=articlename&searchkey=%s&page=%d", URLEncoder.encode(searchContent, "gbk"), pageindex);
            try {
                list = parseNovelList(loginWenku8.getPageHtml(url));
            } catch (Exception e) {
                System.out.println("搜索间隔要大于5秒，请稍后重试");
                return null;
            }

        } else {
            String url = String.format("https://www.wenku8.net/modules/article/search.php?searchtype=author&searchkey=%s&page=%d", URLEncoder.encode(searchContent, "gbk"), pageindex);
            try {
                list = parseNovelList(loginWenku8.getPageHtml(url));
            } catch (Exception e) {
                System.out.println("搜索间隔要大于5秒，请稍后重试");
                return null;
            }
        }
        return list;
    }


    public static int totalBookcase = 0;

    public static List<BookcaseClass> getBookcase() throws IOException {
        List<BookcaseClass> temp = new ArrayList<>();
        int bookIndex = 0;
        String url = "https://www.wenku8.net/modules/article/bookcase.php";
        Document document = parse(loginWenku8.getPageHtml(url));
        Element a = document.getElementById("content");
        Elements b = a.getElementsByTag("tr");
        for (Element c : b) {
            if (c.hasAttr("align")) {
                continue;
            }
            if (c.getElementsByTag("td").eq(0).hasClass("foot")) {
                continue;
            }
            String bid = c.getElementsByTag("td").eq(0).select("input").attr("value");
            String bookUrl = c.getElementsByTag("td").eq(1).select("a").attr("href");
            String title = c.getElementsByTag("td").eq(1).select("a").text();
            String author = c.getElementsByTag("td").eq(2).select("a").text();
            String lastChapter = c.getElementsByTag("td").eq(3).select("a").text();
            String aid = bookUrl.substring(bookUrl.indexOf("aid=") + 4, bookUrl.indexOf("&"));
            String imgUrl;
            if (aid.length() <= 3)  {
                imgUrl = String.format("https://img.wenku8.com/image/%s/%s/%ss.jpg",0,aid,aid);
            } else {
                imgUrl = String.format("https://img.wenku8.com/image/%s/%s/%ss.jpg",aid.charAt(0),aid,aid);
            }

            bookIndex += 1;

            BookcaseClass bcc = new BookcaseClass(bid, aid, bookUrl, title, author, lastChapter,imgUrl);
            bcc.getInfo();
            temp.add(bcc);
        }
        totalBookcase = bookIndex;
        System.out.println("当前：" + totalBookcase);
        return temp;
    }
    public static void removeBook(int bid) throws IOException {
        String url = String.format("https://www.wenku8.net/modules/article/bookcase.php?delid=%d", bid);
        loginWenku8.getPageHtml(url);
    }

    public static boolean addBook(int aid) throws IOException {
        if (totalBookcase >= 300) {
            System.out.println("添加失败，最多收藏300本");
            return false;
        }

        String addUrl = String.format("https://www.wenku8.net/modules/article/addbookcase.php?bid=%d", Integer.valueOf(aid));
        String html = loginWenku8.getPageHtml(addUrl);
        if (html.contains("出现错误！")) {
            return false;
        }
        Log.d("debug", addUrl);
        return true;
    }

    public static List<List<String>> Content(String url, String index) throws IOException {
        List<List<String>> allContent = new ArrayList<>();
        List<String> html = new ArrayList<>();
        List<String> imgUrl = new ArrayList<>();

        Document document = parse(loginWenku8.getPageHtml(url));
        Element a = document.getElementById("content");
        String contentUrl = a.selectFirst("div[style=text-align:center]").getElementsByTag("a").eq(0).attr("href");
        contentUrl = contentUrl.replace("index.htm", index);
        Document document1 = parse(loginWenku8.getPageHtml(contentUrl));
        Element b = document1.getElementById("content");//获取文字，如果有的话
        html.add(b.html());

        Elements c = b.getElementsByTag("img");
        for (Element d : c) {//获取图片，如果有的话
            String src = d.attr("src");
            if (src.startsWith("http://")) { //http容易(glide)加载失败，将其改为https
                src = src.replace("http://", "https://");
            }
            imgUrl.add(src);
        }
        allContent.add(html);
        allContent.add(imgUrl);

        return allContent;
    }

    public static List<BookListClass> parseNovelList(String resHtml) {
        //该函数可以用于解析小说列表
        List<BookListClass> NLC = new ArrayList<>();

        Document document = parse(resHtml);
        Elements lastPageT1 = document.getElementsByClass("last");//获取总页数

        int totalPage = 0;
        for (Element temp : lastPageT1) {
            totalPage = Integer.parseInt(temp.text());
        }
        Element bookT1 = document.getElementById("content");
        Elements bookT2 = bookT1.getElementsByAttributeValue("style", "width:373px;height:136px;float:left;margin:5px 0px 5px 5px;");
        for (Element temp : bookT2) {
            String pic = temp.getElementsByTag("img").attr("src");
            String bookTitle = temp.getElementsByTag("a").attr("title");
            String Author = temp.getElementsByTag("p").eq(0).text();
            String other = temp.getElementsByTag("p").eq(1).text();
            String tags = temp.getElementsByTag("span").eq(1).text();
            String bookUrl = temp.getElementsByTag("p").eq(4).select("a").eq(0).attr("href");

            if (pic.startsWith("http://")) { //http容易(glide)加载失败，将其改为https
                pic = pic.replace("http://", "https://");
            }

            BookListClass nlc = new BookListClass(pic, bookTitle, Author, other, tags, bookUrl, totalPage);
            NLC.add(nlc);
            nlc.getInfo();
        }
        return NLC;
    }

    public static List<List<String>> getComment(String url, int index) throws IOException {
        List<List<String>> allComment = new ArrayList<>();
        url += "&page=" + index;
        Document document = Jsoup.parse(loginWenku8.getPageHtml(url));
        Element a = document.getElementById("content");
        Element b = a.select("table").get(2);
        Elements c = b.getElementsByTag("tr");
        final String lastPage = a.getElementById("pagelink").getElementsByClass("last").text();
        System.out.println(lastPage);
        for (Element d : c) {
            if (d.hasAttr("align")) {
                continue;
            }
            List<String> Comment = new ArrayList<>();
            String detail = d.select("td").get(0).select("a").attr("href");
            String comment = d.select("td").get(0).select("a").text();
            String viewData = d.select("td").get(1).text();
            String user = d.select("td").get(2).select("a").text();
            String date = d.select("td").get(3).text();
            System.out.println(detail + comment + viewData + user + date);
            Comment.add(lastPage);
            Comment.add(detail);
            Comment.add(viewData);
            Comment.add(user);
            Comment.add(date);
            Comment.add(comment);
            allComment.add(Comment);
        }
        return allComment;
    }

    public static List<List<String>> getCommentInComment(String url, int index) throws IOException {
        List<List<String>> allComment = new ArrayList<>();
        url += "&page=" + index;
        Document document = Jsoup.parse(loginWenku8.getPageHtml(url));
        Element a = document.getElementById("content");
        Elements b = a.getElementsByTag("table");
        Element d = b.select("table[cellpadding=3]").get(1);
        final String lastPage = d.getElementsByClass("last").text();
        System.out.println(lastPage);
        int count = 0;
        for (Element c : b) {
            count++;
            if (count < 4) {
                continue;
            } else if (count == b.size() - 1) {
                break;
            }
            List<String> Comment = new ArrayList<>();
            String user = c.select("td").get(0).selectFirst("a").text();
            String date = c.select("td").get(1).select("div").get(1).text();
            date = date.substring(0, date.indexOf("|") - 1);
            String comment = c.select("td").get(1).select("div").get(2).text();
            System.out.println(user + date + comment);
            Comment.add(user);
            Comment.add(date);
            Comment.add(comment);
            Comment.add(lastPage);
            allComment.add(Comment);
        }
        return allComment;
    }
}
