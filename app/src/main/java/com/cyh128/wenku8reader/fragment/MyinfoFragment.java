package com.cyh128.wenku8reader.fragment;

import static android.content.Context.MODE_PRIVATE;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import com.cyh128.wenku8reader.activity.SettingActivity;
import com.cyh128.wenku8reader.R;
import com.cyh128.wenku8reader.activity.LoginInputActivity;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

public class MyinfoFragment extends Fragment {
    private View view;
    private Button logout;
    private CardView setting;
    private CardView about;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (view == null) {
            view = inflater.inflate(R.layout.fragment_myinfo, container, false);
            logout = view.findViewById(R.id.button_logout);
            setting = view.findViewById(R.id.cardView_frag_myinfo_setting);
            about = view.findViewById(R.id.cardView_frag_myinfo_about);

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
                dialogBuilder.setPositiveButton("前往Github页面", null);
                dialogBuilder.show();
            });
        }
        return view;
    }

}
