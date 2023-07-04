package com.cyh128.wenku8reader.activity;


import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.cyh128.wenku8reader.util.VarTemp;
import com.cyh128.wenku8reader.util.loginWenku8;
import com.cyh128.wenku8reader.R;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

public class LoginingActivity extends AppCompatActivity {
    private String username;
    private String password;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_logining);
        Login login = new Login();
        login.start();
    }

    private boolean getNameAndPassword() {
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
                boolean flag = loginWenku8.login(username, password);
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
                runOnUiThread(() -> {
                    new MaterialAlertDialogBuilder(LoginingActivity.this)
                            .setCancelable(false)//禁止点击其他区域
                            .setTitle("网络错误")
                            .setMessage("可能是以下原因造成的:\n\n1 -> 请检查是否正在连接VPN或代理服务器\n2 -> 未连接上网络\n3 -> 服务器(wenku8.net)出错，(此网站有时会登不上去)\n\n请稍后再试")
                            .setPositiveButton("退出软件", (dialogInterface, i) -> {
                                android.os.Process.killProcess(android.os.Process.myPid());//杀死整个进程
                            })
                            .setIcon(R.drawable.warning)
                            .show();
                });
            }
        }
    }
}
