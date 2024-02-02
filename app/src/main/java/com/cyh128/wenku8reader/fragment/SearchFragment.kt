package com.cyh128.wenku8reader.fragment

import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.cyh128.wenku8reader.R
import com.cyh128.wenku8reader.activity.ContentsActivity
import com.cyh128.wenku8reader.activity.SearchActivity
import com.cyh128.wenku8reader.adapter.BookListAdapter
import com.cyh128.wenku8reader.bean.BookListBean
import com.cyh128.wenku8reader.util.GlobalConfig
import com.cyh128.wenku8reader.util.Wenku8Spider.searchNovel
import com.google.android.material.snackbar.Snackbar
import me.jingbin.library.ByRecyclerView

class SearchFragment : Fragment() {
    private lateinit var list: ByRecyclerView
    private var pageindex: Int = 0 //上拉加载数据用，每上拉一次，索引值加1
    private var maxindex: Int = 1
    private var novelList: MutableList<BookListBean> = ArrayList()
    private lateinit var view: View
    private lateinit var emptyView: View
    private var searchText: String? = null
    private var bookListAdapter: BookListAdapter? = null
    private lateinit var novelTitle: RadioButton
    private lateinit var authorName: RadioButton
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        super.onCreate(savedInstanceState)
        view = inflater.inflate(R.layout.fragment_booklist, container, false)
        emptyView = inflater.inflate(R.layout.view_empty_view, container, false)
        searchText = requireArguments().getString("searchText")
        list = view.findViewById(R.id.booklist)
        novelTitle = requireActivity().findViewById(R.id.radiobutton_act_search_noveltitle)
        authorName = requireActivity().findViewById(R.id.radiobutton_act_search_authorname)
        novelList = ArrayList()
        list.layoutManager = LinearLayoutManager(view.context)
        bookListAdapter = BookListAdapter(view.context, novelList)
        list.adapter = bookListAdapter
        Thread(Runnable {
            if (!GlobalConfig.isFiveSecondDone) {
                list.loadMoreFail()
                return@Runnable
            }
            val bookListBeans: List<BookListBean>? = data
            if (bookListBeans!!.size == 1) {
                val url: String = bookListBeans[0].bookUrl
                val intent: Intent = Intent(activity, ContentsActivity::class.java)
                intent.putExtra("bookUrl", url)
                startActivity(intent)
                return@Runnable
            }
            if (bookListBeans.isNotEmpty()) {
                maxindex = bookListBeans[0].totalPage //设置总页数
            }
            setPageData(true, bookListBeans)
        }).start()
        list.setOnRefreshListener {
            if (!GlobalConfig.isFiveSecondDone) {
                Snackbar.make(view, "因网站限制，请等待5秒之后再重新尝试", Snackbar.LENGTH_SHORT)
                    .setAction("好的") { v: View? -> }
                    .show()
                list.isRefreshing = false
                return@setOnRefreshListener
            }
            pageindex = 0
            novelList.clear()
            bookListAdapter!!.notifyDataSetChanged()
            Thread { setPageData(true, data) }.start()
            list.isRefreshing = false
        }
        list.setOnLoadMoreListener {
            if (pageindex == maxindex) {
                list.loadMoreEnd()
                return@setOnLoadMoreListener
            }
            if (!GlobalConfig.isFiveSecondDone) {
                Snackbar.make(view, "因网站限制，请等待5秒之后再重新尝试", Snackbar.LENGTH_SHORT)
                    .setAction("好的") { v: View? -> null }
                    .show()
                list.loadMoreFail()
                return@setOnLoadMoreListener
            }
            Thread(Runnable {
                val bookListBeans: List<BookListBean>? = data
                if (bookListBeans == null) {
                    requireActivity().runOnUiThread { list.loadMoreFail() }
                    return@Runnable
                }
                setPageData(true, bookListBeans)
                requireActivity().runOnUiThread { list.loadMoreComplete() }
            }).start()
        }
        return view
    }

    private fun setPageData(isFirstPage: Boolean, data: List<BookListBean>?) {
        if (isFirstPage) {
            // 第一页
            if (!data.isNullOrEmpty()) {
                // 有数据
                list.isStateViewEnabled = false
                list.isLoadMoreEnabled = true
                novelList.addAll(data)
                requireActivity().runOnUiThread {
                    bookListAdapter!!.notifyItemChanged(
                        bookListAdapter!!.itemCount, bookListAdapter!!.itemCount + 20
                    )
                }
            } else {
                // 没数据，设置空布局
                requireActivity().runOnUiThread {
                    list.setStateView(emptyView)
                    list.isLoadMoreEnabled = false
                    bookListAdapter!!.notifyDataSetChanged()
                }
            }
        } else {
            // 第二页
            if (!data.isNullOrEmpty()) {
                // 有数据，显示更多数据
                novelList.addAll(data)
                requireActivity().runOnUiThread {
                    bookListAdapter!!.notifyItemChanged(
                        bookListAdapter!!.itemCount, bookListAdapter!!.itemCount + 20
                    )
                }
                list.loadMoreComplete()
            }
        }
    }

    private val data: List<BookListBean>?
        get() {
            return try {
                requireActivity().runOnUiThread { waitFiveSecond() }
                if (novelTitle.isChecked) {
                    println("checked")
                }
                val mode: String = if (novelTitle.isChecked) "articlename" else "author"
                searchNovel(mode, searchText, ++pageindex)
            } catch (e: Exception) {
                pageindex--
                e.printStackTrace()
                null
            }
        }

    private fun waitFiveSecond() {
        GlobalConfig.isFiveSecondDone = false
        SearchActivity.searchFlag =
            false //下滑操作也会触发搜索小说的5秒等待机制，所以需要将搜索框的搜索也加入限制，即下滑操作或者搜索小说的五秒没过，不允许操作
        object : CountDownTimer(5500, 1000) {
            override fun onTick(millisUntilFinished: Long) {}
            override fun onFinish() {
                GlobalConfig.isFiveSecondDone = true
                SearchActivity.searchFlag = true
            }
        }.start()
    }
}
