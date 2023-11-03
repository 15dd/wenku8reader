package com.cyh128.wenku8reader.newReader

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.util.Log
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import android.widget.ViewFlipper
import com.bumptech.glide.Glide
import com.cyh128.wenku8reader.R
import com.cyh128.wenku8reader.activity.PhotoViewActivity
import com.cyh128.wenku8reader.util.GlobalConfig
import kotlin.reflect.KProperty

/*
cyh128注:
原作者 https://github.com/ya-b/NetNovelReader
这个PageView已经被我改的乱七八糟的了，导致老是会莫名其妙的出现一些让人意想不到的问题。
但至少目前没什么大问题。
 */

class PageView : ViewFlipper, IPageView {
    override var pageNum: Int by InvalidateAfterSet(1)                    //页数

    override var backgroundcolor: Int by InvalidateAfterSet(Color.WHITE)
    override var textColor: Int by InvalidateAfterSet(Color.BLACK)           //字体颜色
    override var txtFontType: Typeface by InvalidateAfterSet(Typeface.DEFAULT)  //正文字体类型//背景颜色
    override var rowSpace: Float by InvalidateAfterSet(1f)               //行距
    /*
    cyh128注:
    很奇怪，当设置的textSize为19时，居然比设置为21的时候要大很多。
    多次尝试下，发现1~20这个区间显示的字体大小显示的不太正常。
    所以我设置了只能在21~120这个区间内调整，也就是在ReadActivity中设置字体大小时+20f。
    当然，这个问题也有可能是没有根据原作者的原本使用方式来使用导致的，因为原作者的使用方式我看不懂😂😂。
     */
    override var textSize: Float by InvalidateAfterSet(21f)             //正文部分默认画笔的大小
    override var bottomTextSize: Float by InvalidateAfterSet(60f)        //底部部分默认画笔的大小
    override var text: String by InvalidateAfterSet("")                 //一个未分割章节,格式：章节名|正文
    var title: String = ""                                             //章节名称
    override var isDrawTime = false                    //左下角是否显示时间

    var maxTextPageNum = 0                        //最大文本页数

    var pageFlag = 0                          //0刚进入view，1表示目录跳转，2表示下一页，3表示上一页

    var textArray = ArrayList<ArrayList<String>>()
    var imgUrlList = ArrayList<String>()


    //防止滑动过快重复显示对话框 https://blog.csdn.net/shitou0/article/details/81020883
    private val MIN_CLICK_DELAY_TIME = 1000
    private var lastClickTime: Long = 0

    var mBitmap: Bitmap? = null
    var mBarIsShow: Boolean = false
    var direction: Orientation? = null

