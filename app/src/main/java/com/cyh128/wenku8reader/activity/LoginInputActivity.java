package com.cyh128.wenku8reader.activity;


import android.content.ContentValues;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.cyh128.wenku8reader.util.NavbarStatusbarInit;
import com.cyh128.wenku8reader.util.VarTemp;
import com.cyh128.wenku8reader.util.loginWenku8;
import com.cyh128.wenku8reader.R;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.progressindicator.CircularProgressIndicatorSpec;
import com.google.android.material.progressindicator.IndeterminateDrawable;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

public class LoginInputActivity extends AppCompatActivity {
    private boolean isRemember = false;
    private TextInputLayout usernameLayout;
    private TextInputLayout passwordLayout;
    private TextInputEditText username;
    private TextInputEditText password;
    private MaterialButton signIn;
    private Button signUp;
    private String str_username;
    private String str_password;
    private CircularProgressIndicatorSpec spec;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_input);
        NavbarStatusbarInit.allTransparent(this);//界面沉浸
        spec = new CircularProgressIndicatorSpec(this, null, 0, com.google.android.material.R.style.Widget_Material3_CircularProgressIndicator_ExtraSmall);

        signIn = findViewById(R.id.confirm_login);
        signIn.setOnClickListener(v -> {
            signIn.setClickable(false);
            signIn.setIcon(IndeterminateDrawable.createCircularDrawable(this, spec));
            //获取输入的字符，判断是否正确
            str_username = username.getText().toString();
            str_password = password.getText().toString();
            if (str_username.trim().length() == 0 || str_password.trim().length() == 0) { //判断输入的内容是否为空
                usernameLayout.setError("用户名和密码不能为空");
                passwordLayout.setError("用户名和密码不能为空");

                signIn.setClickable(true);
                signIn.setIcon(null);
                return;
            }

            //尝试登录
            new Login().start();
        });

        CheckBox ckb = findViewById(R.id.rememberMe);
        ckb.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                isRemember = true;
            } else {
                isRemember = false;
            }
        });//是否保存用户名和密码

        signUp = findViewById(R.id.button_act_login_input_sign_up);
        signUp.setOnClickListener(v -> {
            new MaterialAlertDialogBuilder(this)
                    .setTitle("请前往浏览器注册")
                    .setMessage("您需要前往浏览器页面注册，注册成功后再将用户名和密码填入输入框中，点击[前往注册]以继续注册")
                    .setCancelable(false)
                    .setNegativeButton("取消",null)
                    .setPositiveButton("前往注册", (dialog, which) -> {
                        Uri uri = Uri.parse("https://www.wenku8.net/register.php");    //设置跳转的网站
                        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                        startActivity(intent);
                    })
                    .show();
        });

        usernameLayout = findViewById(R.id.textfield_username);
        passwordLayout = findViewById(R.id.textfield_password);
        username = findViewById(R.id.username);
        password = findViewById(R.id.password);

        initTextFieldListener();//负责清空错误信息
    }

    public void initTextFieldListener() { //负责清空错误信息
        username.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                usernameLayout.setError(null);
                passwordLayout.setError(null);
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        password.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                usernameLayout.setError(null);
                passwordLayout.setError(null);
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    private class Login extends Thread {
        @Override
        public void run() {
            try {
                boolean flag = loginWenku8.login(str_username, str_password);
                if (flag) {
                    Intent toMainAppUI = new Intent(LoginInputActivity.this, AppActivity.class);
                    startActivity(toMainAppUI);
                    LoginInputActivity.this.finish();
                    if (isRemember) {
                        //当设置了保存用户名和密码时，将其放入数据库中
                        VarTemp.db.execSQL("CREATE TABLE IF NOT EXISTS user_info(_id INTEGER PRIMARY KEY autoincrement,username TEXT,password TEXT)");
                        ContentValues values = new ContentValues();
                        values.put("username", str_username);
                        values.put("password", str_password);
                        VarTemp.db.insert("user_info", null, values);
                    }
                } else {
                    runOnUiThread(() -> {
                        signIn.setClickable(true);
                        signIn.setIcon(null);
                        usernameLayout.setError("用户名或密码错误");
                        passwordLayout.setError("用户名或密码错误");
                    });
                }
            } catch (Exception e) {
                runOnUiThread(() -> {
                    signIn.setClickable(true);
                    signIn.setIcon(null);
                    new MaterialAlertDialogBuilder(LoginInputActivity.this)
                            .setCancelable(false)//禁止点击其他区域
                            .setTitle("网络错误")
                            .setMessage("可能是以下原因造成的:\n\n1 -> 请检查是否正在连接VPN或代理服务器\n2 -> 未连接上网络\n3 -> 服务器(wenku8.net)出错，(此网站有时会登不上去)\n\n请稍后再试")
                            .setPositiveButton("明白", null)
                            .setIcon(R.drawable.warning)
                            .show();
                });
            }
        }
    }
}
