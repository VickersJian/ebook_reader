/* Created by Vickers Jian on 2019/07 */
package com.vickers.ebook_reader.data.entity;

import org.litepal.crud.LitePalSupport;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import nl.siegmann.epublib.domain.Book;

/**
 * 数据库LibraryBookEntity表：所有用户添加进app的书籍<p>
 * 书籍重复判定：书的路径(bookUrl)相同即认定为同一本书
 * 载入的书籍不能没有书名
 */
public class LibraryBookEntity extends LitePalSupport {

    private long id;
    private String title; //小说名
    private String author;//作者
    private String bookUrl;  //小说地址
    private String coverUrl; //小说封面

    private List<UserLibraryBookEntity> userList = new ArrayList<>();

    private LibraryBookEntity() {
    }

    public LibraryBookEntity(String title, String author, String bookUrl, String coverUrl) {
        this.title = title;
        this.author = author;
        this.bookUrl = bookUrl;
        this.coverUrl = coverUrl;
    }

    public long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getBookUrl() {
        return bookUrl;
    }

    public void setBookUrl(String bookUrl) {
        this.bookUrl = bookUrl;
    }

    public String getCoverUrl() {
        return coverUrl;
    }

    public void setCoverUrl(String coverUrl) {
        this.coverUrl = coverUrl;
    }

    public List<UserLibraryBookEntity> getUserList() {
        return userList;
    }

    //根据title和author重写equals和hashCode方法
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj instanceof LibraryBookEntity) {
            return bookUrl.equals(((LibraryBookEntity) obj).bookUrl);
        } else return false;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 17;
        result = prime * result + ((bookUrl == null) ? 0 : bookUrl.hashCode());
        return result;
    }


}
