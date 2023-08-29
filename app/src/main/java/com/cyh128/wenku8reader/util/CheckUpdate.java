package com.cyh128.wenku8reader.util;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.alibaba.fastjson2.JSON;
import com.cyh128.wenku8reader.R;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Map;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class CheckUpdate { //检查是否有更新
    public enum Mode { WITH_TIP,WITHOUT_TIP }
    public static void checkUpdate(Context context,Mode mode) throws IOException {
        String gitVersion = getGithubLatestReleaseVersion();
        String thisVersion = getVersion(context);
        if (!gitVersion.equals(thisVersion)) {
            new Handler(Looper.getMainLooper()).post(() -> new MaterialAlertDialogBuilder(context)
                    .setIcon(R.drawable.new_update)
                    .setTitle("有新版本")
                    .setMessage("强烈建议您下载最新版本\n您可以前往Github下载最新版本")
                    .setCancelable(false)
                    .setNegativeButton("不更新",null)
                    .setPositiveButton("前往Github下载更新", (dialog, which) -> {
                        Uri uri = Uri.parse("https://github.com/15dd/wenku8reader/releases");
                        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                        context.startActivity(intent);
                    })
                    .show());
        } else {
            if (mode == Mode.WITH_TIP) {
                new Handler(Looper.getMainLooper()).post(() -> Toast.makeText(context,"已经是最新版本",Toast.LENGTH_SHORT).show());
            }
        }
    }

    private static String getGithubLatestReleaseVersion() throws IOException {
        OkHttpClient okHttpClient = new OkHttpClient.Builder().build();
        Request request = new Request.Builder()
                .url("https://api.github.com/repos/15dd/wenku8reader/releases/latest")
                .get()
                .build();
        Response response = okHttpClient.newCall(request).execute();
        String info = new String(response.body().bytes(), StandardCharsets.UTF_8);
        if (info.trim().length() != 0) {
            Map map = (Map) JSON.parse(info);
            if (map != null) {
                String ver = map.get("name").toString();
                ver = ver.substring(8);
                return ver;
            }
        }
        return null;
    }

    private static String getVersion(Context context) {
        PackageManager manager = context.getPackageManager();
        String name = null;
        try {
            PackageInfo info = manager.getPackageInfo(context.getPackageName(), 0);
            name = info.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return name;
    }
}
