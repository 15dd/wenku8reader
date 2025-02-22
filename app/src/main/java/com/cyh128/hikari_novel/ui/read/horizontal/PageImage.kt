package com.cyh128.hikari_novel.ui.read.horizontal

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.graphics.Typeface
import android.util.AttributeSet
import android.view.MotionEvent
import androidx.appcompat.widget.AppCompatImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import kotlin.math.abs

/*
 * Copyright 2018 ya-b
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * This file includes code from NetNovelReader under the Apache License, Version 2.0.
 * The original source can be found at: https://github.com/ya-b/NetNovelReader/
 *
 * Modified by 15dd on 2024 - 增加图片支持
 */

class PageImage : AppCompatImageView {
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
    var mBottomTextSize = 45f
    var mTextSize = 55f
        set(value) {
            if (value > 20f) {
                field = value
            }
        }
    var mRowSpace = 2f  //行距

    var mTitle: String? = null
    var mPageNum: Int = 0
    var mMaxPageNum: Int = 0

    var imageRect: RectF? = null
    var mImageurl: String? = null
    lateinit var mImageClick: (String) -> Unit
    lateinit var mOnNextPage: () -> Unit
    lateinit var mOnPreviousPage: () -> Unit
    var mOnCenterClick: IPageView.OnCenterClick? = null

    private var isMoved = false                       //手势判断
    private var isTouching = false                    //手势判断
    private var moveStart = FloatArray(2)        //手势判断
    private var moveEnd = FloatArray(2)          //手势判断

    private var imageBitmap: Bitmap? = null
    private var isLoadFailed = false

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {
        imageRect = getBitmapRectF(imageBitmap)

        val x = event.x
        val y = event.y

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

        if (!isTouching && (!isMoved || (isMoved && abs(moveStart[0] - moveEnd[0]) < 5f && abs(moveStart[1] - moveEnd[1]) < 5f))) { //点击事件
            when {
                moveStart[0] > width * 3 / 5 -> {
                    mOnNextPage()
                }

                moveStart[0] < width * 2 / 5 -> {
                    mOnPreviousPage()
                }

                moveStart[1] > height * 2 / 5 && moveStart[1] < height * 3 / 5 -> {
                    if (!isTouching && imageRect?.contains(x, y) == true) {
                        mImageurl?.let {
                            mImageClick(it)
                        }
                        isTouching = false
                        return true
                    } else if (!isTouching) {
                        //处理点击在bitmap范围外的事件
                        mOnCenterClick?.onCenterClick()
                        return true
                    }
                    isTouching = false
                }

                moveStart[0] > width * 2 / 5 && moveStart[0] < height * 3 / 5 -> {
                    if (!isTouching && imageRect?.contains(x, y) == true) {
                        mImageurl?.let {
                            mImageClick(it)
                        }
                        isTouching = false
                        return true
                    } else if (!isTouching) {
                        //处理点击在bitmap范围外的事件
                        mOnCenterClick?.onCenterClick()
                        return true
                    }
                    isTouching = false
                }
            }
        }
        return super.onTouchEvent(event)
    }

    override fun onDraw(canvas: Canvas) {
        mPaint.color = mTextColor                             //字体颜色
        mPaint.typeface = mTxtFontType
        mPaint.textSize = mBottomTextSize
        canvas.drawColor(mBgColor)

        //底部右下角绘制：章节相关信息    格式为:   第 XXX 章节 YYY章节名  ：  n / 该章节总共页数
        val bottomText =
            "${mTitle ?: ""} ${if (mPageNum > mMaxPageNum) 0 else mPageNum}/$mMaxPageNum"
        canvas.drawText(
            bottomText,
            width - mPaint.measureText(bottomText) - getMarginLeft(),
            height - mBottomTextSize,
            mPaint
        )

        if (imageBitmap == null) {
            if (isLoadFailed) {
                val fontMetrics = mPaint.fontMetrics
                val textHeight = fontMetrics.top - fontMetrics.bottom
                val textWidth = mPaint.measureText("加载图片失败")
                val pivotX = (width - textWidth) / 2
                val pivotY = (height - textHeight) / 2
                mPaint.textSize = mTextSize
                canvas.drawText("加载图片失败", pivotX, pivotY, mPaint)

                imageRect = null
            } else {
                //将提示语句放到正中间
                val fontMetrics = mPaint.fontMetrics
                val textHeight = fontMetrics.top - fontMetrics.bottom
                val textWidth = mPaint.measureText("加载插图中")
                val pivotX = (width - textWidth) / 2
                val pivotY = (height - textHeight) / 2
                mPaint.textSize = mTextSize
                canvas.drawText("加载插图中", pivotX, pivotY, mPaint)

                imageRect = null
            }
        }

        super.onDraw(canvas)
    }

