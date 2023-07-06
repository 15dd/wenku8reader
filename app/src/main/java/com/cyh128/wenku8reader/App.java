package com.cyh128.wenku8reader;

import android.app.Application;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Handler;
import android.os.Message;

import androidx.annotation.NonNull;

import com.cyh128.wenku8reader.activity.CrashActivity;
import com.cyh128.wenku8reader.activity.LoginInputActivity;
import com.cyh128.wenku8reader.activity.LoginingActivity;
import com.cyh128.wenku8reader.util.VarTemp;
import com.developer.crashx.config.CrashConfig;
import com.google.android.material.color.DynamicColors;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

public class App extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        DynamicColors.applyToActivitiesIfAvailable(this);//添加md动态颜色
        CrashConfig.Builder.create()
                .errorActivity(CrashActivity.class)
                .apply();
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        VarTemp.db.close();
    }
}
