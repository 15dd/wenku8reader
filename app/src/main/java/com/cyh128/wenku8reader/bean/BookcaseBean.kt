package com.cyh128.wenku8reader.bean

class BookcaseBean(
    var bid: String,
    var aid: String,
    var bookUrl: String,
    var title: String,
    var author: String,
    var lastchapter: String,
    var imgUrl: String
) {
    val info: Unit
        get() {
            println(bid + aid + bookUrl + title + author + lastchapter + imgUrl)
        }
}
