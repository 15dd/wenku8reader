package com.cyh128.wenku8reader.fragment

import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.cyh128.wenku8reader.R
import com.cyh128.wenku8reader.adapter.BookCaseAdapter
import com.cyh128.wenku8reader.bean.BookcaseBean
import com.cyh128.wenku8reader.util.DatabaseHelper
import com.cyh128.wenku8reader.util.GlobalConfig
import com.cyh128.wenku8reader.util.Wenku8Spider.bookcase
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import me.jingbin.library.ByRecyclerView

class BookCaseFragment : Fragment() {
    private lateinit var list: ByRecyclerView
    private lateinit var view: View
    private var bookCaseAdapter: BookCaseAdapter? = null
    private lateinit var toolbar: Toolbar
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        super.onCreate(savedInstanceState)
        view = inflater.inflate(R.layout.fragment_bookcase, container, false)
        list = view.findViewById(R.id.frag_bookcase_booklist)
        list.isLoadMoreEnabled = false
        toolbar = view.findViewById(R.id.toolbar_bookcase)
        toolbar.setOnMenuItemClickListener { item: MenuItem ->
            if (item.itemId == R.id.update) {
                setBookcase().start()
            } else if (item.itemId == R.id.viewType) {
                GlobalConfig.bookcaseViewType = !GlobalConfig.bookcaseViewType
                changeLayout(GlobalConfig.bookcaseViewType) //更改视图类型
                //保存视图类型的设置，使下次启动自动使用当前视图类型================================
                DatabaseHelper.SaveSetting()
                //=========================================================================
            }
            true
        }
        list.setOnRefreshListener { setBookcase().start() }
        setBookcase().start()
        return view
    }

    private fun changeLayout(isChange: Boolean) {
        //布局切换方法
        //如果isChange == true 就调用瀑布流模式,反之调用列表模式
        //https://www.jianshu.com/p/f773ffb3d7e4
        if (isChange) {
            //瀑布流设置
            val layoutManager = GridLayoutManager(context, 3)
            list.layoutManager = layoutManager
            bookCaseAdapter = BookCaseAdapter(requireView().context, bookcaseList, BookCaseAdapter.GRID)
            list.adapter = bookCaseAdapter
            bookCaseAdapter!!.notifyDataSetChanged()
        } else {
            //列表模式
            val layoutManager = LinearLayoutManager(requireView().context)
            layoutManager.orientation = LinearLayoutManager.VERTICAL
            list.layoutManager = layoutManager
            bookCaseAdapter = BookCaseAdapter(requireView().context, bookcaseList, BookCaseAdapter.LINEAR)
            list.adapter = bookCaseAdapter
            bookCaseAdapter!!.notifyDataSetChanged()
        }
    }

    private inner class setBookcase : Thread() {
        override fun run() {
            try {
                /*
                https://blog.csdn.net/momoliaoliao/article/details/49559953
                特别注意：想更新列表中的数据必须向下面这么写，不能直接novelList = Wenku8Spider.getBookcase()
                否则无效，导致显示不出页面。如果是一次性数据的话，就不需要这么做了
                 */
                bookcaseList.clear()
                bookcaseList.addAll(bookcase)
            } catch (e: Exception) {
                requireActivity().runOnUiThread {
                    MaterialAlertDialogBuilder(view.context)
                        .setTitle("网络超时")
                        .setMessage("连接超时，可能是服务器出错了、也可能是网络卡慢或者您正在连接VPN或代理服务器，请稍后再试")
                        .setIcon(R.drawable.timeout)
                        .setCancelable(false)
                        .setPositiveButton("明白", null)
                        .show()
                }
                return
            }
            val msg: Message = Message()
            firstLaunchHandler.sendMessage(msg)
        }

        private val firstLaunchHandler: Handler = Handler {
            changeLayout(GlobalConfig.bookcaseViewType)
            toolbar.title = "书架(共" + bookCaseAdapter!!.itemCount + "本)"
            list.isRefreshing = false
            false
        }
    }

    companion object {
        var bookcaseList: MutableList<BookcaseBean> = ArrayList()
    }
}
