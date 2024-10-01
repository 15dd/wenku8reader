package com.cyh128.wenku8reader.activity

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.cyh128.wenku8reader.R
import com.cyh128.wenku8reader.fragment.TagSearchFragment
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator

class TagSearchActivity : AppCompatActivity() {
    private lateinit var viewPager2: ViewPager2
    private lateinit var tabLayout: TabLayout
    private var tag: String? = null
    private lateinit var toolbar: Toolbar
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tag_search)
        viewPager2 = findViewById(R.id.fragment_act_tag_search)
        tabLayout = findViewById(R.id.tabLayout_act_tag_search)
        toolbar = findViewById(R.id.toolbar_act_tag_search)
        tag = intent.getStringExtra("tag")
        toolbar.title = tag
        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener { v: View? ->
            // 退出当前页面
            finish()
        }
        viewPageInit()
    }

    private fun viewPageInit() {
        //https://blog.csdn.net/qq_45866344/article/details/115128445
        val adapter: ViewPager2Adapter = ViewPager2Adapter(this)
        viewPager2.adapter = adapter
        tabLayout.visibility = View.VISIBLE
        val tabLayoutMediator: TabLayoutMediator =
            TabLayoutMediator(
                tabLayout, viewPager2
            ) { tab, position ->
                when (position) {
                    0 -> tab.setText("按更新查看")
                    1 -> tab.setText("按热门查看")
                    2 -> tab.setText("只看已完结")
                    3 -> tab.setText("只看动画化")
                }
            }
        tabLayoutMediator.attach()
    }

    internal inner class ViewPager2Adapter(fragmentActivity: FragmentActivity) :
        FragmentStateAdapter(fragmentActivity) {
        override fun createFragment(position: Int): Fragment {
            when (position) {
                0 -> {
                    val v0: TagSearchFragment = TagSearchFragment()
                    val bundle: Bundle = Bundle()
                    bundle.putString("tag", tag)
                    bundle.putString("sort", "0")
                    v0.arguments = bundle
                    return v0
                }

                1 -> {
                    val v1: TagSearchFragment = TagSearchFragment()
                    val bundle: Bundle = Bundle()
                    bundle.putString("tag", tag)
                    bundle.putString("sort", "1")
                    v1.arguments = bundle
                    return v1
                }

                2 -> {
                    val v2: TagSearchFragment = TagSearchFragment()
                    val bundle: Bundle = Bundle()
                    bundle.putString("tag", tag)
                    bundle.putString("sort", "2")
                    v2.arguments = bundle
                    return v2
                }

                3 -> {
                    val v3: TagSearchFragment = TagSearchFragment()
                    val bundle: Bundle = Bundle()
                    bundle.putString("tag", tag)
                    bundle.putString("sort", "3")
                    v3.arguments = bundle
                    return v3
                }
            }
            return Fragment()
        }

        override fun getItemCount(): Int {
            return 4
        }
    }
}
