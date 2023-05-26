package com.cyh128.wenku8reader.util;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

import okhttp3.FormBody;
import okhttp3.Headers;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class loginWenku8 {
    private static final String ua = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/112.0.0.0 Safari/537.36 Edg/112.0.1722.68";
    private static OkHttpClient okHttpClient = new OkHttpClient.Builder().build();
    private static String cookie = "";

    public static String getPageHtml(String url) throws IOException {
        Request request = new Request.Builder()
                .url(url)
                .addHeader("cookie", cookie)
                .get()
                .build();
        Response response = okHttpClient.newCall(request).execute();
        String html = new String(response.body().bytes(), "gbk");
        return html;
    }

    public static boolean login(String username, String password) throws IOException {
        HashMap<String, String> paramsMap = new HashMap<>();
        paramsMap.put("username", username);
        paramsMap.put("password", password);
        paramsMap.put("action", "login");
        paramsMap.put("Content-Type", "application/x-www-form-urlencoded");
        paramsMap.put("User-Agent", ua);
        paramsMap.put("X-Requested-With", "XMLHttpRequest");
        FormBody.Builder builder = new FormBody.Builder();
        for (String key : paramsMap.keySet()) {
            //追加表单信息
            builder.add(key, paramsMap.get(key));
        }
        RequestBody formBody = builder.build();
        Request request = new Request.Builder().url("https://www.wenku8.net/login.php").post(formBody).build();

        Response response = okHttpClient.newCall(request).execute();
        String html = new String(response.body().bytes(), "gbk");
        if (!isCorrectUsernameOrPassword(html)) { //判断密码正确
            System.out.println("error");
            return false;
        }

        //保存获取到的cookie==============================================================================
        Headers headers = response.headers();
        boolean repeat = false;
        List<String> cookies = headers.values("Set-Cookie");
        for (String a : cookies) {
            if (a.substring(0, a.indexOf("=")).equals("PHPSESSID") && !repeat) { //防止重复PHPSESSID
                repeat = true;
                continue;
            }
            System.out.println(a);
            String temp = a.substring(0, a.indexOf(";"));
//            System.out.println("截取_之前字符串:"+temp);
            cookie += temp + ";";
        }
        //==============================================================================================
        return true;
    }

    public static boolean isCorrectUsernameOrPassword(String html) { //判断密码是否正确
        Document document = Jsoup.parse(html);
        Elements a = document.getElementsByClass("blocktitle");
        String t;
        try {
            t = a.first().text();
        } catch (Exception e) {
            return true;
        }
        if (t.equals("出现错误！")) {
//            Log.e("debug","错误");
//            Elements b = document.getElementsByClass("blockcontent");
//            b.first().getElementsByTag("br");
//            Log.d("debug",b.text().replace(" 请 返 回 并修正 [关闭本窗口]",""));
            return false;
        }
        return true;
    }
}
