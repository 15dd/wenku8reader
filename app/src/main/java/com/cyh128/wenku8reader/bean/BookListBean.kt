package com.cyh128.wenku8reader.bean

class BookListBean {
    var imgUrl: String? = null
    var title: String? = null
    var author: String? = null
    var other: String? = null
    var tags: String? = null
    var bookUrl: String
    val totalPage: Int

    constructor(
        imgUrl: String?,
        title: String?,
        author: String?,
        other: String?,
        tags: String?,
        bookUrl: String,
        totalPage: Int
    ) {
        this.imgUrl = imgUrl
        this.title = title
        this.author = author
        this.other = other
        this.tags = tags
        this.bookUrl = bookUrl
        this.totalPage = totalPage
    }

    constructor(bookUrl: String) {
        this.bookUrl = bookUrl
        totalPage = 1
    }

    val info: Unit
        get() {
            println(imgUrl + title + author + other + tags + bookUrl + totalPage)
        }
}
