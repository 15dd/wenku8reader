package com.cyh128.wenku8reader.activity;

import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.wifi.WifiManager;
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
import com.cyh128.wenku8reader.fragment.MyinfoFragment;
import com.cyh128.wenku8reader.util.CheckUpdate;
import com.cyh128.wenku8reader.util.NetWorkReceiver;
import com.cyh128.wenku8reader.util.VarTemp;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.List;

public class AppActivity extends AppCompatActivity {
    private BottomNavigationView bottomNavigationView;
    private BookCaseFragment bookcaseFragment;
    private HomeFragment homeFragment;
    private MyinfoFragment myinfoFragment;
    private NetWorkReceiver netWorkReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_app);
        bottomNavigationView = findViewById(R.id.bottom_nav);
        bottomNavigationListener();

        Window window = getWindow();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.setDecorFitsSystemWindows(false);//底部小白条沉浸（全面屏手势）https://juejin.cn/post/6904545697552007181
        }

        //注册网络状态监听广播,判断网络是否连接==========================================================
        netWorkReceiver = new NetWorkReceiver(AppActivity.this);
        IntentFilter filter = new IntentFilter();
        filter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION);
        filter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);
        filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(netWorkReceiver, filter);
        //=========================================================================================

        //检查更新==================================================================================
        if (VarTemp.checkUpdate) {
            new Thread(() -> {
                try {
                    CheckUpdate.checkUpdate(this, CheckUpdate.WITHOUT_TIP);
                } catch (Exception e) {
                    Log.e("debug", "checkUpdate failed");
                }
            }).start();
        }
        //========================================================================================

        initFragment();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (netWorkReceiver != null) {
            unregisterReceiver(netWorkReceiver);
        }
    }

    public void initFragment() {
        homeFragment = new HomeFragment();
        bookcaseFragment = new BookCaseFragment();
        myinfoFragment = new MyinfoFragment();
        List<Fragment> fragment = new ArrayList<>();
        fragment.add(homeFragment);
        fragment.add(bookcaseFragment);
        fragment.add(myinfoFragment);
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
                switchFragment(myinfoFragment);
                return true;
            }
        });
    }
}
