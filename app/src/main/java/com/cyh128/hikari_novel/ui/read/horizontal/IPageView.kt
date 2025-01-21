package com.cyh128.hikari_novel.ui.read.horizontal

import android.graphics.Typeface
import com.cyh128.hikari_novel.data.model.HorizontalRead

interface IPageView {
    var pageNum: Int                    //页数
    var backgroundcolor: Int            //背景色
    var textColor : Int                 //字体颜色
    var txtFontType: Typeface           //正文字体类型//背景颜色
    var rowSpace: Float                 //行距
    var textSize: Float                 //正文部分默认画笔的大小
    var bottomTextSize: Float           //底部部分默认画笔的大小
    var content: HorizontalRead         //一个未分割章节

    var onNextChapter: OnNextChapter?          //下一章
    var onPreviousChapter: OnPreviousChapter?  //上一章
    var onCenterClick: OnCenterClick?          //点击view中间部分
    var onPageChange: OnPageChange?            //当翻页时调用，向前向后翻页，同一章内翻页，翻至其他章节都会调用

    @FunctionalInterface
    interface OnCenterClick {
        fun onCenterClick()
    }

    @FunctionalInterface
    interface OnNextChapter {
        fun onNextChapter()
    }

    @FunctionalInterface
    interface OnPreviousChapter {
        fun onPreviousChapter()
    }

    @FunctionalInterface
    interface OnPageChange {
        fun onPageChange(index: Int)
    }
}