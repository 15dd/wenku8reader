package com.cyh128.wenku8reader.util;

import android.content.ContentValues;

public class DatabaseHelper {
    public static void SaveOldReaderReadHistory(String bookUrl, String indexUrl, String title, int location) {
        ContentValues values = new ContentValues();
        values.put("bookUrl", bookUrl);
        values.put("indexUrl", indexUrl);
        values.put("title", title);
        values.put("location", location);
        GlobalConfig.db.replace("old_reader_read_history", null, values);
    }

    public static void SaveNewReaderReadHistory(String bookUrl, String indexUrl, String title, int location) {
        ContentValues values = new ContentValues();
        values.put("bookUrl", bookUrl);
        values.put("indexUrl", indexUrl);
        values.put("title", title);
        values.put("location", location);
        GlobalConfig.db.replace("new_reader_read_history", null, values);
    }

    public static void SaveSetting() {
        ContentValues values = new ContentValues();
        values.put("_id", 1);
        values.put("checkUpdate", GlobalConfig.checkUpdate);
        values.put("bookcaseViewType", GlobalConfig.bookcaseViewType);
        values.put("readerMode", GlobalConfig.readerMode);
        GlobalConfig.db.replace("setting", null, values);
    }

    public static void SaveReaderSetting() {
        ContentValues values = new ContentValues();
        values.put("_id", 1);
        values.put("newFontSize", GlobalConfig.newReaderFontSize);
        values.put("newLineSpacing", GlobalConfig.newReaderLineSpacing);
        values.put("oldFontSize", GlobalConfig.oldReaderFontSize);
        values.put("oldLineSpacing", GlobalConfig.oldReaderLineSpacing);
        values.put("bottomTextSize", GlobalConfig.readerBottomTextSize);
        values.put("isUpToDown", GlobalConfig.isUpToDown);
        values.put("canSwitchChapterByScroll", GlobalConfig.canSwitchChapterByScroll);
        values.put("backgroundColorDay", GlobalConfig.backgroundColorDay);
        values.put("backgroundColorNight", GlobalConfig.backgroundColorNight);
        values.put("textColorDay", GlobalConfig.textColorDay);
        values.put("textColorNight", GlobalConfig.textColorNight);
        GlobalConfig.db.replace("reader", null, values);
    }
}
