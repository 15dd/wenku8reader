package com.cyh128.wenku8reader.classLibrary;

public class BookcaseClass {
    public String bid;
    public String aid;
    public String bookUrl;
    public String title;
    public String author;
    public String lastchapter;
    public String imgUrl;

    public BookcaseClass(String bid, String aid, String bookUrl, String title, String author, String lastchapter,String imgUrl) {
        this.bid = bid;
        this.aid = aid;
        this.bookUrl = bookUrl;
        this.title = title;
        this.author = author;
        this.lastchapter = lastchapter;
        this.imgUrl = imgUrl;
    }

    public void getInfo() {
        System.out.println(bid + aid + bookUrl + title + author + lastchapter + imgUrl);
    }
}
