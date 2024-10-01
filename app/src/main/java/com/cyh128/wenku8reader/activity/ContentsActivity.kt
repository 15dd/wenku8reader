package com.cyh128.wenku8reader.activity

import android.content.DialogInterface
import android.content.Intent
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.text.Html
import android.util.Log
import android.util.TypedValue
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.NestedScrollView
import androidx.recyclerview.widget.LinearLayoutManager
import chen.you.expandable.ExpandableRecyclerView
import com.bumptech.glide.Glide
import com.ctetin.expandabletextviewlibrary.ExpandableTextView
import com.cyh128.wenku8reader.R
import com.cyh128.wenku8reader.adapter.ContentsListAdapter
import com.cyh128.wenku8reader.bean.ContentsCcssBean
import com.cyh128.wenku8reader.bean.ContentsVcssBean
import com.cyh128.wenku8reader.fragment.BookCaseFragment
import com.cyh128.wenku8reader.newReader.ReaderActivity
import com.cyh128.wenku8reader.util.GlobalConfig
import com.cyh128.wenku8reader.util.Wenku8Spider.addBook
import com.cyh128.wenku8reader.util.Wenku8Spider.bookVote
import com.cyh128.wenku8reader.util.Wenku8Spider.bookcase
import com.cyh128.wenku8reader.util.Wenku8Spider.getContents
import com.cyh128.wenku8reader.util.Wenku8Spider.getNovelDetail
import com.cyh128.wenku8reader.util.Wenku8Spider.removeBook
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.button.MaterialButton
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
import com.google.android.material.progressindicator.LinearProgressIndicator
import com.google.android.material.snackbar.Snackbar
import java.io.IOException

