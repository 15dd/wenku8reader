package com.cyh128.hikari_novel.ui.view.read.horizontal

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.graphics.Typeface
import android.util.AttributeSet
import android.view.MotionEvent
import android.widget.ViewFlipper
import androidx.core.view.size
import com.cyh128.hikari_novel.R
import com.cyh128.hikari_novel.data.model.HorizontalRead
import kotlin.math.abs
import kotlin.reflect.KProperty

class PageView : ViewFlipper, IPageView {
    override var pageNum: Int by InvalidateAfterSet(1)                  //页数
    override var backgroundcolor: Int by InvalidateAfterSet(Color.WHITE)    //背景颜色
    override var textColor: Int by InvalidateAfterSet(Color.BLACK)             //字体颜色
    override var txtFontType: Typeface by InvalidateAfterSet(Typeface.DEFAULT)  //正文字体类型
    override var rowSpace: Float by InvalidateAfterSet(1f)          //行距
    override var textSize: Float by InvalidateAfterSet(55f)               //正文部分默认画笔的大小
    override var bottomTextSize: Float by InvalidateAfterSet(45f)         //底部部分默认画笔的大小
    override var content: HorizontalRead by InvalidateAfterSet(HorizontalRead.empty()) //一个未分割章节,格式：章节名|正文
    var switchAnimation: Boolean = false                             //翻页动画
    var title: String = ""                                           //章节名称

    var maxPageNum = 0                        //最大页数
    var pageFlag = 0                          //0刚进入view，1表示目录跳转，2表示下一页，3表示上一页
    private val minMove = 80F                //翻页最小滑动距离

    var textArray = mutableListOf<MutableList<String>>()
    var imageArray = listOf<String>()

    private lateinit var imageClick: (String) -> Unit

    private var isMoved = false                       //手势判断
    private var isTouching = false                    //手势判断
    private var moveStart = FloatArray(2)        //手势判断
    private var moveEnd = FloatArray(2)          //手势判断

    override var onNextChapter: IPageView.OnNextChapter? = null          //下一章
    override var onPreviousChapter: IPageView.OnPreviousChapter? = null  //上一章
    override var onCenterClick: IPageView.OnCenterClick? = null          //点击view中间部分
    override var onPageChange: IPageView.OnPageChange? = null            //当翻页时调用，向前向后翻页，同一章内翻页，翻至其他章节都会调用

