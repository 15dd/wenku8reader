package com.cyh128.wenku8reader.reader

import android.content.Context
import android.content.Intent
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import com.cyh128.wenku8reader.activity.PhotoViewActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch


class PageImage : androidx.appcompat.widget.AppCompatImageView {
    private val mPaint by lazy {
        Paint().apply {
            isAntiAlias = true
            isDither = true
            isFilterBitmap = false
        }
    }
    var mTextColor: Int = Color.BLACK
    var mBottomTextSize = 35f
    var mPageNum: Int = 0
    var mMaxPageNum: Int = 0
    var mIsDrawTime = false
    var mTitle: String? = null
    var mTextSize = 55f
        set(value) {
            if (value > 20f) {
                field = value
            }
        }

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)

    override fun onDraw(canvas: Canvas?) {
        mPaint.textSize = mBottomTextSize
        mPaint.color = mTextColor
        //底部右下角绘制：章节相关信息    格式为:   第 XXX 章节 YYY章节名  ：  n / 该章节总共页数
        val bottomText =
            "${mTitle ?: ""} ${if (mPageNum > mMaxPageNum) 0 else mPageNum}/$mMaxPageNum"
        canvas!!.drawText(
            bottomText,
            width - mPaint.measureText(bottomText) - getMarginLeft(),
            height - mBottomTextSize,
            mPaint
        )

        //mBitmap.toDrawable()
        //canvas.drawBitmap(mBitmap, ,, mPaint)
        //setImageDrawable()
        super.onDraw(canvas)
    }

    //左边距
    private fun getMarginLeft(): Float {
        val count = getTextWidth() / mTextSize.toInt()   //一行字数
        return (width - count * mTextSize) / 2
    }

    //正文区域宽度
    private fun getTextWidth(): Int = (width * 0.96f).toInt()

}