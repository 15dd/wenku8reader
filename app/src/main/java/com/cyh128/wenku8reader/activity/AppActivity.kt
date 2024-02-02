package com.cyh128.wenku8reader.activity

import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.cyh128.wenku8reader.R
import com.cyh128.wenku8reader.fragment.BookCaseFragment
import com.cyh128.wenku8reader.fragment.HomeFragment
import com.cyh128.wenku8reader.fragment.MoreFragment
import com.cyh128.wenku8reader.util.CheckUpdate
import com.cyh128.wenku8reader.util.GlobalConfig
import com.google.android.material.bottomnavigation.BottomNavigationView

class AppActivity : AppCompatActivity() {
    private var bottomNavigationView: BottomNavigationView? = null
    private var bookcaseFragment: BookCaseFragment? = null
    private var homeFragment: HomeFragment? = null
    private var moreFragment: MoreFragment? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_app)
        val window = window
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.setDecorFitsSystemWindows(false) //底部小白条沉浸（全面屏手势）https://juejin.cn/post/6904545697552007181
        }

        //检查更新==================================================================================
        if (GlobalConfig.checkUpdate) {
            Thread {
                try {
                    CheckUpdate.checkUpdate(this, CheckUpdate.Mode.WITHOUT_TIP)
                } catch (e: Exception) {
                    Log.e("debug", "checkUpdate failed")
                }
            }.start()
        }
        //========================================================================================
        bottomNavigationView = findViewById(R.id.bottom_nav)
        bottomNavigationListener()
        initFragment()
    }

    override fun onBackPressed() {
        //防止内存泄漏，Android 10专用(Android 10通病)，https://issuetracker.google.com/issues/139738913
        if (isTaskRoot && supportFragmentManager.backStackEntryCount == 0) {
            finishAfterTransition()
        } else {
            super.onBackPressed()
        }
    }

    private fun initFragment() {
        homeFragment = HomeFragment()
        bookcaseFragment = BookCaseFragment()
        moreFragment = MoreFragment()
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.appFragment, homeFragment!!)
            .commit()
    }

    private fun switchFragment(fragment: Fragment?) {
        //https://blog.csdn.net/AliEnCheng/article/details/108517157
        //切换fragment
        val fragmentTransaction = supportFragmentManager.beginTransaction()
        val childFragments = supportFragmentManager.fragments
        for (childFragment in childFragments) {
            fragmentTransaction.hide(childFragment!!)
        }
        if (!childFragments.contains(fragment)) {
            fragmentTransaction.add(R.id.appFragment, fragment!!)
        } else {
            fragmentTransaction.show(fragment!!)
        }
        fragmentTransaction.commit()
    }

    private fun bottomNavigationListener() {
        bottomNavigationView!!.setOnItemSelectedListener { item: MenuItem ->
            when (item.itemId) {
                R.id.nav_home -> {
                    switchFragment(homeFragment)
                    return@setOnItemSelectedListener true
                }
                R.id.nav_bookcase -> {
                    switchFragment(bookcaseFragment)
                    return@setOnItemSelectedListener true
                }
                else -> {
                    switchFragment(moreFragment)
                    return@setOnItemSelectedListener true
                }
            }
        }
    }
}
