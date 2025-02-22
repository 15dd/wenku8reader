package com.cyh128.hikari_novel.ui.read.horizontal

import android.graphics.Typeface
import com.cyh128.hikari_novel.data.model.HorizontalRead

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
 * Modified by 15dd on 2024 - 删除了用不到的函数以及变量
 */


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