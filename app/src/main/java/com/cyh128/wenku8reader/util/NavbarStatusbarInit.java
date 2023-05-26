package com.cyh128.wenku8reader.util;

import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Color;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;


public class NavbarStatusbarInit {
    public static void allTransparent(Activity activity) {
        //https://blog.csdn.net/pbx6666/article/details/120866336 状态栏，导航条沉浸
        Window window = activity.getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setNavigationBarColor(Color.TRANSPARENT);
        window.setStatusBarColor(Color.TRANSPARENT);

        //判断是否为深色模式
        boolean isDarkMode = (activity.getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_YES) != 0;
        if (isDarkMode) { //如果是沈色模式
            Log.e("debug", "darkmode");
            //设置状态栏字体为白色时的状态栏样式
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);//实现状态栏图标和文字颜色为暗色
        }
    }

    public static int getStatusbarHeight(Context context) { //获取状态栏高度
        int height = 0;
        int resourceId = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            height = context.getResources().getDimensionPixelSize(resourceId);
        }
        return height;
    }
}
