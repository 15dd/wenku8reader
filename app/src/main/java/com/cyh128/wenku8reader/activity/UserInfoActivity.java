package com.cyh128.wenku8reader.activity;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.cyh128.wenku8reader.R;
import com.cyh128.wenku8reader.util.LoginWenku8;
import com.cyh128.wenku8reader.util.Wenku8Spider;
import com.google.android.material.appbar.MaterialToolbar;

import java.io.IOException;
import java.util.List;

public class UserInfoActivity extends AppCompatActivity {
    private ImageView avatar;
    private TextView userID, userName, userLevel, email, signUpDate, contribution, experience, score, maxBookcase, maxRecommed;
    private Button logout;
    private List<String> userInfo;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_userinfo);
        avatar = findViewById(R.id.image_act_userinfo);
        userID = findViewById(R.id.text_act_userinfo_userID);
        userName = findViewById(R.id.text_act_userinfo_userName);
        userLevel = findViewById(R.id.text_act_userinfo_userLevel);
        email = findViewById(R.id.text_act_userinfo_email);
        signUpDate = findViewById(R.id.text_act_userinfo_signUpDate);
        contribution = findViewById(R.id.text_act_userinfo_contribution);
        experience = findViewById(R.id.text_act_userinfo_experience);
        score = findViewById(R.id.text_act_userinfo_score);
        maxBookcase = findViewById(R.id.text_act_userinfo_maxBookcase);
        maxRecommed = findViewById(R.id.text_act_userinfo_maxRecommend);
        logout = findViewById(R.id.button_act_userinfo_logout);

        //设置logout按钮的背景色为红色
        TypedValue typedValue = new TypedValue();
        getTheme().resolveAttribute(com.google.android.material.R.attr.colorError, typedValue, true);
        logout.setBackgroundColor(typedValue.data);

        MaterialToolbar toolbar = findViewById(R.id.toolbar_act_userinfo);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(v -> {
            // 退出当前页面
            finish();
        });

        logout.setOnClickListener(v -> {
            SQLiteDatabase db = openOrCreateDatabase("info.db", MODE_PRIVATE, null);
            Cursor cursor = db.query("user_info", null, null, null, null, null, null);
            if (cursor.moveToNext()) {
                db.execSQL("DROP TABLE user_info");
                cursor.close();
            } else {
                Log.d("debug", "数据库没有table");
            }
            Intent intent = new Intent(this, LoginInputActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        });

        new Thread(() -> {
            try {
                userInfo = Wenku8Spider.getUserInfo();
                setData();
            } catch (IOException e) {
                e.printStackTrace();
                throw new RuntimeException(e);
            }
        }).start();
    }

    private void setData() {
        runOnUiThread(() -> {
            Glide.with(UserInfoActivity.this).load(userInfo.get(0)).into(avatar);
            userID.setText(userInfo.get(1));
            userName.setText(userInfo.get(2));
            userLevel.setText(userInfo.get(3));
            email.setText(userInfo.get(4));
            signUpDate.setText(userInfo.get(5));
            contribution.setText(userInfo.get(6));
            experience.setText(userInfo.get(7));
            score.setText(userInfo.get(8));
            maxBookcase.setText(userInfo.get(9));
            maxRecommed.setText(userInfo.get(10));
        });
    }
}
