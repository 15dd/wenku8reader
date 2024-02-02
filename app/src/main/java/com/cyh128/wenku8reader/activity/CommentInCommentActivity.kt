package com.cyh128.wenku8reader.activity

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.cyh128.wenku8reader.R
import com.cyh128.wenku8reader.adapter.CommentInCommentAdapter
import com.cyh128.wenku8reader.util.Wenku8Spider.getCommentInComment
import com.google.android.material.appbar.MaterialToolbar
import me.jingbin.library.ByRecyclerView
import me.jingbin.library.ByRecyclerView.OnLoadMoreListener

class CommentInCommentActivity : AppCompatActivity() {
    private lateinit var list: ByRecyclerView
    private val allComment: MutableList<List<String>> = ArrayList()
    private var commentInCommentAdapter: CommentInCommentAdapter? = null
    private var maxindex: Int = 1
    private var pageindex: Int = 0
    private var url: String? = null
    private var emptyView: View? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_comment)
        val intent: Intent = intent
        url = intent.getStringExtra("url")
        list = findViewById(R.id.recyclerView_act_comment)
        emptyView = View.inflate(this, R.layout.view_empty_view, null)
        val toolbar: MaterialToolbar = findViewById(R.id.toolbar_act_comment)
        toolbar.title = "回复"
        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener { v: View? ->
            // 退出当前页面
            finish()
        }
        list.layoutManager = LinearLayoutManager(this)
        commentInCommentAdapter = CommentInCommentAdapter(this, allComment)
        list.adapter = commentInCommentAdapter
        Thread {
            val comment: List<List<String>>? = data
            setPageData(true, comment)
            maxindex = if (comment!!.isEmpty()) {
                1
            } else {
                comment[0][3].toInt() //设置总页数
            }
        }.start()
        list.setOnRefreshListener {
            pageindex = 0
            allComment.clear()
            commentInCommentAdapter!!.notifyDataSetChanged()
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
                    val comment: List<List<String>>? = data
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
                    commentInCommentAdapter!!.notifyItemChanged(commentInCommentAdapter!!.itemCount, commentInCommentAdapter!!.itemCount + 20)
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
                    commentInCommentAdapter!!.notifyItemChanged(commentInCommentAdapter!!.itemCount, commentInCommentAdapter!!.itemCount + 20)
                }
                list.loadMoreComplete()
            } else {
                // 没数据，显示加载到底
                list.loadMoreEnd()
            }
        }
    }

    private val data: List<List<String>>?
        get() {
            return try {
                getCommentInComment(url!!, ++pageindex)
            } catch (e: Exception) {
                pageindex--
                e.printStackTrace()
                null
            }
        }
}