    constructor(context: Context) : super(context) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        init()
    }

    private fun init() { //界面初始化
        showLoadingTip()
        showNext()
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                moveStart = floatArrayOf(event.x, event.y)
                isTouching = true
                isMoved = false
            }

            MotionEvent.ACTION_MOVE -> {
                moveEnd = floatArrayOf(event.x, event.y)
                isMoved = true
            }

            MotionEvent.ACTION_UP -> {
                moveEnd = floatArrayOf(event.x, event.y)
                isTouching = false
            }
        }
        if (!isTouching && (!isMoved || (isMoved && abs(moveStart[0] - moveEnd[0]) < 5f && abs(
                moveStart[1] - moveEnd[1]
            ) < 5f))
        ) { //点击事件
            when {
                moveStart[0] > width * 3 / 5 -> {
                    if (currentView is PageImage || currentView is PageLoading) {
                        currentView.dispatchTouchEvent(event)
                        return super.onTouchEvent(event)
                    }
                    pageToNext()
                }

                moveStart[0] < width * 2 / 5 -> {
                    if (currentView is PageImage || currentView is PageLoading) {
                        currentView.dispatchTouchEvent(event)
                        return super.onTouchEvent(event)
                    }
                    pageToPrevious()
                }

                moveStart[1] > height * 2 / 5 && moveStart[1] < height * 3 / 5 -> {
                    if (currentView is PageImage) {
                        currentView.dispatchTouchEvent(event)
                        return super.onTouchEvent(event)
                    }
                    inAnimation = null
                    outAnimation = null
                    onCenterClick?.onCenterClick()
                    pageFlag = 1
                }

                moveStart[0] > width * 2 / 5 && moveStart[0] < height * 3 / 5 -> {
                    if (currentView is PageImage) {
                        currentView.dispatchTouchEvent(event)
                        return super.onTouchEvent(event)
                    }
                    inAnimation = null
                    outAnimation = null
                    onCenterClick?.onCenterClick()
                    pageFlag = 1
                }
            }
        } else if (!isTouching && isMoved && abs(moveStart[0] - moveEnd[0]) > minMove && abs(
                moveStart[1] - moveEnd[1]
            ) < minMove
        ) { //滑动左右翻页
            when {
                moveStart[0] > moveEnd[0] -> {
                    pageToNext()
                }

                else -> pageToPrevious()
            }
        } else if (!isTouching && isMoved && abs(moveStart[1] - moveEnd[1]) > minMove * height / width && abs(
                moveStart[0] - moveEnd[0]
            ) > minMove
        ) {  //需要进一步判断翻页方向
            if (!(abs(moveStart[1] - moveEnd[1]) / abs(moveStart[0] - moveEnd[0]) > height / width)) {
                if (moveStart[0] > moveEnd[0]) {
                    pageToNext()
                } else {
                    pageToPrevious()
                }
            }
        }
        return true
    }

    fun pageToNext() {
        if (switchAnimation) {
            setInAnimation(context, R.anim.slide_in_right)
            setOutAnimation(context, R.anim.slide_out_left)
        } else {
            inAnimation = null
            outAnimation = null
        }
        pageFlag = 2
        if (pageNum < maxPageNum) {
            pageNum += 1
        } else {
            onNextChapter?.onNextChapter()
        }
    }

    fun pageToPrevious() {
        if (switchAnimation) {
            setInAnimation(context, R.anim.slide_in_left)
            setOutAnimation(context, R.anim.slide_out_right)
        } else {
            inAnimation = null
            outAnimation = null
        }
        pageFlag = 3
        if (pageNum < 2) {
            onPreviousChapter?.onPreviousChapter()
        } else {
            pageNum -= 1
        }
    }

    fun showLoadingTip() {
        if (this.currentView !is PageLoading) {
            this.removeAllViews()
            PageLoading(context).apply {
                mBgColor = backgroundcolor
                mTextSize = textSize
                mTextColor = textColor
                mTxtFontType = txtFontType
                this@PageView.addView(this)
            }
        }
    }

    fun setImageClickListener(listener: (String) -> Unit) {
        imageClick = listener
    }

    private fun displayView() {
        initPage()

        if (pageNum <= textArray.size) {
            val pageTextAnother = if (displayedChild == 0) 1 else 0
            (getChildAt(pageTextAnother) as PageText).apply {
                mBgColor = backgroundcolor
                if (maxPageNum > 0) {
                    if (pageNum > textArray.size) pageNum = textArray.size
                    if (pageNum == 0) pageNum = 1
                    mTextArray = textArray[pageNum - 1]
                }
                mRowSpace = rowSpace
                mTextSize = textSize
                mPageNum = pageNum
                mMaxPageNum = maxPageNum
                mTextColor = textColor
                mTitle = title
                mTxtFontType = txtFontType
                mBottomTextSize = bottomTextSize
            }
            displayedChild = pageTextAnother
        } else if (textArray.size < pageNum && pageNum <= maxPageNum) {
            val pageImageAnother = pageNum - textArray.size + 1
            val imageUrl = imageArray[pageNum - textArray.size - 1].trim()
            (getChildAt(pageImageAnother) as PageImage).apply {
                mBgColor = backgroundcolor
                mImageurl = imageUrl
                mRowSpace = rowSpace
                mTextSize = textSize
                mPageNum = pageNum
                mMaxPageNum = maxPageNum
                mTextColor = textColor
                mTitle = title
                mTxtFontType = txtFontType
                mBottomTextSize = bottomTextSize
                mImageClick = imageClick
                mOnCenterClick = onCenterClick
                mOnNextPage = { pageToNext() }
                mOnPreviousPage = { pageToPrevious() }
                updateImage()
            }
            displayedChild = pageImageAnother
        }
    }

    private fun initPage() {
        if (this.currentView is PageLoading) {
            this.removeAllViews()
            PageText(context).apply {
                mBgColor = backgroundcolor
                mTextSize = textSize
                mBottomTextSize = bottomTextSize
                mRowSpace = rowSpace
                mPageNum = 0
                mMaxPageNum = 0
                mTextColor = textColor
                mTxtFontType = txtFontType
                this@PageView.addView(this)
            }
            PageText(context).apply {
                mBgColor = backgroundcolor
                mTextSize = textSize
                mBottomTextSize = bottomTextSize
                mRowSpace = rowSpace
                mPageNum = 0
                mMaxPageNum = 0
                mTextColor = textColor
                mTxtFontType = txtFontType
                this@PageView.addView(this)
            }
        }

        if (imageArray.isNotEmpty() && this.size == 2) {
            for (i in imageArray) {
                PageImage(context).apply {
                    mBgColor = backgroundcolor
                    mTextSize = textSize
                    mBottomTextSize = bottomTextSize
                    mRowSpace = rowSpace
                    mPageNum = 0
                    mMaxPageNum = 0
                    mTextColor = textColor
                    mTxtFontType = txtFontType
                    this@PageView.addView(this)
                }
            }
        }
    }

    //正文区域宽度
    private fun getTextWidth(): Int = (width * 0.96f).toInt()

    //正文区域高度
    private fun getTextHeight(): Int = ((height - bottomTextSize) * 0.96f).toInt()

    private fun splitContent(chapterContent: String?): MutableList<MutableList<String>> {
        if (chapterContent.isNullOrEmpty() || getTextWidth() == 0) return mutableListOf()
        title = content.title
        val content = chapterContent
        if (content.isEmpty()) return mutableListOf()
        val tmpArray = content.split("\n")
        val tmpList = mutableListOf<String>()
        tmpArray.forEach {
            val tmp = "" + it.trim()
            val totalCount = getTextWidth() / textSize.toInt() //一行容纳字数
            for (i in 0..tmp.length / totalCount) {
                tmp.filterIndexed { index, _ -> index > i * totalCount - 1 && index < (i + 1) * totalCount }
                    .also { tmpList.add(it) }
            }
        }
        val arrayList = mutableListOf<MutableList<String>>()
        val totalCount = getTextHeight() / (textSize * rowSpace).toInt()  //一页容纳行数
        for (i in 0..tmpList.size / totalCount) {
            (tmpList.filterIndexed { index, _ -> index > i * totalCount - 1 && index < (i + 1) * totalCount } as ArrayList<String>)
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
                "rowSpace", "textSize", "bottomTextSize" -> {
                    if (width < 1) return
                    val scale = maxPageNum.toFloat() / pageNum
                    textArray = splitContent(content.content)
                    maxPageNum = if (content.content.length <= title.length + 1
                        || content.content.isEmpty()
                    ) 0 else textArray.size + imageArray.size
                    pageNum = (maxPageNum / scale).toInt().takeIf { it != 0 } ?: 1
                }

                "content" -> {
                    if (width < 1) return
                    textArray = splitContent(content.content)
                    imageArray = content.imageList
                    maxPageNum = if (content.content.length <= title.length + 1
                        || content.content.isEmpty()
                    ) 0 else textArray.size + imageArray.size
                    pageNum = when (pageFlag) {
                        0 -> if (maxPageNum == 0) 0 else if (pageNum == 0) 1 else pageNum
                        1, 2 -> if (maxPageNum == 0) 0 else 1
                        3 -> maxPageNum
                        else -> 1
                    }

                }
                "pageNum" -> {
                    onPageChange?.onPageChange(pageNum)
                    displayView()
                }

                else -> {
                    displayView()
                }  //刷新view
            }
        }
    }
}