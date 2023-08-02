package com.cyh128.wenku8reader.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import com.cyh128.wenku8reader.R;
import com.cyh128.wenku8reader.activity.AboutActivity;
import com.cyh128.wenku8reader.activity.SettingActivity;
import com.cyh128.wenku8reader.activity.UserInfoActivity;

public class MoreFragment extends Fragment {
    private View view;
    private CardView setting,about,userInfo;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        view = inflater.inflate(R.layout.fragment_more, container, false);
        setting = view.findViewById(R.id.cardView_frag_myinfo_setting);
        about = view.findViewById(R.id.cardView_frag_myinfo_about);
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
            startActivity(new Intent(getActivity(), AboutActivity.class));
        });

        return view;
    }

}
