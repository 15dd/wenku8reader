package com.cyh128.hikari_novel.ui.read.horizontal

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface
import android.util.AttributeSet
import android.view.View

class PageText : View {
    private val mPaint by lazy {
        Paint().apply {
            isAntiAlias = true
            isDither = true
            isFilterBitmap = true
        }
    }
    var mBgColor = Color.WHITE
    var mTextArray: MutableList<String>? = null
    var mTextColor: Int = Color.BLACK
    var mTxtFontType: Typeface = Typeface.DEFAULT
    var mBottomTextSize = 45f
    var mTextSize = 55f
        set(value) {
            if(value > 20f) {
                field = value
            }
        }
    var mRowSpace = 2f  //行距

    var mTitle: String? = null
    var mPageNum: Int = 0
    var mMaxPageNum: Int = 0

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )

    override fun onDraw(canvas: Canvas) {
        mPaint.color = mTextColor                             //字体颜色
        mPaint.typeface = mTxtFontType
        mPaint.textSize = mBottomTextSize
        canvas.drawColor(mBgColor)
        //底部右下角绘制：章节相关信息    格式为:   第 XXX 章节 YYY章节名  ：  n / 该章节总共页数
        val bottomText = "${mTitle ?: ""} ${if(mPageNum > mMaxPageNum) 0 else mPageNum}/$mMaxPageNum"
        canvas.drawText(
            bottomText,
            width - mPaint.measureText(bottomText) - getMarginLeft(),
            height - mBottomTextSize,
            mPaint
        )
        if (mTextArray == null || mMaxPageNum < 1) return              //正文内容缺乏，直接不绘制了
        mPaint.textSize = mTextSize                                       //正文部分画笔大小
        //绘制正文
        for (i in 0 until (mTextArray?.size ?: 0)) {
            canvas.drawText(
                mTextArray!![i].replace(" ", "  "),
                getMarginLeft(),
                getMarginTop() + i * mTextSize * mRowSpace,
                mPaint
            )
        }
    }

    //左边距
    private fun getMarginLeft(): Float {
        val count = getTextWidth() / mTextSize.toInt()   //一行字数
        return (width - count * mTextSize) / 2
    }

    //上边距
    private fun getMarginTop(): Float {
        val count = getTextHeight() / (mTextSize * mRowSpace).toInt()   //一列字数
        return ((height - mBottomTextSize) - count * mTextSize * mRowSpace) / 2 + mTextSize
    }

    //正文区域宽度
    private fun getTextWidth(): Int = (width * 0.96f).toInt()

    //正文区域高度
    private fun getTextHeight(): Int = ((height - mBottomTextSize) * 0.96f).toInt()

}