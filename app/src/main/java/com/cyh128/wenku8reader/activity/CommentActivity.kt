package com.cyh128.wenku8reader.activity

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.cyh128.wenku8reader.R
import com.cyh128.wenku8reader.adapter.CommentAdapter
import com.cyh128.wenku8reader.util.Wenku8Spider.getComment
import com.google.android.material.appbar.MaterialToolbar
import me.jingbin.library.ByRecyclerView
import me.jingbin.library.ByRecyclerView.OnLoadMoreListener

class CommentActivity : AppCompatActivity() {
    private lateinit var list: ByRecyclerView
    private val allComment: MutableList<List<String>> = ArrayList()
    private var commentAdapter: CommentAdapter? = null
    private var maxindex = 1
    private var pageindex = 0
    private var url: String? = null
    private var emptyView: View? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_comment)
        val intent = intent
        url = intent.getStringExtra("url")
        list = findViewById(R.id.recyclerView_act_comment)
        emptyView = View.inflate(this, R.layout.view_empty_view, null)
        val toolbar = findViewById<MaterialToolbar>(R.id.toolbar_act_comment)
        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener { v: View? ->
            // 退出当前页面
            finish()
        }
        list.layoutManager = LinearLayoutManager(this)
        commentAdapter = CommentAdapter(this, allComment)
        list.adapter = commentAdapter
        Thread {
            val comment = data
            setPageData(true, comment)
            maxindex = if (comment!!.isEmpty()) {
                1
            } else {
                comment[0][0].toInt() //设置总页数
            }
        }.start()
        list.setOnRefreshListener {
            pageindex = 0
            allComment.clear()
            commentAdapter!!.notifyDataSetChanged()
            Thread { setPageData(true, data) }.start()
            list.isRefreshing = false
        }
        list.setOnLoadMoreListener(object : OnLoadMoreListener {
            override fun onLoadMore() {
                if (pageindex == maxindex) {
                    runOnUiThread { list.loadMoreEnd() }
                    return
                }
                Thread(Runnable {
                    val comment = data
                    if (comment == null) {
                        runOnUiThread { list.loadMoreFail() }
                        return@Runnable
                    }
                    setPageData(true, comment)
                    runOnUiThread { list.loadMoreComplete() }
                }).start()
            }
        })
    }

    private fun setPageData(isFirstPage: Boolean, data: List<List<String>>?) {
        if (isFirstPage) {
            // 第一页
            if (!data.isNullOrEmpty()) {
                // 有数据
                list.isStateViewEnabled = false
                list.isLoadMoreEnabled = true
                allComment.addAll(data)
                runOnUiThread {
                    commentAdapter!!.notifyItemChanged(
                        commentAdapter!!.itemCount, commentAdapter!!.itemCount + 20
                    )
                }
            } else {
                // 没数据，设置空布局
                runOnUiThread {
                    list.setStateView(emptyView)
                    list.isLoadMoreEnabled = false
                }
            }
        } else {
            // 第二页
            if (!data.isNullOrEmpty()) {
                // 有数据，显示更多数据
                allComment.addAll(data)
                runOnUiThread {
                    commentAdapter!!.notifyItemChanged(
                        commentAdapter!!.itemCount, commentAdapter!!.itemCount + 20
                    )
                }
                list.loadMoreComplete()
            } else {
                // 没数据，显示加载到底
                list.loadMoreEnd()
            }
        }
    }

    private val data: List<List<String>>?
        get() = try {
            getComment(url!!, ++pageindex)
        } catch (e: Exception) {
            pageindex--
            e.printStackTrace()
            null
        }
}
