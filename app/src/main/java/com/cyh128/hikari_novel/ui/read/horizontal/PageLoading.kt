package com.cyh128.hikari_novel.ui.read.horizontal

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface
import android.util.AttributeSet
import android.view.View

class PageLoading: View {
    private val mPaint by lazy {
        Paint().apply {
            isAntiAlias = true
            isDither = true
            isFilterBitmap = true
        }
    }
    var mBgColor = Color.WHITE
    var mTextColor: Int = Color.BLACK
    var mTxtFontType: Typeface = Typeface.DEFAULT
    var mTextSize = 50f
        set(value) {
            if(value > 20f) {
                field = value
            }
        }

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
        mPaint.textSize = mTextSize
        canvas.drawColor(mBgColor)

        //将提示语句放到正中间
        val fontMetrics = mPaint.fontMetrics
        val textHeight = fontMetrics.top - fontMetrics.bottom
        val textWidth = mPaint.measureText("加载章节中")
        val pivotX = (width - textWidth) / 2
        val pivotY = (height - textHeight) / 2
        canvas.drawText("加载章节中", pivotX, pivotY, mPaint)
    }
}