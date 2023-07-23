package com.cyh128.wenku8reader.activity;


import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.cyh128.wenku8reader.R;
import com.cyh128.wenku8reader.util.VarTemp;
import com.cyh128.wenku8reader.util.LoginWenku8;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.io.File;

public class LoginingActivity extends AppCompatActivity {
    private String username;
    private String password;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_logining);

        if (initDataBase()) {
            Login login = new Login();
            login.start();
        }
    }

    private boolean getNameAndPassword() {
        try {
            Cursor cursor = VarTemp.db.query("user_info", null, null, null, null, null, null);
            if (cursor.moveToNext()) {
                for (int i = 0; i < cursor.getCount(); i++) {
                    cursor.move(i);
                    //int id = cursor.getInt(0);
                    username = cursor.getString(1);
                    password = cursor.getString(2);
                }
                cursor.close();
                return true;
            }
            return false;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    private class Login extends Thread {
        @Override
        public void run() {
            try {
                if (!getNameAndPassword()) {//如果数据库中没有保存用户名和密码时，跳转到用户名和密码输入界面
                    Intent gotoinput = new Intent(LoginingActivity.this, LoginInputActivity.class);
                    startActivity(gotoinput);
                    LoginingActivity.this.finish();
                    return;
                }
                boolean flag = LoginWenku8.login(username, password);
                if (flag) {
                    Intent toMainAppUI = new Intent(LoginingActivity.this, AppActivity.class);
                    startActivity(toMainAppUI);
                    LoginingActivity.this.finish();
                } else {
                    Intent gotoinput = new Intent(LoginingActivity.this, LoginInputActivity.class);
                    startActivity(gotoinput);
                    LoginingActivity.this.finish();
                }
            } catch (Exception e) { //当登录错误时
                e.printStackTrace();
                runOnUiThread(() -> {
                    new MaterialAlertDialogBuilder(LoginingActivity.this)
                            .setCancelable(false)//禁止点击其他区域
                            .setTitle("网络错误")
                            .setMessage("可能是以下原因造成的:\n\n1 -> 请检查是否正在连接VPN或代理服务器\n2 -> 未连接上网络\n3 -> 服务器(wenku8.net)出错，(此网站有时会登不上去)\n\n请稍后再试")
                            .setPositiveButton("重启软件", (dialogInterface, i) -> {
                                final Intent intent = getPackageManager().getLaunchIntentForPackage(getPackageName());
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                startActivity(intent);
                                finish();
                            })
                            .setIcon(R.drawable.warning)
                            .show();
                });
            }
        }
    }

    private boolean initDataBase() {//初始化数据库，读取数据
        VarTemp.db = openOrCreateDatabase("info.db", MODE_PRIVATE, null);
        //下面是为了防止没有对应的table时报错，一般出现在删除数据库时
        VarTemp.db.execSQL("CREATE TABLE IF NOT EXISTS readHistory(bookUrl TEXT PRIMARY KEY,indexUrl TEXT UNIQUE NOT NULL,title TEXT NOT NULL,location INT NOT NULL)");
        VarTemp.db.execSQL("CREATE TABLE IF NOT EXISTS user_info(_id INTEGER PRIMARY KEY autoincrement,username TEXT,password TEXT)");
        VarTemp.db.execSQL("CREATE TABLE IF NOT EXISTS setting(_id INTEGER UNIQUE,fontSize FLOAT NOT NULL,lineSpacing FLOAT NOT NULL,checkUpdate BOOLEAN NOT NULL,bookcaseViewType BOOLEAN NOT NULL)");

        try {
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
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            runOnUiThread(() -> {
                new MaterialAlertDialogBuilder(this)
                        .setIcon(R.drawable.database_error)
                        .setTitle("数据库出现问题")
                        .setMessage("数据库读取出现问题，可能是您升级过软件，点击重启软件，软件会自行修复\n注意：该过程会清除设置数据、登录状态、本地阅读记录")
                        .setCancelable(false)
                        .setPositiveButton("重启软件", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                cleanDatabases();//删除数据库

                                final Intent intent = getPackageManager().getLaunchIntentForPackage(getPackageName());
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                startActivity(intent);
                                finish();
                            }
                        })
                        .show();
            });
            return false;
        }
    }

    private void cleanDatabases() { //https://www.jianshu.com/p/603329500679
        deleteFilesByDirectory(new File("/data/data/"
                + getPackageName() + "/databases"));
    }
    private void deleteFilesByDirectory(File directory) {
        if (directory != null && directory.exists() && directory.isDirectory()) {
            for (File item : directory.listFiles()) {
                item.delete();
            }
        }
    }
}
