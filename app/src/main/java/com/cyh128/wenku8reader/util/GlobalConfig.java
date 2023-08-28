package com.cyh128.wenku8reader.util;

import android.database.sqlite.SQLiteDatabase;

public class GlobalConfig {//app全局变量存放处
    public static float newReaderFontSize;
    public static float newReaderLineSpacing;
    public static float oldReaderFontSize;
    public static float oldReaderLineSpacing;
    public static float readerBottomTextSize;
    public static boolean isUpToDown;
    public static boolean canSwitchChapterByScroll;
    public static String backgroundColorDay;
    public static String backgroundColorNight;
    public static String textColorDay;
    public static String textColorNight;
    public static boolean checkUpdate;
    public static boolean bookcaseViewType;
    public static SQLiteDatabase db;
    public static boolean isFiveSecondDone = true;
    public static int readerMode;
}
