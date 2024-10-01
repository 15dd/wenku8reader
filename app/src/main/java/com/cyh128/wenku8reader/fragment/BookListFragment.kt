package com.cyh128.wenku8reader.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.cyh128.wenku8reader.R
import com.cyh128.wenku8reader.adapter.BookListAdapter
import com.cyh128.wenku8reader.bean.BookListBean
import com.cyh128.wenku8reader.util.Wenku8Spider.getNovelByType
import me.jingbin.library.ByRecyclerView
import me.jingbin.library.ByRecyclerView.OnLoadMoreListener

class BookListFragment : Fragment() {
    private lateinit var list: ByRecyclerView
    private var pageindex: Int = 0 //上拉加载数据用，每上拉一次，索引值加1
    private var maxindex: Int = 1
    private val novelList: MutableList<BookListBean> = ArrayList()
    private lateinit var view: View
    private lateinit var emptyView: View
    private var bookType: String? = null
    private var bookListAdapter: BookListAdapter? = null
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        super.onCreate(savedInstanceState)
        view = inflater.inflate(R.layout.fragment_booklist, container, false)
        bookType = requireArguments().getString("type")
        list = view.findViewById(R.id.booklist)
        emptyView = inflater.inflate(R.layout.view_empty_view, container, false)
        list.layoutManager = LinearLayoutManager(view.context)
        bookListAdapter = BookListAdapter(view.context, novelList)
        list.adapter = bookListAdapter
        Thread {
            val bookListBeans: List<BookListBean>? = data
            setPageData(true, bookListBeans)
            if (!bookListBeans.isNullOrEmpty()) maxindex =
                bookListBeans[0].totalPage //设置总页数
        }.start()
        list.setOnRefreshListener {
            pageindex = 0
            novelList.clear()
            bookListAdapter!!.notifyDataSetChanged()
            Thread { setPageData(true, data) }.start()
            list.isRefreshing = false
        }
        list.setOnLoadMoreListener(object : OnLoadMoreListener {
            override fun onLoadMore() {
                if (pageindex == maxindex) {
                    requireActivity().runOnUiThread { list.loadMoreEnd() }
                    return
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
        })
        return view
    }

    private fun setPageData(isFirstPage: Boolean, data: List<BookListBean>?) {
        if (activity == null) { //防止切换浅色或深色模式时崩溃
            return
        }
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
                getNovelByType(bookType, ++pageindex)
            } catch (e: Exception) {
                pageindex--
                e.printStackTrace()
                null
            }
        }
}
