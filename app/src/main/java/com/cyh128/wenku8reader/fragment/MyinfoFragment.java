package com.cyh128.wenku8reader.fragment;

import static android.content.Context.MODE_PRIVATE;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import com.cyh128.wenku8reader.R;
import com.cyh128.wenku8reader.activity.LoginInputActivity;
import com.cyh128.wenku8reader.activity.SettingActivity;
import com.cyh128.wenku8reader.activity.UserInfoActivity;
import com.cyh128.wenku8reader.util.CheckUpdate;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

public class MyinfoFragment extends Fragment {
    private View view;
    private CardView setting,about,checkUpdate,userInfo;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        view = inflater.inflate(R.layout.fragment_myinfo, container, false);
        setting = view.findViewById(R.id.cardView_frag_myinfo_setting);
        about = view.findViewById(R.id.cardView_frag_myinfo_about);
        checkUpdate = view.findViewById(R.id.cardView_frag_myinfo_checkUpdate);
        userInfo = view.findViewById(R.id.cardView_frag_myinfo_userinfo);

        userInfo.setOnClickListener(v -> {
            Intent toUserInfo = new Intent(getActivity(), UserInfoActivity.class);
            startActivity(toUserInfo);
        });

        setting.setOnClickListener(v -> {
            Intent toSetting = new Intent(getActivity(), SettingActivity.class);
            startActivity(toSetting);
        });
        about.setOnClickListener(v -> {
            MaterialAlertDialogBuilder dialogBuilder = new MaterialAlertDialogBuilder(getActivity());
            dialogBuilder.setView(R.layout.dialog_about);
            dialogBuilder.setCancelable(false);
            dialogBuilder.setNegativeButton("取消", null);
            dialogBuilder.setPositiveButton("前往Github页面", (dialog, which) -> {
                Uri uri = Uri.parse("https://github.com/15dd/wenku8reader");    //设置跳转的网站
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
            });
            dialogBuilder.show();
        });
        checkUpdate.setOnClickListener(v -> {
            new Thread(() -> {
                try {
                    CheckUpdate.checkUpdate(getContext(), CheckUpdate.WITH_TIP);
                } catch (Exception e) {
                    e.printStackTrace();
                    getActivity().runOnUiThread(() -> Toast.makeText(getContext(), "检查更新失败", Toast.LENGTH_SHORT).show());
                }
            }).start();
        });

        return view;
    }

}
