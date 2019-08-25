/* Created by Vickers Jian on 2019/08 */
package com.vickers.ebook_reader.data.dao;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;

import com.vickers.ebook_reader.utils.FileUtils;
import com.vickers.ebook_reader.data.Result;
import com.vickers.ebook_reader.data.entity.LibraryBookEntity;
import com.vickers.ebook_reader.data.entity.UserEntity;
import com.vickers.ebook_reader.data.entity.UserLibraryBookEntity;

import org.litepal.LitePal;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

import nl.siegmann.epublib.domain.Author;
import nl.siegmann.epublib.domain.Book;

import static com.vickers.ebook_reader.utils.FileUtils.isFileExist;
import static com.vickers.ebook_reader.utils.FileUtils.readStream;

/**
 * 对数据库的LibraryBookEntity表的数据处理
 */
public class LibraryBookEntityDao {

    private static final String TAG = "LibraryBookEntityDao";

    private Book book;
    private String title;
    private String author;
    private String bookUrl;
    public String coverUrl;

    public LibraryBookEntityDao(Book book, String bookUrl) {
        this.book = book;
        this.bookUrl = bookUrl;
        title = book.getMetadata().getFirstTitle();
        List<Author> authors = book.getMetadata().getAuthors();
        if (authors.isEmpty()) {
            author = "";
        } else {
            author = authors.get(0).toString();
        }
    }

    /**
     * 向数据库中添加书籍
     *
     * @param title    书名
     * @param author   作者
     * @param bookUrl  书籍地址
     * @param coverUrl 封面地址
     * @return 数据库中的实例(即带有id)
     */
    public static Result<LibraryBookEntity> addBook(String title, String author, String bookUrl, String coverUrl) {
        if (findBook(title, author) == null) {
            LibraryBookEntity libraryBook = new LibraryBookEntity(title, author, bookUrl, coverUrl);
            try {
                libraryBook.saveThrows();
            } catch (Exception e) {
                return new Result.Error(e);
            }
            return new Result.Success<>(libraryBook);
        } else return new Result.Error(new Exception("该书已存在"));
    }

    /**
     * @param book 要添加的书籍
     * @see LibraryBookEntityDao#addBook(String title, String author, String bookUrl, String coverUrl)  addBook的重载
     */
    public static Result<LibraryBookEntity> addBook(LibraryBookEntity book) {
        return addBook(book.getTitle(), book.getAuthor(), book.getBookUrl(), book.getCoverUrl());
    }

    /**
     * 将书籍添加到数据库中，返回数据库中的实例
     *
     * @param bookList 要添加的书籍序列
     * @return 数据库中的实例(即带有id)
     */
    public static List<LibraryBookEntity> addBook(List<LibraryBookEntity> bookList) {
        //将迭代器游标定位到List末尾元素后一位，向前遍历，删除重复元素(书籍)
        ListIterator<LibraryBookEntity> it = bookList.listIterator(bookList.size());
        while (it.hasPrevious()) {
            LibraryBookEntity book = it.previous();
            if (bookList.indexOf(book) != bookList.lastIndexOf(book)) {
                it.remove();
            }
        }
        //确定要添加到数据库的书，已在数据库中的将替换为从数据库中的实例
        it = bookList.listIterator();
        List<LibraryBookEntity> toSave = new ArrayList<>();
        while (it.hasNext()) {
            LibraryBookEntity book = it.next();
            LibraryBookEntity origin = findBook(book.getTitle(), book.getAuthor());
            if (origin == null) {
                toSave.add(book);
            } else {
                it.set(origin);
            }
        }
        LitePal.saveAll(toSave);
        //返回用户要添加的书籍
        it = bookList.listIterator();
        for (int i = 0; it.hasNext(); ) {
            LibraryBookEntity book = it.next();
            if (book.getId() == 0) {
                it.set(toSave.get(i));
                i++;
            }
        }
        return bookList;
    }