    val FLIP_DISTANCE = 80f //最小滑动距离（滑动距离超过这个值才能翻页）
    var mDetector: GestureDetector? =
        GestureDetector(context, object : GestureDetector.OnGestureListener {
            override fun onSingleTapUp(e: MotionEvent): Boolean {
                onCenterClick()
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

            override fun onShowPress(e: MotionEvent) {
            }

            override fun onLongPress(e: MotionEvent) {
                if (pageNum - maxTextPageNum > 0 && imgUrlList.size != 0) { //判断当前页面是否有图片，图片存放列表是否不为空
                    val intent = Intent(context, PhotoViewActivity::class.java)
                    intent.putExtra("url", imgUrlList[pageNum - maxTextPageNum - 1].trim())
                    context.startActivity(intent)
                }
            }

            override fun onFling(
                e1: MotionEvent?,
                e2: MotionEvent,
                velocityX: Float,
                velocityY: Float
            ): Boolean {
                if (direction == Orientation.horizontal) {
                    if (e1!!.x - e2.x > FLIP_DISTANCE) {
                        Log.i("debug", "手指向左滑...")
                        pageToNext(Orientation.horizontal)
                        return true
                    }
                    if (e2.x - e1.x > FLIP_DISTANCE) {
                        Log.i("debug", "手指向右滑...")
                        pageToPrevious(Orientation.horizontal)
                        return true
                    }
                } else if (direction == Orientation.vertical) {
                    if (e1!!.y - e2.y > FLIP_DISTANCE) {
                        Log.i("debug", "手指向上滑...")
                        pageToNext(Orientation.vertical)
                        return true
                    }
                    if (e2.y - e1.y > FLIP_DISTANCE) {
                        Log.i("debug", "手指向下滑...")
                        pageToPrevious(Orientation.vertical)
                        return true
                    }
                }
                return false
            }

            override fun onDown(e: MotionEvent): Boolean {
                return false
            }
        })

    constructor(context: Context) : super(context) {
        init(context)
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        init(context)
    }

    fun init(context: Context) {
        showNext()
    }

    override fun prepare(pageNum: Int) {
        this.pageNum = pageNum
    }

    override fun onCenterClick() {
        if (mBarIsShow) { //https://blog.csdn.net/u010687392/article/details/48003979
            //显示
            ReaderActivity.readerActivity.showBar()
            mBarIsShow = !mBarIsShow
        } else {
            //隐藏
            ReaderActivity.readerActivity.hideBar()
            mBarIsShow = !mBarIsShow
        }
    }

    override fun onNextChapter() {
        if (!GlobalConfig.canSwitchChapterByScroll) return
        //https://blog.csdn.net/shitou0/article/details/81020883
        val curClickTime = System.currentTimeMillis()
        if (curClickTime - lastClickTime >= MIN_CLICK_DELAY_TIME) {
            lastClickTime = curClickTime
            ReaderActivity.readerActivity.switchChapter(
                ReaderActivity.Direction.Next)
        }
    }

    override fun onPreviousChapter() {
        if (!GlobalConfig.canSwitchChapterByScroll) return
        //https://blog.csdn.net/shitou0/article/details/81020883
        val curClickTime = System.currentTimeMillis()
        if (curClickTime - lastClickTime >= MIN_CLICK_DELAY_TIME) {
            lastClickTime = curClickTime
            ReaderActivity.readerActivity.switchChapter(
                ReaderActivity.Direction.Previous)
        }
    }

    override fun onPageChange() {
        if (maxTextPageNum != 0 && ReaderActivity.readProgress.valueFrom < (maxTextPageNum + imgUrlList.size).toFloat()) {
            ReaderActivity.readProgress.valueTo = (maxTextPageNum + imgUrlList.size).toFloat()
            ReaderActivity.readProgress.value = pageNum.toFloat()
            ReaderActivity.readProgress.visibility = View.VISIBLE
        } else {
            ReaderActivity.readProgress.visibility = View.INVISIBLE
        }
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        mDetector!!.onTouchEvent(event)
        return true //直接消耗事件
    }

    private fun pageToNext(orientation: Orientation) {
        if (orientation == Orientation.horizontal) {
            setInAnimation(getContext(), R.anim.slide_in_right)
            setOutAnimation(getContext(), R.anim.slide_out_left)
        } else {
            setInAnimation(getContext(), R.anim.slide_in_bottom)
            setOutAnimation(getContext(), R.anim.slide_out_top)
        }
        pageFlag = 2


        if (pageNum < maxTextPageNum + imgUrlList.size) {
            pageNum = pageNum + 1
        } else {
            onNextChapter()
        }
    }

    private fun pageToPrevious(orientation: Orientation) {
        if (orientation == Orientation.horizontal) {
            setInAnimation(getContext(), R.anim.slide_in_left)
            setOutAnimation(getContext(), R.anim.slide_out_right)
        } else {
            setInAnimation(getContext(), R.anim.slide_in_top)
            setOutAnimation(getContext(), R.anim.slide_out_bottom)
        }
        pageFlag = 3
        if (pageNum < 2) {
            onPreviousChapter()
        } else {
            pageNum = pageNum - 1
        }
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    fun displayView() {
        if (pageNum > maxTextPageNum && imgUrlList.size != 0 && maxTextPageNum != -1 && pageNum != 0) {
            val pI = PageImage(context).apply {
                mBgColor = backgroundcolor
                mBottomTextSize = bottomTextSize
                mIsDrawTime = isDrawTime
                mPageNum = 0
                mMaxPageNum = 0
                mTextColor = textColor
                this@PageView.addView(this)
            }

            (getChildAt(this@PageView.indexOfChild(pI)) as PageImage).apply {
                mBgColor = backgroundcolor
                mBottomTextSize = bottomTextSize
                mIsDrawTime = isDrawTime
                mPageNum = pageNum
                mMaxPageNum = maxTextPageNum + imgUrlList.size
                mTextColor = textColor
                mTitle = title

                Thread {
                    try {
                        handler.post {
                            if (ReaderActivity.isNigntMode) {
                                setImageDrawable(resources.getDrawable(R.drawable.image_loading_small_night, null))
                            } else {
                                setImageDrawable(resources.getDrawable(R.drawable.image_loading_small_day, null))
                            }
                            requestLayout()
                        }

                        var drawAble: Drawable = Glide.with(context)
                            .asDrawable()
                            .load(imgUrlList[pageNum - maxTextPageNum - 1].trim())
                            .submit()
                            .get()

                        if (handler != null) {
                            handler.post {
                                setImageDrawable(drawAble)
                                requestLayout()
                            }
                        }
                    } catch (e: NullPointerException) {
                        e.printStackTrace()
                    } catch (e: Exception) {
                        e.printStackTrace()
                        handler.post {
                            if (ReaderActivity.isNigntMode) {
                                setImageDrawable(resources.getDrawable(R.drawable.image_loading_fail_small_night, null))
                            } else {
                                setImageDrawable(resources.getDrawable(R.drawable.image_loading_fail_small_day, null))
                            }

                            requestLayout()
                        }
                    }
                }.start()
            }

            displayedChild = this@PageView.indexOfChild(pI)
        } else {
            val pC = PageText(context).apply {
                mBgColor = backgroundcolor
                mTextSize = textSize
                mBottomTextSize = bottomTextSize
                mIsDrawTime = isDrawTime
                mRowSpace = rowSpace
                mPageNum = 0
                mMaxPageNum = 0
                mTextColor = textColor
                mTxtFontType = txtFontType
                this@PageView.addView(this)
            }

            (getChildAt(this@PageView.indexOfChild(pC)) as PageText).apply {
                mBgColor = backgroundcolor
                if (maxTextPageNum > 0) {
                    if (pageNum > textArray.size) pageNum = textArray.size
                    if (pageNum == 0) pageNum = 1
                    mTextArray = textArray[pageNum - 1]
                }
                mRowSpace = rowSpace
                mTextSize = textSize
                mPageNum = pageNum
                mMaxPageNum = maxTextPageNum + imgUrlList.size
                mTextColor = textColor
                mTitle = title
                mTxtFontType = txtFontType
            }
            displayedChild = this@PageView.indexOfChild(pC)
        }

        Log.i(
            "tag",
            "pageNum " + pageNum + " maxTextPageNum " + maxTextPageNum + " imgUrlList.size " + imgUrlList.size
        )
    }

    //正文区域宽度
    private fun getTextWidth(): Int = (width * 0.96f).toInt()

    //正文区域高度
    private fun getTextHeight(): Int = ((height - bottomTextSize) * 0.96f).toInt()

    private fun spliteText(text: String?): ArrayList<ArrayList<String>> {
        if (text.isNullOrEmpty() || getTextWidth() == 0) return ArrayList()
        title = text.substring(0, text.indexOf("|"))
        val content = text.substring(text.indexOf("|") + 1)
        if (content.isEmpty()) return ArrayList()
        val tmpArray = content.split("\n")
        val tmplist = ArrayList<String>()
        tmpArray.forEach {
            val tmp = "  " + it.trim()
            val totalCount = getTextWidth() / textSize.toInt() //一行容纳字数
            for (i in 0..tmp.length / totalCount) {
                tmp.filterIndexed { index, _ -> index > i * totalCount - 1 && index < (i + 1) * totalCount }
                    .also { tmplist.add(it) }
            }
        }
        val arrayList = ArrayList<ArrayList<String>>()
        val totalCount = getTextHeight() / (textSize * rowSpace).toInt()  //一页容纳行数
        for (i in 0..tmplist.size / totalCount) {
            (tmplist.filterIndexed { index, _ -> index > i * totalCount - 1 && index < (i + 1) * totalCount } as ArrayList<String>)
                .takeIf { it.isNotEmpty() }?.also { arrayList.add(it) }
        }
        return arrayList
    }

    inner class InvalidateAfterSet<T>(@Volatile var value: T) {
        operator fun getValue(thisRef: Any?, property: KProperty<*>): T = value

        @Synchronized
        operator fun setValue(thisRef: Any?, property: KProperty<*>, value: T) {
            this.value = value
            when (property.name) {
                "rowSpace", "textSize" -> {
                    if (width < 1) return
                    val scale = maxTextPageNum.toFloat() / pageNum
                    textArray = spliteText(text)
                    maxTextPageNum = if (text.length <= title.length + 1
                        || text.isEmpty()
                    ) 0 else textArray.size

                    ReaderActivity.showCount++
                    if (ReaderActivity.showCount == 2) { //防止页数在初始化时乱跳
                        pageNum = 1
                    } else {
                        pageNum = (maxTextPageNum / scale).toInt().takeIf { it != 0 } ?: 1
                    }
                    Log.d("tag",pageNum.toString())
                }

                "text" -> {
                    if (width < 1) return
                    textArray = spliteText(text)
                    maxTextPageNum = if (text.length <= title.length + 1
                        || text.isEmpty()
                    ) 0 else textArray.size
                    pageNum = when (pageFlag) {
                        0 -> if (maxTextPageNum == 0) 0 else if (pageNum == 0) 1 else pageNum
                        1, 2 -> if (maxTextPageNum == 0) 0 else 1
                        3 -> maxTextPageNum
                        else -> 1
                    }
                }

                "pageNum" -> {
                    onPageChange()
                    displayView()
                }

                else -> {
                    displayView()
                }  //刷新view
            }
        }
    }
}