    @SuppressLint("CheckResult")
    fun updateImage() {
        if (imageBitmap != null) return

        isLoadFailed = false
        imageBitmap = null

        Glide.with(this)
            .asBitmap()
            .load(mImageurl)
            .diskCacheStrategy(DiskCacheStrategy.ALL)
            .addListener(object : RequestListener<Bitmap> {
                override fun onLoadFailed(
                    e: GlideException?,
                    model: Any?,
                    target: Target<Bitmap>,
                    isFirstResource: Boolean
                ): Boolean {
                    isLoadFailed = true
                    imageBitmap = null
                    invalidate()
                    return true
                }

                override fun onResourceReady(
                    resource: Bitmap,
                    model: Any,
                    target: Target<Bitmap>?,
                    dataSource: DataSource,
                    isFirstResource: Boolean
                ): Boolean {
                    imageBitmap = resource
                    return false
                }
            })
            .into(this)
    }

    private fun getBitmapRectF(bitmap: Bitmap?): RectF? {
        if (bitmap == null) return null

        val viewWidth = width.toFloat()
        val viewHeight = height.toFloat()

        val bitmapWidth = bitmap.width.toFloat()
        val bitmapHeight = bitmap.height.toFloat()

        val scale: Float
        val dx: Float
        val dy: Float

        when (scaleType) {
            ScaleType.FIT_XY -> {
                scale = 1f
                dx = 0f
                dy = 0f
            }

            ScaleType.CENTER_CROP -> {
                if (bitmapWidth * viewHeight > viewWidth * bitmapHeight) {
                    scale = viewHeight / bitmapHeight
                    dx = (viewWidth - bitmapWidth * scale) * 0.5f
                    dy = 0f
                } else {
                    scale = viewWidth / bitmapWidth
                    dx = 0f
                    dy = (viewHeight - bitmapHeight * scale) * 0.5f
                }
            }

            ScaleType.CENTER_INSIDE -> {
                if (bitmapWidth <= viewWidth && bitmapHeight <= viewHeight) {
                    scale = 1f
                    dx = (viewWidth - bitmapWidth) * 0.5f
                    dy = (viewHeight - bitmapHeight) * 0.5f
                } else {
                    if (bitmapWidth * viewHeight > viewWidth * bitmapHeight) {
                        scale = viewWidth / bitmapWidth
                        dx = 0f
                        dy = (viewHeight - bitmapHeight * scale) * 0.5f
                    } else {
                        scale = viewHeight / bitmapHeight
                        dx = (viewWidth - bitmapWidth * scale) * 0.5f
                        dy = 0f
                    }
                }
            }

            ScaleType.FIT_CENTER, ScaleType.CENTER -> {
                if (bitmapWidth * viewHeight > viewWidth * bitmapHeight) {
                    scale = viewWidth / bitmapWidth
                    dx = 0f
                    dy = (viewHeight - bitmapHeight * scale) * 0.5f
                } else {
                    scale = viewHeight / bitmapHeight
                    dx = (viewWidth - bitmapWidth * scale) * 0.5f
                    dy = 0f
                }
            }

            else -> {
                // 默认情况（例如ScaleType.FIT_START, FIT_END等）
                if (bitmapWidth * viewHeight > viewWidth * bitmapHeight) {
                    scale = viewWidth / bitmapWidth
                    dx = 0f
                    dy = (viewHeight - bitmapHeight * scale) * 0.5f
                } else {
                    scale = viewHeight / bitmapHeight
                    dx = (viewWidth - bitmapWidth * scale) * 0.5f
                    dy = 0f
                }
            }
        }

        return RectF(dx, dy, dx + bitmapWidth * scale, dy + bitmapHeight * scale)
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