package com.cyh128.wenku8reader.fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.cyh128.wenku8reader.R
import com.cyh128.wenku8reader.activity.SearchActivity
import com.cyh128.wenku8reader.activity.TagSelectActivity
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator

class HomeFragment : Fragment() {
    //private BookListFragment toplist, lastupdate, allvote, postdate, articlelist ,dayvisit,dayvote;
    private lateinit var view: View
    private lateinit var viewPager2: ViewPager2
    private lateinit var tabLayout: TabLayout
    private lateinit var toolbar: Toolbar
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        super.onCreate(savedInstanceState)
        view = inflater.inflate(R.layout.fragment_home, container, false)
        viewPager2 = view.findViewById(R.id.fragment_home_content)
        //        viewPager2.setUserInputEnabled(false); //true:滑动，false：禁止滑动
        tabLayout = view.findViewById(R.id.tabLayout_home)
        tabLayout.tabMode = TabLayout.MODE_SCROLLABLE
        toolbar = view.findViewById(R.id.toolbar_home)
        toolbar.setOnMenuItemClickListener { item: MenuItem ->
            if (item.itemId == R.id.btn_search) {
                val intent: Intent = Intent(activity, SearchActivity::class.java)
                startActivity(intent)
                return@setOnMenuItemClickListener true
            } else if (item.itemId == R.id.btn_tagSearch) {
                val intent: Intent = Intent(activity, TagSelectActivity::class.java)
                startActivity(intent)
                return@setOnMenuItemClickListener true
            }
            false
        }
        viewPageInit()
        return view
    }

    private fun viewPageInit() {
        //https://blog.csdn.net/qq_45866344/article/details/115128445
        val adapter: ViewPager2Adapter = ViewPager2Adapter(this)
        viewPager2.adapter = adapter
        val tabLayoutMediator: TabLayoutMediator =
            TabLayoutMediator(tabLayout, viewPager2
            ) { tab, position ->
                when (position) {
                    0 -> tab.setText("总排行榜")
                    1 -> tab.setText("总推荐榜")
                    2 -> tab.setText("月排行榜")
                    3 -> tab.setText("月推荐榜")
                    4 -> tab.setText("周排行榜")
                    5 -> tab.setText("周推荐榜")
                    6 -> tab.setText("日排行榜")
                    7 -> tab.setText("日推荐榜")
                    8 -> tab.setText("最新入库")
                    9 -> tab.setText("最近更新")
                    10 -> tab.setText("总收藏榜")
                    11 -> tab.setText("字数排行")
                    12 -> tab.setText("全部")
                }
            }
        tabLayoutMediator.attach()
    }

    internal inner class ViewPager2Adapter(fragment: Fragment) : FragmentStateAdapter(fragment) {
        override fun createFragment(position: Int): Fragment {
            when (position) {
                0 -> {
                    val toplist: BookListFragment = BookListFragment()
                    val bundle: Bundle = Bundle()
                    bundle.putString("type", "toplist")
                    toplist.arguments = bundle
                    return toplist
                }

                1 -> {
                    val allvote: BookListFragment = BookListFragment()
                    val bundle: Bundle = Bundle()
                    bundle.putString("type", "allvote")
                    allvote.arguments = bundle
                    return allvote
                }

                2 -> {
                    val monthvisit: BookListFragment = BookListFragment()
                    val bundle: Bundle = Bundle()
                    bundle.putString("type", "monthvisit")
                    monthvisit.arguments = bundle
                    return monthvisit
                }

                3 -> {
                    val monthvote: BookListFragment = BookListFragment()
                    val bundle: Bundle = Bundle()
                    bundle.putString("type", "monthvote")
                    monthvote.arguments = bundle
                    return monthvote
                }

                4 -> {
                    val weekvisit: BookListFragment = BookListFragment()
                    val bundle: Bundle = Bundle()
                    bundle.putString("type", "weekvisit")
                    weekvisit.arguments = bundle
                    return weekvisit
                }

                5 -> {
                    val weekvote: BookListFragment = BookListFragment()
                    val bundle: Bundle = Bundle()
                    bundle.putString("type", "weekvote")
                    weekvote.arguments = bundle
                    return weekvote
                }

                6 -> {
                    val dayvisit: BookListFragment = BookListFragment()
                    val bundle: Bundle = Bundle()
                    bundle.putString("type", "dayvisit")
                    dayvisit.arguments = bundle
                    return dayvisit
                }

                7 -> {
                    val dayvote: BookListFragment = BookListFragment()
                    val bundle: Bundle = Bundle()
                    bundle.putString("type", "dayvote")
                    dayvote.arguments = bundle
                    return dayvote
                }

                8 -> {
                    val postdate: BookListFragment = BookListFragment()
                    val bundle: Bundle = Bundle()
                    bundle.putString("type", "postdate")
                    postdate.arguments = bundle
                    return postdate
                }

                9 -> {
                    val lastupdate: BookListFragment = BookListFragment()
                    val bundle: Bundle = Bundle()
                    bundle.putString("type", "lastupdate")
                    lastupdate.arguments = bundle
                    return lastupdate
                }

                10 -> {
                    val goodnum: BookListFragment = BookListFragment()
                    val bundle: Bundle = Bundle()
                    bundle.putString("type", "goodnum")
                    goodnum.arguments = bundle
                    return goodnum
                }

                11 -> {
                    val size: BookListFragment = BookListFragment()
                    val bundle: Bundle = Bundle()
                    bundle.putString("type", "size")
                    size.arguments = bundle
                    return size
                }

                12 -> {
                    val articlelist: BookListFragment = BookListFragment()
                    val bundle: Bundle = Bundle()
                    bundle.putString("type", "articlelist")
                    articlelist.arguments = bundle
                    return articlelist
                }
            }
            return Fragment()
        }

        override fun getItemCount(): Int {
            return 13
        }
    }
}
