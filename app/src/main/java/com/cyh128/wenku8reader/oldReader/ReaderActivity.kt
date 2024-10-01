package com.cyh128.wenku8reader.oldReader

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.DialogInterface
import android.database.Cursor
import android.os.Build
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Handler
import android.os.Message
import android.util.Log
import android.view.GestureDetector
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.MotionEvent
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.NestedScrollView
import com.cyh128.wenku8reader.R
import com.cyh128.wenku8reader.activity.ContentsActivity
import com.cyh128.wenku8reader.bean.ContentsCcssBean
import com.cyh128.wenku8reader.bean.ContentsVcssBean
import com.cyh128.wenku8reader.util.DatabaseHelper
import com.cyh128.wenku8reader.util.GlobalConfig
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.bottomappbar.BottomAppBar
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.slider.Slider
import com.google.android.material.snackbar.Snackbar
import java.text.DecimalFormat

class ReaderActivity : AppCompatActivity() {
    private lateinit var bottomAppBar: BottomAppBar
    private lateinit var bottomSheetDialog: BottomSheetDialog
    private lateinit var toolbar: MaterialToolbar
    private var readFragment: ReadFragment? = null
    private lateinit var dialog: Dialog
    private lateinit var nestedScrollView: NestedScrollView
    private lateinit var fontSizeSlider: Slider
    private lateinit var lineSpacingSlider: Slider
    private lateinit var progressText: TextView
    private var gestureDetector: GestureDetector? = null //https://juejin.cn/post/7032900181519515685
    private var myGestureListener: MyGestureListener? = null

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_old_reader)
        //textView = findViewById(R.id.text_act_reader);
        toolbar = findViewById(R.id.toolbar_act_old_reader)
        bottomAppBar = findViewById(R.id.bottomAppBar_act_old_reader)
        nestedScrollView = findViewById(R.id.scrollView_act_old_reader)
        progressText = findViewById(R.id.text_act_old_reader_progress)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.setDecorFitsSystemWindows(false) //底部小白条沉浸（全面屏手势）https://juejin.cn/post/6904545697552007181
        }
        dialog = Dialog(this)
        val contentView: View = LayoutInflater.from(this).inflate(R.layout.view_alert_progress_dialog, null)
        val builder = MaterialAlertDialogBuilder(this)
        builder.setView(contentView)
        builder.setCancelable(false)
        dialog = builder.show()
        vcss = ContentsActivity.vcss
        ccss = ContentsActivity.ccss
        bookUrl = ContentsActivity.bookUrl
        ccssPosition = ContentsActivity.ccssPosition //设置为在目录点击的章节的索引值(childPosition)，以便跳转上一章节或者下一章节
        vcssPosition = ContentsActivity.vcssPosition
        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener { v: View? ->
            // 退出当前页面
            finish()
        }
        toolbar.title = ccss[vcssPosition][ccssPosition].ccss
        bottomSheetDialog = BottomSheetDialog(this)
        val bottomSheetView: View = LayoutInflater.from(this@ReaderActivity).inflate(R.layout.view_bottom_sheet_act_old_reader, null, false)
        bottomSheetDialog.setContentView(bottomSheetView)
        bottomSheetDialog.dismissWithAnimation = true
        bottomSheetDialog.setOnDismissListener {
            if (GlobalConfig.oldReaderFontSize != fontSizeSlider.value || GlobalConfig.oldReaderLineSpacing != lineSpacingSlider.value) { //判断是否有改动
                GlobalConfig.oldReaderFontSize = fontSizeSlider.value
                GlobalConfig.oldReaderLineSpacing = lineSpacingSlider.value
                DatabaseHelper.SaveReaderSetting()
            }
        }
        fontSizeSlider = bottomSheetView.findViewById(R.id.slider_bottom_sheet_act_old_reader)
        fontSizeSlider.value = GlobalConfig.oldReaderFontSize //设置为之前字体大小的值
        fontSizeSlider.addOnSliderTouchListener(object : Slider.OnSliderTouchListener {
            //字体大小滑动条监听
            override fun onStartTrackingTouch(slider: Slider) {}
            override fun onStopTrackingTouch(slider: Slider) {
                readFragment!!.setContentFontSize(slider.value)
            }
        })
        lineSpacingSlider = bottomSheetView.findViewById(R.id.slider_bottom_sheet_act_old_reader_linespacing)
        lineSpacingSlider.value = GlobalConfig.oldReaderLineSpacing
        lineSpacingSlider.addOnSliderTouchListener(object : Slider.OnSliderTouchListener {
            override fun onStartTrackingTouch(slider: Slider) {}
            override fun onStopTrackingTouch(slider: Slider) {
                readFragment!!.setContentLineSpacing(slider.value)
            }
        })
        readFragment = ReadFragment()
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.fragment_act_old_reader, readFragment!!)
            .commit()
        val msg = Message()
        handler.sendMessage(msg)
        setBottomAppBarListener() //设置底栏菜单按钮监听
        nestedScrollView.setOnScrollChangeListener { v, scrollX, scrollY, oldScrollX, oldScrollY ->
            val start: Int = nestedScrollView.height + scrollY
            val bottom: Int = nestedScrollView.getChildAt(0).height
            val df = DecimalFormat("0.00")
            var result: String = df.format((start.toFloat() / bottom * 100).toDouble())
            result = result.substring(0, result.indexOf("."))
            progressText.text = "阅读进度:$result%"
        }

        //隐藏appbar和bottomAppbar
        object : CountDownTimer(3000, 1000) {
            override fun onTick(millisUntilFinished: Long) {}
            override fun onFinish() {
                supportActionBar!!.hide()
                bottomAppBar.visibility = View.INVISIBLE
            }
        }.start()
        myGestureListener = MyGestureListener()
        gestureDetector = GestureDetector(this, myGestureListener!!)
        findViewById<View>(R.id.scrollView_act_old_reader).setOnTouchListener { v: View?, event: MotionEvent? ->
            gestureDetector!!.onTouchEvent(
                (event)!!
            )
        }
    }

    override fun onTouchEvent(event: MotionEvent): Boolean { //https://juejin.cn/post/7032900181519515685
        return gestureDetector!!.onTouchEvent(event)
    }

    override fun onDestroy() {
        super.onDestroy()
        dialog.dismiss()
        DatabaseHelper.SaveOldReaderReadHistory(
            bookUrl,
            ccss[vcssPosition][ccssPosition].url,
            ccss[vcssPosition][ccssPosition].ccss,
            nestedScrollView.scrollY
        )
    }

    private fun setBottomAppBarListener() { //设置底栏菜单按钮监听
        bottomAppBar.setOnMenuItemClickListener { item: MenuItem ->
            when (item.itemId) {
                R.id.setting -> {
                    bottomSheetDialog.show()
                    return@setOnMenuItemClickListener true
                }
                R.id.previous -> {
                    if (ccssPosition == 0) { //判断是不是该卷的第一章
                        val snackbar: Snackbar = Snackbar.make(
                            (toolbar),
                            "没有上一章，已经是这一卷的第一章了",
                            Snackbar.LENGTH_SHORT
                        )
                        snackbar.setAnchorView(bottomAppBar) //使它出现在bottomAppBar的上面，避免遮挡内容
                        snackbar.show()
                        return@setOnMenuItemClickListener true
                    }
                    ccssPosition--
                    dialog.show()
                    toolbar.title = ccss[vcssPosition][ccssPosition].ccss
                    Thread {
                        try {
                            readFragment!!.setContent()
                            dialog.dismiss()
                            runOnUiThread {
                                nestedScrollView.post {
                                    nestedScrollView.scrollTo(
                                        0,
                                        0
                                    )
                                }
                            }
                        } catch (e: Exception) {
                            e.printStackTrace()
                            runOnUiThread {
                                MaterialAlertDialogBuilder(this@ReaderActivity)
                                    .setTitle("网络超时")
                                    .setMessage("连接超时，可能是服务器出错了、也可能是网络卡慢或者您正在连接VPN或代理服务器，请稍后再试")
                                    .setIcon(R.drawable.timeout)
                                    .setCancelable(false)
                                    .setPositiveButton(
                                        "明白"
                                    ) { dialog: DialogInterface?, which: Int -> finish() }
                                    .show()
                            }
                        }
                    }.start()
                    return@setOnMenuItemClickListener true
                }
                R.id.next -> {
                    if (ccssPosition == ccss[vcssPosition].size - 1) { //判断是不是该卷的最后一章
                        val snackbar: Snackbar = Snackbar.make(
                            toolbar,
                            "没有下一章，已经是这一卷的最后一章了",
                            Snackbar.LENGTH_SHORT
                        )
                        snackbar.setAnchorView(bottomAppBar) //使它出现在bottomAppBar的上面，避免遮挡内容
                        snackbar.show()
                        return@setOnMenuItemClickListener true
                    }
                    ccssPosition++
                    dialog.show()
                    toolbar.title = ccss[vcssPosition][ccssPosition].ccss
                    Thread {
                        try {
                            readFragment!!.setContent()
                            dialog.dismiss()
                            runOnUiThread {
                                nestedScrollView.post {
                                    nestedScrollView.scrollTo(
                                        0,
                                        0
                                    )
                                }
                            }
                        } catch (e: Exception) {
                            e.printStackTrace()
                            runOnUiThread {
                                MaterialAlertDialogBuilder(this@ReaderActivity)
                                    .setTitle("网络超时")
                                    .setMessage("连接超时，可能是服务器出错了、也可能是网络卡慢或者您正在连接VPN或代理服务器，请稍后再试")
                                    .setIcon(R.drawable.timeout)
                                    .setCancelable(false)
                                    .setPositiveButton(
                                        "明白"
                                    ) { dialog: DialogInterface?, which: Int -> finish() }
                                    .show()
                            }
                        }
                    }.start()
                    return@setOnMenuItemClickListener true
                }
                else -> false
            }
        }
    }

    private val handler: Handler = Handler {
        Thread {
            try {
                readFragment!!.setContent()
                dialog.dismiss()
                val msg2 = Message()
                scrollToTarget.sendMessage(msg2)
            } catch (e: Exception) {
                e.printStackTrace()
                runOnUiThread {
                    MaterialAlertDialogBuilder(this@ReaderActivity)
                        .setTitle("网络超时")
                        .setMessage("连接超时，可能是服务器出错了、也可能是网络卡慢或者您正在连接VPN或代理服务器，请稍后再试")
                        .setIcon(R.drawable.timeout)
                        .setCancelable(false)
                        .setPositiveButton(
                            "明白"
                        ) { dialog: DialogInterface?, which: Int -> finish() }
                        .show()
                }
            }
        }.start()
        false
    }
    private val scrollToTarget: Handler = Handler {
        val sql: String =
            String.format("select * from old_reader_read_history where bookUrl='%s'", bookUrl)
        val cursor: Cursor = GlobalConfig.db.rawQuery(sql, null)
        if (cursor.moveToNext()) {
            for (i in 0 until cursor.count) {
                cursor.move(i)
                val position: Int = cursor.getInt(3)
                if ((cursor.getString(2) == ccss[vcssPosition][ccssPosition].ccss)) {
                    Log.d("debug", position.toString())
                    //nestedScrollView.fling(0);
                    nestedScrollView.post {
                        nestedScrollView.scrollTo(
                            0,
                            position
                        )
                    }
                }
            }
            cursor.close()
        } else {
            nestedScrollView.post { nestedScrollView.scrollTo(0, 0) }
        }
        false
    }

    private inner class MyGestureListener : GestureDetector.OnGestureListener {
        override fun onDown(e: MotionEvent): Boolean {
            return false
        }

        override fun onShowPress(e: MotionEvent) {}
        override fun onSingleTapUp(e: MotionEvent): Boolean {
            if (toolbar.isShown && bottomAppBar.isShown) {
                supportActionBar!!.hide()
                bottomAppBar.visibility = View.INVISIBLE
                return true
            }
            supportActionBar!!.show()
            bottomAppBar.visibility = View.VISIBLE
            return true
        }

        override fun onScroll(
            e1: MotionEvent?,
            e2: MotionEvent,
            distanceX: Float,
            distanceY: Float
        ): Boolean {
            return false
        }

        override fun onLongPress(e: MotionEvent) {}
        override fun onFling(
            e1: MotionEvent?,
            e2: MotionEvent,
            velocityX: Float,
            velocityY: Float
        ): Boolean {
            return false
        }
    }

    companion object {
        var vcss: List<ContentsVcssBean> = ArrayList()
        var ccss: List<List<ContentsCcssBean>> = ArrayList()
        var bookUrl: String? = null
        var ccssPosition: Int = 0
        var vcssPosition: Int = 0
    }
}
