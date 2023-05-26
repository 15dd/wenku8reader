package com.cyh128.wenku8reader.classLibrary;

public class BookListClass {
    public String imgUrl;
    public String title;
    public String author;
    public String other;
    public String tags;
    public String bookUrl;
    final public int totalPage;

    public BookListClass(String imgUrl, String title, String author, String other, String tags, String bookUrl, int totalPage) {
        this.imgUrl = imgUrl;
        this.title = title;
        this.author = author;
        this.other = other;
        this.tags = tags;
        this.bookUrl = bookUrl;
        this.totalPage = totalPage;
    }

    public void getInfo() {
        System.out.println(imgUrl + title + author + other + tags + bookUrl + totalPage);
    }
}
