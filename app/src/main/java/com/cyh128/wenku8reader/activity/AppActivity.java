package com.cyh128.wenku8reader.activity;

import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.cyh128.wenku8reader.R;
import com.cyh128.wenku8reader.fragment.BookCaseFragment;
import com.cyh128.wenku8reader.fragment.HomeFragment;
import com.cyh128.wenku8reader.fragment.MoreFragment;
import com.cyh128.wenku8reader.util.CheckUpdate;
import com.cyh128.wenku8reader.util.GlobalConfig;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.List;

public class AppActivity extends AppCompatActivity {
    private BottomNavigationView bottomNavigationView;
    private BookCaseFragment bookcaseFragment;
    private HomeFragment homeFragment;
    private MoreFragment moreFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app);

        Window window = getWindow();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.setDecorFitsSystemWindows(false);//底部小白条沉浸（全面屏手势）https://juejin.cn/post/6904545697552007181
        }

        //检查更新==================================================================================
        if (GlobalConfig.checkUpdate) {
            new Thread(() -> {
                try {
                    CheckUpdate.checkUpdate(this, CheckUpdate.Mode.WITHOUT_TIP);
                } catch (Exception e) {
                    Log.e("debug", "checkUpdate failed");
                }
            }).start();
        }
        //========================================================================================

        bottomNavigationView = findViewById(R.id.bottom_nav);
        bottomNavigationListener();

        initFragment();
    }

    @Override
    public void onBackPressed() {
        //防止内存泄漏，Android 10专用(Android 10通病)，https://issuetracker.google.com/issues/139738913
        if (isTaskRoot() && getSupportFragmentManager().getBackStackEntryCount() == 0) {
            finishAfterTransition();
        } else {
            super.onBackPressed();
        }
    }

    public void initFragment() {
        homeFragment = new HomeFragment();
        bookcaseFragment = new BookCaseFragment();
        moreFragment = new MoreFragment();

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.appFragment, homeFragment)
                .commit();
    }

    public void switchFragment(Fragment fragment) {
        //https://blog.csdn.net/AliEnCheng/article/details/108517157
        //切换fragment
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        List<Fragment> childFragments = getSupportFragmentManager().getFragments();
        for (Fragment childFragment : childFragments) {
            fragmentTransaction.hide(childFragment);
        }
        if (!childFragments.contains(fragment)) {
            fragmentTransaction.add(R.id.appFragment, fragment);
        } else {
            fragmentTransaction.show(fragment);
        }
        fragmentTransaction.commit();
    }

    public void bottomNavigationListener() {
        bottomNavigationView.setOnItemSelectedListener(item -> {
            int index = item.getItemId();
            if (index == R.id.nav_home) {
                switchFragment(homeFragment);
                return true;
            } else if (index == R.id.nav_bookcase) {
                switchFragment(bookcaseFragment);
                return true;
            } else {
                switchFragment(moreFragment);
                return true;
            }
        });
    }
}