class ContentsActivity : AppCompatActivity() {
    private var contentsList: List<Any> = ArrayList()
    private var novelDetail: List<String> = ArrayList()
    private lateinit var title: TextView
    private lateinit var author: TextView
    private lateinit var status: TextView
    private lateinit var update: TextView
    private lateinit var popular: TextView
    private lateinit var tag: TextView
    private lateinit var anime: TextView
    private lateinit var warning: TextView
    private lateinit var introduce: ExpandableTextView
    private lateinit var imageView: ImageView
    private lateinit var expandableRecyclerView: ExpandableRecyclerView
    private var contentsListAdapter: ContentsListAdapter? = null
    private lateinit var myNestedScrollView: NestedScrollView
    private lateinit var mainLayout: View
    private lateinit var warningCardView: View
    private lateinit var linearProgressIndicator: LinearProgressIndicator
    private lateinit var addToBookcase: MaterialButton
    private lateinit var commentButton: Button
    private lateinit var webButton: Button
    private lateinit var recommendButton: Button
    private var inBookcase = false
    private var bid = 0
    private var aid = 0
    private lateinit var fab: ExtendedFloatingActionButton
    private var removeDraw: Drawable? = null
    private var addDraw: Drawable? = null
    private var isAddOrRemoveDone = true //防止在添加或移出书架的操作在未完成的情况下再次点击按钮
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_contents)
        val intent = intent
        bookUrl = intent.getStringExtra("bookUrl")
        removeDraw = resources.getDrawable(R.drawable.remove, null)
        addDraw = resources.getDrawable(R.drawable.add, null)
        fab = findViewById(R.id.fab_act_contents)
        fab.hide()
        mainLayout = findViewById(R.id.linearLayout_act_contents)
        mainLayout.visibility = View.INVISIBLE
        linearProgressIndicator = findViewById(R.id.progress_act_contents)
        addToBookcase = findViewById(R.id.button_act_contents_addToBookcase)
        commentButton = findViewById(R.id.button_act_contents_comment)
        webButton = findViewById(R.id.button_act_contents_web)
        recommendButton = findViewById(R.id.button_act_contents_recommend)
        myNestedScrollView = findViewById(R.id.myscrollview_act_contents)
        introduce = findViewById(R.id.text_act_contents_introduce)
        warningCardView = findViewById(R.id.cardView_act_contents_warning)
        warningCardView.visibility = View.GONE
        title = findViewById(R.id.text_act_contents_title)
        author = findViewById(R.id.text_act_contents_author)
        status = findViewById(R.id.text_act_contents_status)
        update = findViewById(R.id.text_act_contents_update)
        imageView = findViewById(R.id.image_act_contents)
        expandableRecyclerView = findViewById(R.id.recyclerView_act_contents)
        tag = findViewById(R.id.text_act_contents_tag)
        popular = findViewById(R.id.text_act_contents_popular)
        anime = findViewById(R.id.text_act_contents_anime)
        warning = findViewById(R.id.text_act_contents_warning)
        val toolbar = findViewById<MaterialToolbar>(R.id.toolbar_act_contents)
        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener { v: View? ->
            // 退出当前页面
            finish()
        }
        webButton.setOnClickListener { v: View? ->
            val uri = Uri.parse(bookUrl) //设置跳转的网站
            val intent1 = Intent(Intent.ACTION_VIEW, uri)
            startActivity(intent1)
        }
        addToBookcase.setOnClickListener { v: View? ->  //加入书架或移出书架
            if (!isAddOrRemoveDone) {
                val snackbar = Snackbar.make(title, "点击间隔过短，请稍后重试", Snackbar.LENGTH_SHORT)
                snackbar.setAnchorView(fab)
                snackbar.show()
                return@setOnClickListener
            }
            isAddOrRemoveDone = false
            val isChecked = addToBookcase.text.toString() == "加入书架"
            if (isChecked) {
                Thread(Runnable {
                    try {
                        if (!addBook(aid)) {
                            runOnUiThread {
                                MaterialAlertDialogBuilder(this@ContentsActivity)
                                    .setTitle("添加失败")
                                    .setMessage("可能的原因有:\n1 -> 您已经超过了最大收藏限制，无法添加\n2 -> 该书已经在书架内，请退出页面刷新书架然后重试")
                                    .setIcon(R.drawable.warning)
                                    .setCancelable(false)
                                    .setPositiveButton("明白", null)
                                    .show()
                            }
                            isAddOrRemoveDone = true
                            return@Runnable
                        }
                        BookCaseFragment.bookcaseList = bookcase
                        runOnUiThread {
                            addToBookcase.icon = removeDraw
                            addToBookcase.text = "移出书架"
                            val typedValue = TypedValue() //获取 [?attr/] 的颜色
                            theme.resolveAttribute(
                                com.google.android.material.R.attr.colorError,
                                typedValue,
                                true
                            )
                            addToBookcase.setBackgroundColor(typedValue.data)
                            isAddOrRemoveDone = true
                        }
                    } catch (e: Exception) {
                        runOnUiThread {
                            MaterialAlertDialogBuilder(this@ContentsActivity)
                                .setTitle("网络超时")
                                .setMessage("连接超时，可能是服务器出错了、也可能是网络卡慢或者您正在连接VPN或代理服务器，请稍后再试")
                                .setIcon(R.drawable.timeout)
                                .setCancelable(false)
                                .setPositiveButton("明白") { dialog: DialogInterface?, which: Int -> finish() }
                                .show()
                            isAddOrRemoveDone = true
                        }
                    }
                }).start()
            } else {
                Thread {
                    try {
                        getBid() //因为移出过书架，书的delid(bid)会变，比如之前是delid=8843659，删除过就变成了delid=8843661，所以在删除之前需要先重新获取一下书架，然后读取新的delid(bid)
                        removeBook(bid)
                        BookCaseFragment.bookcaseList = bookcase
                        runOnUiThread {
                            addToBookcase.icon = addDraw
                            addToBookcase.text = "加入书架"
                            val typedValue = TypedValue() //获取 [?attr/] 的颜色
                            theme.resolveAttribute(
                                com.google.android.material.R.attr.colorPrimary,
                                typedValue,
                                true
                            )
                            addToBookcase.setBackgroundColor(typedValue.data)
                            isAddOrRemoveDone = true
                        }
                    } catch (e: Exception) {
                        runOnUiThread {
                            MaterialAlertDialogBuilder(this@ContentsActivity)
                                .setTitle("网络超时")
                                .setMessage("连接超时，可能是服务器出错了、也可能是网络卡慢或者您正在连接VPN或代理服务器，请稍后再试")
                                .setIcon(R.drawable.timeout)
                                .setCancelable(false)
                                .setPositiveButton("明白") { dialog: DialogInterface?, which: Int -> finish() }
                                .show()
                            isAddOrRemoveDone = true
                        }
                    }
                }.start()
            }
        }
        Thread {
            try {
                novelDetail = getNovelDetail(bookUrl!!) //小说详情
                contentsList = getContents(bookUrl!!) //小说目录

                //获取这本书的aid和bid====================================================================================================
                //aid获取 https://www.wenku8.net/book/xxxx.htm
                aid = bookUrl!!.substring(bookUrl!!.indexOf("book/") + 5, bookUrl!!.indexOf(".htm"))
                    .toInt()
                //bid获取 https://www.wenku8.net/book/xxxx.htm 如果url没有bid,只有aid,那么就根据它的aid在书架中找到它对应的bid。删除必须用bid
                getBid()
                //end===================================================================================================================
                isInBookcase() //判断这本书是否已在书架中
                otherButtonListener() //按钮监听
                val msg = Message()
                setNovelInfo.sendMessage(msg)
            } catch (e: Exception) {
                e.printStackTrace()
                runOnUiThread {
                    MaterialAlertDialogBuilder(this@ContentsActivity)
                        .setTitle("网络超时")
                        .setMessage("连接超时，可能是服务器出错了、也可能是网络卡慢或者您正在连接VPN或代理服务器，请稍后再试")
                        .setIcon(R.drawable.timeout)
                        .setCancelable(false)
                        .setPositiveButton("明白") { dialog: DialogInterface?, which: Int -> finish() }
                        .show()
                }
            }
        }.start()
        fab.setOnClickListener { v: View? -> isHaveHistory }
        myNestedScrollView.setOnScrollChangeListener { v, scrollX, scrollY, oldScrollX, oldScrollY ->
            if (scrollY - oldScrollY > 0) {
                if (fab.isExtended) {
                    fab.shrink()
                }
            } else if (scrollY == myNestedScrollView.bottom) {
                if (fab.isExtended) {
                    fab.shrink()
                }
            } else if (scrollY == myNestedScrollView.top) {
                if (!fab.isExtended) {
                    fab.extend()
                }
            } else {
                if (!fab.isExtended) {
                    fab.extend()
                }
            }
        }
    }

    @Throws(IOException::class)
    private fun getBid() {
        //https://www.wenku8.net/book/xxxx.htm 如果url没有bid,只有aid,那么就根据它的aid在书架中找到它对应的bid。因为删除必须用bid
        BookCaseFragment.bookcaseList = bookcase
        for (bcc in BookCaseFragment.bookcaseList) {
            if (bcc.aid.toInt() == aid) {
                bid = bcc.bid.toInt()
                break
            }
        }
        Log.d("debug", "当前bookUrl:$bookUrl bid:$bid")
    }

    private fun otherButtonListener() {
        commentButton.setOnClickListener { v: View? ->
            val intent = Intent(this@ContentsActivity, CommentActivity::class.java)
            intent.putExtra("url", novelDetail[6])
            startActivity(intent)
        }
        recommendButton.setOnClickListener { v: View? ->
            Thread {
                try {
                    val result = bookVote(aid)
                    runOnUiThread {
                        MaterialAlertDialogBuilder(this@ContentsActivity)
                            .setMessage(result)
                            .setCancelable(false)
                            .setPositiveButton("明白", null)
                            .show()
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                    runOnUiThread {
                        MaterialAlertDialogBuilder(this@ContentsActivity)
                            .setTitle("网络超时")
                            .setMessage("连接超时，可能是服务器出错了、也可能是网络卡慢或者您正在连接VPN或代理服务器，请稍后再试")
                            .setIcon(R.drawable.timeout)
                            .setCancelable(false)
                            .setPositiveButton("明白", null)
                            .show()
                    }
                }
            }.start()
        }
        imageView.setOnClickListener { v: View? ->
            val intent1 = Intent(this@ContentsActivity, PhotoViewActivity::class.java)
            intent1.putExtra("url", novelDetail[4])
            startActivity(intent1)
        }
    }

    private fun isInBookcase() { //判断是否已在书架中，根据这本书的aid是否跟书架中的任意一本的aid相同，如果相同，那么这本书就已经在书架了
        if (BookCaseFragment.bookcaseList.size == 0) { //如果书架为空时，这种情况一般发生在用户没有点击书架页或者没有收藏的情况下
            try {
                BookCaseFragment.bookcaseList = bookcase
            } catch (e: Exception) {
                e.printStackTrace()
                runOnUiThread {
                    MaterialAlertDialogBuilder(this@ContentsActivity)
                        .setTitle("网络超时")
                        .setMessage("连接超时，可能是服务器出错了、也可能是网络卡慢或者您正在连接VPN或代理服务器，请稍后再试")
                        .setIcon(R.drawable.timeout)
                        .setCancelable(false)
                        .setPositiveButton("明白") { dialog: DialogInterface?, which: Int -> finish() }
                        .show()
                }
                return
            }
            if (BookCaseFragment.bookcaseList.size == 0) { //如果再一次获取目录，但size还是为0时，那么返回
                Log.d("debug", "bookcaselist size -> 0")
                inBookcase = false
                return
            }
        }
        for (bcc in BookCaseFragment.bookcaseList) {
            if (bcc.aid.toInt() == aid) {
                inBookcase = true
                return
            }
        }
        inBookcase = false
    }

    private val isHaveHistory: Unit
        get() {
            val sql = if (GlobalConfig.readerMode == 1) {
                String.format(
                    "select * from new_reader_read_history where bookUrl='%s'",
                    bookUrl
                )
            } else {
                String.format(
                    "select * from old_reader_read_history where bookUrl='%s'",
                    bookUrl
                )
            }
            val cursor = GlobalConfig.db.rawQuery(sql, null)
            if (cursor.moveToNext()) {
                for (i in 0 until cursor.count) {
                    val indexUrl = cursor.getString(1)

                    //这是为了获得该章节在目录中第几卷第几章，供ReaderActivity使用
                    for (j in ccss.indices) {
                        for (k in ccss[j].indices) {
                            if (ccss[j][k].url == indexUrl) {
                                val toContent: Intent = if (GlobalConfig.readerMode == 1) {
                                    Intent(this@ContentsActivity, ReaderActivity::class.java)
                                } else {
                                    Intent(
                                        this@ContentsActivity,
                                        com.cyh128.wenku8reader.oldReader.ReaderActivity::class.java
                                    )
                                }
                                vcssPosition = j
                                ccssPosition = k
                                val position = cursor.getInt(3)
                                toContent.putExtra("position", position)
                                startActivity(toContent)
                            }
                        }
                    }
                }
                cursor.close()
            } else {
                runOnUiThread {
                    val snackbar = Snackbar.make(mainLayout, "没有阅读记录", Snackbar.LENGTH_SHORT)
                    snackbar.setAnchorView(fab)
                    snackbar.show()
                }
            }
        }
    private val setNovelInfo: Handler = object : Handler(Looper.myLooper()!!) {
        override fun handleMessage(msg: Message) {
            title.text = novelDetail[0]
            author.text = novelDetail[1]
            status.text = novelDetail[2]
            update.text = novelDetail[3]
            tag.text = novelDetail[7]
            if (novelDetail[8].contains("本书公告")) { //判断当前小说是否无版权
                popular.visibility = View.GONE
                warningCardView.visibility = View.VISIBLE
                warning.text = novelDetail[8]
                introduce.visibility = View.GONE
            } else {
                popular.text = novelDetail[8]
            }
            anime.text = "动画化情况：" + novelDetail[9]
            if (!this@ContentsActivity.isFinishing) { //https://blog.csdn.net/wjj1996825/article/details/80280109 防止在未加载完成的情况下返回导致的奔溃问题
                Glide.with(this@ContentsActivity)
                    .load(novelDetail[4])
                    .centerCrop()
                    .into(imageView)
            }
            introduce.setContent(
                Html.fromHtml(novelDetail[5], Html.FROM_HTML_MODE_COMPACT).toString()
            )
            vcss = contentsList[0] as List<ContentsVcssBean> //卷list
            ccss = contentsList[1] as List<List<ContentsCcssBean>> //章节list
            contentsListAdapter = ContentsListAdapter(this@ContentsActivity, vcss, ccss)
            expandableRecyclerView.setAdapter(contentsListAdapter)
            expandableRecyclerView.layoutManager = LinearLayoutManager(this@ContentsActivity)


            //加载完成，显示界面======================================================================
            if (inBookcase) { //如果这本书在书架，那么将加入书架的按钮样式变为移出书架的按钮样式
                addToBookcase.icon = removeDraw
                addToBookcase.text = "移出书架"
                val typedValue = TypedValue() //获取 [?attr/] 的颜色
                theme.resolveAttribute(
                    com.google.android.material.R.attr.colorError,
                    typedValue,
                    true
                )
                addToBookcase.setBackgroundColor(typedValue.data)
            }
            mainLayout.visibility = View.VISIBLE
            fab.show()
            linearProgressIndicator.hide()
        }
    }

    companion object {
        var vcss: List<ContentsVcssBean> = ArrayList()
        var ccss: List<List<ContentsCcssBean>> = ArrayList()
        var bookUrl: String? = null
        var ccssPosition = 0
        var vcssPosition = 0
    }
}