    /**
     * 删除指定书籍
     *
     * @param title
     * @param author
     */
    public static void deleteBook(String title, String author) {
        LibraryBookEntity bookEntity = findBook(title, author);
        if (bookEntity != null) {
            LitePal.delete(LibraryBookEntity.class, bookEntity.getId());
        }
    }

    /**
     * 查找书籍
     *
     * @param title  书名
     * @param author 作者
     * @return 数据库中的实例
     */
    public static LibraryBookEntity findBook(String title, String author) {
        List<LibraryBookEntity> bookList = LitePal
                .where("title = ? and author = ?", title, author)
                .find(LibraryBookEntity.class);
        if (bookList == null || bookList.size() == 0) {
            return null;
        } else {
            for (int i = 1; i < bookList.size(); i++) {
                bookList.get(i).delete();
            }
        }
        return bookList.get(0);
    }

    /**
     * @param book 要查找的书籍
     * @return 数据库中的实例
     * @see #findBook(String, String) findBook(String, String)的重载
     */
    public static LibraryBookEntity findBook(LibraryBookEntity book) {
        return findBook(book.getTitle(), book.getAuthor());
    }


    /**
     * 获得指定书籍的所有用户
     *
     * @return List UserEntity 用户列表
     */
    public List<UserEntity> getUsers(LibraryBookEntity libraryBookEntity) {
        List<UserLibraryBookEntity> userLibraryBookEntityList = LitePal.where("librarybookentity_id = ?", String.valueOf(libraryBookEntity.getId())).find(UserLibraryBookEntity.class);
        List<UserEntity> Users = new ArrayList<>();
        for (UserLibraryBookEntity book_user : userLibraryBookEntityList) {
            Users.add(LitePal.find(UserEntity.class, book_user.getUserentity_id()));
        }
        return Users;
    }

    /**
     * 添加书籍到数据库
     */
    public Result<LibraryBookEntity> addBook() {
        title = book.getMetadata().getFirstTitle();
        if (title.equals("")) {
            return new Result.Error(new Exception("添加的书籍无书名"));
        }
        coverUrl = getCoverUrl();
        return addBook(title, author, bookUrl, coverUrl);
    }

    /**
     * 从数据库中删除书籍
     */
    public void deleteBook() {
        deleteBook(title, author);
    }

    /**
     * @return 数据库中的实例
     */
    public LibraryBookEntity findBook() {
        return findBook(title, author);
    }

    /**
     * 将封面写入缓存，返回缓存封面uri
     */
    private String getCoverUrl() {
        String cachePath = FileUtils.getCachePath() + File.separator + "cover";
        FileUtils.createFolderIfNotExists(cachePath);
        String coverUrl = cachePath + File.separator + title.hashCode() + ".jpg";
        if (isFileExist(coverUrl)) {
            Log.i(TAG, "getCoverUrl: " + coverUrl);
            return coverUrl;
        } else {
            return extractEpubCoverImage(cachePath);
        }
    }

    /**
     * 将封面写入缓存
     */
    private String extractEpubCoverImage(String cachePath) {
        String coverCacheUrl = "";
        InputStream inputStream = null;
        try {
            inputStream = book.getCoverImage().getInputStream();
        } catch (IOException e) {
            Log.i(TAG, "extractEpubCoverImage: " + e.getMessage());
            e.printStackTrace();
        }
        if (inputStream == null) {
            throw new RuntimeException("stream is null");
        } else {
            try {
                byte[] data = readStream(inputStream);
                if (data != null) {
                    Bitmap bitmapCover = BitmapFactory.decodeByteArray(data, 0, data.length);
                    FileUtils.createFolderIfNotExists(cachePath);
                    coverCacheUrl = cachePath + File.separator + title.hashCode() + ".jpg";
                    FileOutputStream out = new FileOutputStream(new File(coverCacheUrl));
                    bitmapCover.compress(Bitmap.CompressFormat.JPEG, 90, out);
                    out.flush();
                    out.close();
                    inputStream.close();
                }
            } catch (Exception e) {
                Log.i(TAG, "extractEpubCoverImage: " + e.getMessage());
                e.printStackTrace();
            }
        }
        return coverCacheUrl;
    }
}