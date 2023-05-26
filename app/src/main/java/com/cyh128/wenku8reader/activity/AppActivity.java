package com.cyh128.wenku8reader.activity;

import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.wifi.WifiManager;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.cyh128.wenku8reader.R;
import com.cyh128.wenku8reader.fragment.BookCaseFragment;
import com.cyh128.wenku8reader.fragment.HomeFragment;
import com.cyh128.wenku8reader.fragment.MyinfoFragment;
import com.cyh128.wenku8reader.util.NavbarStatusbarInit;
import com.cyh128.wenku8reader.util.NetWorkReceiver;
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
        NavbarStatusbarInit.allTransparent(this);
        //findViewById(R.id.appFragment).setPadding(0,NavbarStatusbarInit.getStatusbarHeight(this),0,0);

        //注册网络状态监听广播,判断网络是否连接==========================================================
        netWorkReceiver = new NetWorkReceiver(AppActivity.this);
        IntentFilter filter = new IntentFilter();
        filter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION);
        filter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);
        filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(netWorkReceiver, filter);
        //=========================================================================================

        bottomNavigationView = findViewById(R.id.bottom_nav);
        bottomNavigationListener();

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
