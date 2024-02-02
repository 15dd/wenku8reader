package com.cyh128.wenku8reader.newReader

import android.animation.Animator
import android.app.Dialog
import android.app.UiModeManager
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.os.CountDownTimer
import android.text.Html
import android.util.Log
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import android.widget.Button
import android.widget.CompoundButton
import androidx.appcompat.app.AppCompatActivity
import com.app.hubert.guide.NewbieGuide
import com.app.hubert.guide.model.GuidePage
import com.cyh128.wenku8reader.R
import com.cyh128.wenku8reader.activity.ContentsActivity
import com.cyh128.wenku8reader.activity.SelectColorActivity
import com.cyh128.wenku8reader.bean.ContentsCcssBean
import com.cyh128.wenku8reader.bean.ContentsVcssBean
import com.cyh128.wenku8reader.util.DatabaseHelper
import com.cyh128.wenku8reader.util.GlobalConfig
import com.cyh128.wenku8reader.util.Wenku8Spider.Content
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.button.MaterialButtonToggleGroup
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.materialswitch.MaterialSwitch
import com.google.android.material.slider.Slider
import com.google.android.material.snackbar.Snackbar
import com.gyf.immersionbar.ImmersionBar

class ReaderActivity : AppCompatActivity() {
    lateinit var toolbar: MaterialToolbar
    lateinit var bottombar: View
    private var showBarColor: Int = 0
    private var hideBarColor: String? = null
    lateinit var pageView: PageView
    private lateinit var dialog: Dialog
    private var builder: MaterialAlertDialogBuilder? = null
    private lateinit var bottomSheetDialog: BottomSheetDialog
    private lateinit var fontSize: Slider
    private lateinit var lineSpacing: Slider
    private lateinit var bottomTextSize: Slider
    private lateinit var readDirection: MaterialButtonToggleGroup
    private lateinit var switchChapter: MaterialSwitch
    private var historyPosition: Int = -1 //历史记录
    private lateinit var bottomSheetView: View
    override fun onCreate(savedInstanceState: Bundle?) {
        //我已经逐渐看不懂这个阅读器是怎么写出来的了，好像想整个重写一遍。。。。。。
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_reader)
        readerActivity = this
        historyPosition = intent.getIntExtra("position", -1) //是否有阅读记录
        vcss = ContentsActivity.vcss
        ccss = ContentsActivity.ccss
        bookUrl = ContentsActivity.bookUrl
        ccssPosition = ContentsActivity.ccssPosition
        vcssPosition = ContentsActivity.vcssPosition
        pageView = findViewById(R.id.readerView)
        bottombar = findViewById(R.id.bottombar_act_read)
        toolbar = findViewById(R.id.toolbar_act_read)
        readProgress = findViewById(R.id.progress_act_read)
        val previousChapter: Button = findViewById(R.id.button_act_read_previousChapter)
        val nextChapter: Button = findViewById(R.id.button_act_read_nextChapter)
        val setting: Button = findViewById(R.id.button_act_read_Setting)
        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener { v: View? ->
            // 退出当前页面
            finish()
        }
        toolbar.title = ccss[vcssPosition][ccssPosition].ccss
        val typedValue: TypedValue = TypedValue()
        theme.resolveAttribute(
            com.google.android.material.R.attr.colorSurfaceContainer,
            typedValue,
            true
        )
        showBarColor = typedValue.resourceId
        toolbar.setBackgroundResource(showBarColor)
        bottombar.setBackgroundResource(showBarColor)

