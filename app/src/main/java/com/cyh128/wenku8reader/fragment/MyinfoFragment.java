package com.cyh128.wenku8reader.fragment;

import static android.content.Context.MODE_PRIVATE;

import android.content.DialogInterface;
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

import com.cyh128.wenku8reader.activity.SettingActivity;
import com.cyh128.wenku8reader.R;
import com.cyh128.wenku8reader.activity.LoginInputActivity;
import com.cyh128.wenku8reader.util.CheckUpdate;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.io.IOException;

public class MyinfoFragment extends Fragment {
    private View view;
    private Button logout;
    private CardView setting;
    private CardView about;
    private CardView checkUpdate;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (view == null) {
            view = inflater.inflate(R.layout.fragment_myinfo, container, false);
            logout = view.findViewById(R.id.button_logout);
            setting = view.findViewById(R.id.cardView_frag_myinfo_setting);
            about = view.findViewById(R.id.cardView_frag_myinfo_about);
            checkUpdate = view.findViewById(R.id.cardView_frag_myinfo_checkUpdate);

            logout.setOnClickListener(v -> {
                SQLiteDatabase db = getActivity().openOrCreateDatabase("info.db", MODE_PRIVATE, null);
                Cursor cursor = db.query("user_info", null, null, null, null, null, null);
                if (cursor.moveToNext()) {
                    db.execSQL("DROP TABLE user_info");
                    cursor.close();
                } else {
                    Log.d("debug", "数据库没有table");
                }
                Intent gotoinput = new Intent(getContext(), LoginInputActivity.class);
                startActivity(gotoinput);
                getActivity().finish();
            });

            setting.setOnClickListener(v -> {
                Intent toSetting = new Intent(getActivity(), SettingActivity.class);
                startActivity(toSetting);
            });
            about.setOnClickListener(v -> {
                MaterialAlertDialogBuilder dialogBuilder = new MaterialAlertDialogBuilder(getActivity());
                dialogBuilder.setView(R.layout.dialog_about);
                dialogBuilder.setCancelable(false);
                dialogBuilder.setNegativeButton("取消",null);
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
                        CheckUpdate.checkUpdate(getContext(),CheckUpdate.WITH_TIP);
                    } catch (Exception e) {
                        e.printStackTrace();
                        getActivity().runOnUiThread(() -> Toast.makeText(getContext(),"检查更新失败",Toast.LENGTH_SHORT).show());
                    }
                }).start();
            });
        }
        return view;
    }

}
