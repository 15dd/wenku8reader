package com.cyh128.wenku8reader;

import android.app.Application;
import android.database.Cursor;

import com.cyh128.wenku8reader.util.VarTemp;
import com.google.android.material.color.DynamicColors;

public class App extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        DynamicColors.applyToActivitiesIfAvailable(this);//添加md动态颜色
        VarTemp.db = openOrCreateDatabase("info.db", MODE_PRIVATE, null);
        //下面是为了防止没有对应的table时报错，一般出现在删除数据库时
        VarTemp.db.execSQL("CREATE TABLE IF NOT EXISTS readHistory(bookUrl TEXT PRIMARY KEY,indexUrl TEXT UNIQUE NOT NULL,title TEXT NOT NULL,location INT NOT NULL)");
        VarTemp.db.execSQL("CREATE TABLE IF NOT EXISTS user_info(_id INTEGER PRIMARY KEY autoincrement,username TEXT,password TEXT)");
        VarTemp.db.execSQL("CREATE TABLE IF NOT EXISTS setting(_id INTEGER UNIQUE,fontSize FLOAT NOT NULL,lineSpacing FLOAT NOT NULL,checkUpdate BOOLEAN NOT NULL,bookcaseViewType BOOLEAN NOT NULL)");
        initSetting();
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        VarTemp.db.close();
    }

    private void initSetting() {
        String sql = "select * from setting where _id=1";
        Cursor cursor = VarTemp.db.rawQuery(sql, null);
        if (cursor.moveToNext()) {
            for (int i = 0; i < cursor.getCount(); i++) {
                cursor.move(i);
                float size = cursor.getFloat(1);
                float lineSpacing = cursor.getFloat(2);
                int checkUpdate = cursor.getInt(3);
                int bookcaseViewType = cursor.getInt(4);
                VarTemp.readerFontSize = size;
                VarTemp.readerLineSpacing = lineSpacing;
                VarTemp.checkUpdate = checkUpdate == 1;
                VarTemp.bookcaseViewType = bookcaseViewType == 1;
            }
            cursor.close();
        } else {
            VarTemp.readerFontSize = 16;//如果第一次打开app,那么设置为默认值
            VarTemp.readerLineSpacing = 10;
            VarTemp.checkUpdate = true;
            VarTemp.bookcaseViewType = false;
        }
    }
}