        //getSystemService导致的内存泄漏问题 https://blog.csdn.net/xiabing082/article/details/53993298
        val uiModeManager: UiModeManager =
            applicationContext.getSystemService(UI_MODE_SERVICE) as UiModeManager
        isNigntMode = uiModeManager.nightMode == UiModeManager.MODE_NIGHT_YES
        setBackgroundAndTextColor() //初始化背景颜色的字符串，以便hideBar()调用
        bottomSheetDialog = BottomSheetDialog(this)
        bottomSheetView = LayoutInflater.from(this).inflate(R.layout.view_bottom_sheet_act_new_reader, null, false)
        bottomSheetDialog.setContentView(bottomSheetView)
        bottomSheetDialog.dismissWithAnimation = true
        //保存设置
        bottomSheetDialog.setOnDismissListener { dialog: DialogInterface? ->
            hideBar()
            GlobalConfig.newReaderFontSize = fontSize.value + 20f
            GlobalConfig.newReaderLineSpacing = lineSpacing.value
            GlobalConfig.readerBottomTextSize = bottomTextSize.value
            DatabaseHelper.SaveReaderSetting()
        }
        fontSize = bottomSheetView.findViewById(R.id.slider_bottom_sheet_act_reader_fontsize)
        lineSpacing = bottomSheetView.findViewById(R.id.slider_bottom_sheet_act_reader_linespacing)
        bottomTextSize = bottomSheetView.findViewById(R.id.slider_bottom_sheet_act_reader_bottomfontsize)
        readDirection = bottomSheetView.findViewById(R.id.toggleGroup_bottom_sheet_act_read)
        switchChapter = bottomSheetView.findViewById(R.id.switch_bottom_sheet_act_read)
        fontSize.value = GlobalConfig.newReaderFontSize - 20f //slider上显示的数字比实际字体大小要小20f
        lineSpacing.value = GlobalConfig.newReaderLineSpacing
        bottomTextSize.value = GlobalConfig.readerBottomTextSize
        readDirection.check(if (GlobalConfig.isUpToDown) R.id.button_bottom_sheet_act_read_utd else R.id.button_bottom_sheet_act_read_ltr)
        switchChapter.isChecked = GlobalConfig.canSwitchChapterByScroll
        fontSize.addOnSliderTouchListener(object : Slider.OnSliderTouchListener {
            override fun onStartTrackingTouch(slider: Slider) {}
            override fun onStopTrackingTouch(slider: Slider) {
                pageView.textSize = slider.value + 20f //实际字体大小比slider上显示的数字要大20f
            }
        })
        lineSpacing.addOnSliderTouchListener(object : Slider.OnSliderTouchListener {
            override fun onStartTrackingTouch(slider: Slider) {}
            override fun onStopTrackingTouch(slider: Slider) {
                pageView.rowSpace = slider.value
            }
        })
        bottomTextSize.addOnSliderTouchListener(object : Slider.OnSliderTouchListener {
            override fun onStartTrackingTouch(slider: Slider) {}
            override fun onStopTrackingTouch(slider: Slider) {
                pageView.bottomTextSize = slider.value
            }
        })
        readDirection.addOnButtonCheckedListener { group: MaterialButtonToggleGroup?, checkedId: Int, isChecked: Boolean ->
            if (checkedId == R.id.button_bottom_sheet_act_read_ltr && isChecked) {
                pageView.direction = Orientation.horizontal
                GlobalConfig.isUpToDown = false
            } else if (checkedId == R.id.button_bottom_sheet_act_read_utd && isChecked) {
                pageView.direction = Orientation.vertical
                GlobalConfig.isUpToDown = true
            }
        }
        readProgress?.addOnSliderTouchListener(object : Slider.OnSliderTouchListener {
            override fun onStartTrackingTouch(slider: Slider) {}
            override fun onStopTrackingTouch(slider: Slider) {
                pageView.pageNum = slider.value.toInt()
            }
        })
        switchChapter.setOnCheckedChangeListener { buttonView: CompoundButton?, isChecked: Boolean ->
            GlobalConfig.canSwitchChapterByScroll = isChecked
        }
        previousChapter.setOnClickListener { v: View? -> toPreviousChapter() }
        nextChapter.setOnClickListener { v: View? -> toNextChapter() }
        setting.setOnClickListener { v: View? -> bottomSheetDialog.show() }
        showLoadingDialog()
        loadContent()
        showBar()

