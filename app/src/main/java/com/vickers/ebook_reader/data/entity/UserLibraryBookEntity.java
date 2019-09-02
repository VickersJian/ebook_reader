/* Created by Vickers Jian on 2019/07 */
package com.vickers.ebook_reader.data.entity;


import com.vickers.ebook_reader.data.entity.UserEntity;

import org.litepal.LitePal;
import org.litepal.crud.LitePalSupport;

import java.util.Date;

/**
 * 用户持有的书籍的信息<p>
 * UserEntity 和 LibraryBookEntity的关联表<p>
 */
public class UserLibraryBookEntity extends LitePalSupport {

    private long id;
    private int BookRateOfProgress;
    private long userentity_id;
    private long librarybookentity_id;
    private Date date;

    private UserEntity user;
    private LibraryBookEntity book;

    private UserLibraryBookEntity(){}

    public UserLibraryBookEntity(UserEntity user, LibraryBookEntity book,int BookRateOfProgress,Date date) {
        this.user = user;
        this.book = book;
        this.BookRateOfProgress=BookRateOfProgress;
        this.date=date;
    }


    public long getId() {
        return id;
    }

    public int getBookRateOfProgress() {
        return BookRateOfProgress;
    }

    public void setBookRateOfProgress(int bookRateOfProgress) {
        if (bookRateOfProgress >= 0 && bookRateOfProgress <= 100)
            BookRateOfProgress = bookRateOfProgress;
        else
            BookRateOfProgress = 0;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public long getUserentity_id() {
        return userentity_id;
    }

    public UserEntity getUser(){
        return user;
    }

    public UserLibraryBookEntity setUser(UserEntity user) {
        this.user = user;
        return this;
    }

    public long getLibrarybookentity_id() {
        return librarybookentity_id;
    }

    public LibraryBookEntity getBook() {
        return book;
    }

    public UserLibraryBookEntity setBook(LibraryBookEntity book) {
        this.book = book;
        return this;
    }
}