        //进入阅读界面2秒后隐藏appbar和bottomAppbar
        object : CountDownTimer(1000, 1000) {
            override fun onTick(millisUntilFinished: Long) {}
            override fun onFinish() {
                hideBar()
            }
        }.start()
        NewbieGuide.with(this@ReaderActivity) //新手引导
            .setLabel("imageLongShow")
            .addGuidePage(GuidePage.newInstance().setLayoutRes(R.layout.view_image_long_show))
            .show()
        bottomSheetView.findViewById<View>(R.id.button_select_rgb)
            .setOnClickListener {
                startActivity(
                    Intent(
                        this@ReaderActivity,
                        SelectColorActivity::class.java
                    )
                )
            }
    }

    override fun onDestroy() {
        super.onDestroy()
        dialog.dismiss()
        //防止内存泄露
        readerActivity = null
        readProgress = null
        DatabaseHelper.SaveNewReaderReadHistory(
            bookUrl,
            ccss[vcssPosition][ccssPosition].url,
            ccss[vcssPosition][ccssPosition].ccss,
            pageView.pageNum
        )
    }

    fun setBackgroundAndTextColor() {
        if (isNigntMode) {
            pageView.textColor = Color.parseColor(GlobalConfig.textColorNight)
            pageView.backgroundcolor = Color.parseColor(GlobalConfig.backgroundColorNight)
            hideBarColor = GlobalConfig.backgroundColorNight
        } else {
            pageView.textColor = Color.parseColor(GlobalConfig.textColorDay)
            pageView.backgroundcolor = Color.parseColor(GlobalConfig.backgroundColorDay)
            hideBarColor = GlobalConfig.backgroundColorDay
        }
    }

    private fun loadContent() {
        Thread {
            try {
                val allContent: List<List<String>?> = Content(
                    (bookUrl)!!, ccss[vcssPosition][ccssPosition].url
                )
                val title: String = ccss[vcssPosition][ccssPosition].ccss
                val text: String = "$title|" + Html.fromHtml(
                    allContent.get(0)!!.get(0),
                    Html.FROM_HTML_MODE_COMPACT
                )
                runOnUiThread {
                    pageView.removeAllViews() //删除上一章加载的内容
                    pageView.imgUrlList = ArrayList()
                    readProgress!!.value = 1f //进度条重置
                    showCount = 0
                    setBackgroundAndTextColor()
                    if (allContent[1] != null && allContent[1]!!.isNotEmpty()) {
                        pageView.imgUrlList = (allContent[1] as ArrayList<String>?)!!
                    }
                    pageView.text = text
                    pageView.textSize = GlobalConfig.newReaderFontSize
                    pageView.bottomTextSize = GlobalConfig.readerBottomTextSize
                    pageView.rowSpace = GlobalConfig.newReaderLineSpacing
                    pageView.direction =
                        if (GlobalConfig.isUpToDown) Orientation.vertical else Orientation.horizontal
                    if (historyPosition != -1) { //如果有传递历史记录，就执行
                        pageView.pageNum = historyPosition
                        historyPosition = -1
                    }
                    dialog.dismiss()
                }
            } catch (e: Exception) {
                e.printStackTrace()
                runOnUiThread {
                    MaterialAlertDialogBuilder(this@ReaderActivity)
                        .setTitle("网络超时")
                        .setMessage("连接超时，可能是服务器出错了、也可能是网络卡慢或者您正在连接VPN或代理服务器，请稍后再试")
                        .setIcon(R.drawable.timeout)
                        .setCancelable(false)
                        .setPositiveButton("好的", null)
                        .show()
                }
                ccssPosition--
            }
        }.start()
    }

    fun showLoadingDialog() {
        dialog = Dialog(this)
        val contentView: View =
            LayoutInflater.from(this).inflate(R.layout.view_alert_progress_dialog, null)
        builder = MaterialAlertDialogBuilder(this)
        builder!!.setView(contentView)
        builder!!.setCancelable(false)
        dialog = builder!!.show()
    }

    enum class Direction {
        Next,
        Previous
    }

    fun switchChapter(direction: Direction) {
        if (direction == Direction.Next) {
            runOnUiThread {
                MaterialAlertDialogBuilder(this@ReaderActivity)
                    .setTitle("切换下一章")
                    .setMessage("是否继续？")
                    .setIcon(R.drawable.info2)
                    .setCancelable(false)
                    .setPositiveButton(
                        "继续"
                    ) { dialog: DialogInterface?, which: Int -> toNextChapter() }
                    .setNegativeButton("取消", null)
                    .show()
            }
        } else if (direction == Direction.Previous) {
            runOnUiThread {
                MaterialAlertDialogBuilder(this@ReaderActivity)
                    .setTitle("切换上一章")
                    .setMessage("是否继续？")
                    .setIcon(R.drawable.info2)
                    .setCancelable(false)
                    .setPositiveButton(
                        "继续"
                    ) { dialog: DialogInterface?, which: Int -> toPreviousChapter() }
                    .setNegativeButton("取消", null)
                    .show()
            }
        }
    }

    private fun toNextChapter() {
        runOnUiThread {
            if (ccssPosition == ccss[vcssPosition].size - 1) { //判断是不是该卷的最后一章
                val snackbar: Snackbar = Snackbar.make(
                    toolbar,
                    "没有下一章，已经是这一卷的最后一章了",
                    Snackbar.LENGTH_SHORT
                )
                snackbar.setAnchorView(bottombar) //使它出现在bottomAppBar的上面，避免遮挡内容
                snackbar.show()
                return@runOnUiThread
            }
            showLoadingDialog()
            ccssPosition++
            toolbar.title = ccss[vcssPosition][ccssPosition].ccss
            loadContent()
        }
    }

    private fun toPreviousChapter() {
        runOnUiThread {
            if (ccssPosition == 0) { //判断是不是该卷的第一章
                val snackbar: Snackbar = Snackbar.make(
                    toolbar,
                    "没有上一章，已经是这一卷的第一章了",
                    Snackbar.LENGTH_SHORT
                )
                snackbar.setAnchorView(bottombar) //使它出现在bottomAppBar的上面，避免遮挡内容
                snackbar.show()
                return@runOnUiThread
            }
            showLoadingDialog()
            ccssPosition--
            toolbar.title = ccss[vcssPosition][ccssPosition].ccss
            loadContent()
        }
    }

    fun showBar() {
        //顶栏显示动画
        toolbar.animate().translationY(0f).setListener(object : Animator.AnimatorListener {
            override fun onAnimationStart(animation: Animator) {
                Log.d("tag", "readeractivity hide:$hideBarColor")
                ImmersionBar.with(this@ReaderActivity)
                    .statusBarColor(showBarColor)
                    .navigationBarColor(showBarColor)
                    .fitsSystemWindows(true)
                    .autoDarkModeEnable(true)
                    .init()
            }

            override fun onAnimationEnd(animation: Animator) {}
            override fun onAnimationCancel(animation: Animator) {}
            override fun onAnimationRepeat(animation: Animator) {}
        }).setInterpolator(AccelerateDecelerateInterpolator())

        //底栏显示动画
        bottombar!!.animate().translationY(0f).setListener(object : Animator.AnimatorListener {
            override fun onAnimationStart(animation: Animator) {
                ImmersionBar.with(this@ReaderActivity)
                    .statusBarColor(showBarColor)
                    .navigationBarColor(showBarColor)
                    .fitsSystemWindows(true)
                    .autoDarkModeEnable(true)
                    .init()
            }

            override fun onAnimationEnd(animation: Animator) {}
            override fun onAnimationCancel(animation: Animator) {}
            override fun onAnimationRepeat(animation: Animator) {}
        }).setInterpolator(AccelerateDecelerateInterpolator())
    }

    fun hideBar() {
        //顶栏隐藏动画
        toolbar.animate().translationY(-toolbar.getHeight().toFloat())
            .setListener(object : Animator.AnimatorListener {
                override fun onAnimationStart(animation: Animator) {}
                override fun onAnimationEnd(animation: Animator) {
                    ImmersionBar.with(this@ReaderActivity)
                        .statusBarColor(hideBarColor)
                        .navigationBarColor(hideBarColor)
                        .fitsSystemWindows(true)
                        .autoDarkModeEnable(true)
                        .init()
                }

                override fun onAnimationCancel(animation: Animator) {}
                override fun onAnimationRepeat(animation: Animator) {}
            }).setInterpolator(AccelerateDecelerateInterpolator())

        //底栏隐藏动画
        bottombar.animate().translationY(bottombar.height.toFloat())
            .setListener(object : Animator.AnimatorListener {
                override fun onAnimationStart(animation: Animator) {}
                override fun onAnimationEnd(animation: Animator) {
                    ImmersionBar.with(this@ReaderActivity)
                        .statusBarColor(hideBarColor)
                        .navigationBarColor(hideBarColor)
                        .fitsSystemWindows(true)
                        .autoDarkModeEnable(true)
                        .init()
                }

                override fun onAnimationCancel(animation: Animator) {}
                public override fun onAnimationRepeat(animation: Animator) {}
            }).setInterpolator(AccelerateDecelerateInterpolator())
    }

    companion object {
        var vcss: List<ContentsVcssBean> = ArrayList()
        var ccss: List<List<ContentsCcssBean>> = ArrayList()
        var bookUrl: String? = null
        var ccssPosition: Int = 0
        var vcssPosition: Int = 0
        var readerActivity: ReaderActivity? = null //状态栏和导航栏沉浸所使用的
        var readProgress: Slider? = null
        var isNigntMode: Boolean = false
        var showCount: Int = 0
    }
}
