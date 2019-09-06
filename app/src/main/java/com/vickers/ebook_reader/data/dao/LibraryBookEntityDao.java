/* Created by Vickers Jian on 2019/08 */
package com.vickers.ebook_reader.data.dao;

import android.content.ContentValues;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import com.vickers.ebook_reader.utils.FileUtils;
import com.vickers.ebook_reader.Base.Result;
import com.vickers.ebook_reader.data.entity.LibraryBookEntity;
import com.vickers.ebook_reader.data.entity.UserEntity;
import com.vickers.ebook_reader.data.entity.UserLibraryBookEntity;

import org.litepal.LitePal;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

import nl.siegmann.epublib.domain.Author;
import nl.siegmann.epublib.domain.Book;
import nl.siegmann.epublib.domain.Metadata;
import nl.siegmann.epublib.epub.EpubReader;
import nl.siegmann.epublib.epub.EpubWriter;

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
    private String coverUrl;

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

    public LibraryBookEntityDao(LibraryBookEntity book) {
        this.title = book.getTitle();
        this.author = book.getAuthor();
        this.bookUrl = book.getBookUrl();
        this.coverUrl = book.getCoverUrl();
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
        if (findBook(bookUrl) == null) {
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
            LibraryBookEntity origin = findBook(book.getBookUrl());
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
    public static void deleteBook(String bookUrl) {
        LibraryBookEntity bookEntity = findBook(bookUrl);
        if (bookEntity != null) {
            LitePal.delete(LibraryBookEntity.class, bookEntity.getId());
        }
    }

    public static Result<LibraryBookEntity> updataBook(LibraryBookEntity book) {
        try {
            boolean isChanged = false;
            LibraryBookEntity bookEntity = findBook(book);
            if (bookEntity != null) {
                if (!book.getTitle().equals("")) {
                    ContentValues values = new ContentValues();
                    if (!bookEntity.getTitle().equals(book.getTitle())) {
                        isChanged = true;
                        values.put("title", book.getTitle());
                    }
                    if (!bookEntity.getAuthor().equals(book.getAuthor())) {
                        isChanged = true;
                        values.put("author", book.getAuthor());
                    }
                    if (!bookEntity.getCoverUrl().equals(book.getCoverUrl())) {
                        String cachePath = FileUtils.getCachePath() + File.separator + "cover";
                        Result result = LibraryBookEntityDao.extractEpubCoverImage(cachePath, bookEntity.getCoverUrl(), book.getCoverUrl());
                        if (result instanceof Result.Success) {
                            isChanged = true;
                            values.put("coverurl", (String) ((Result.Success) result).getData());
                        } else throw ((Result.Error) result).getError();
                    }
                    if (isChanged) {
                        EpubReader reader = new EpubReader();
                        InputStream in = new FileInputStream(new File(book.getBookUrl()));
                        Book epubBook = reader.readEpub(in);
                        in.close();
                        Metadata metadata = new Metadata();
                        if (!book.getAuthor().equals("")) {
                            Author author = new Author(book.getAuthor());
                            List<Author> authorList = new ArrayList<>();
                            authorList.add(author);
                            metadata.setAuthors(authorList);
                        }
                        if (!book.getTitle().equals("")) {
                            List<String> titleList = new ArrayList<>();
                            titleList.add(book.getTitle());
                            metadata.setTitles(titleList);
                        }
                        epubBook.setMetadata(metadata);
                        EpubWriter writer = new EpubWriter();
                        writer.write(epubBook, new FileOutputStream(new File(book.getBookUrl())));
                        LitePal.update(LibraryBookEntity.class, values, book.getId());
                    }
                    return new Result.Success<>(book);
                } else throw new Exception("书名不能为空");
            } else throw new Exception("该书不在数据库中");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result.Error(e);
        }
    }

    /**
     * 查找书籍
     *
     * @return 数据库中的实例
     */
    public static LibraryBookEntity findBook(String bookUrl) {
        List<LibraryBookEntity> bookList = LitePal
                .where("bookurl = ? ", bookUrl)
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
     */
    public static LibraryBookEntity findBook(LibraryBookEntity book) {
        return findBook(book.getBookUrl());
    }


    /**
     * 获得指定书籍的所有用户
     *
     * @return List UserEntity 用户列表
     */
    public static List<UserEntity> getUsers(LibraryBookEntity book) {
        LibraryBookEntity bookEntity = findBook(book.getBookUrl());
        if (bookEntity != null) {
            List<UserLibraryBookEntity> userLibraryBookEntityList = LitePal.where("librarybookentity_id = ?", String.valueOf(bookEntity.getId())).find(UserLibraryBookEntity.class);
            List<UserEntity> Users = new ArrayList<>();
            for (UserLibraryBookEntity book_user : userLibraryBookEntityList) {
                Users.add(LitePal.find(UserEntity.class, book_user.getUserentity_id()));
            }
            return Users;
        }
        return null;
    }

    /**
     * 添加书籍到数据库
     */
    public Result<LibraryBookEntity> addBook() {
        if (title.equals("")) {
            return new Result.Error(new Exception("添加的书籍无书名"));
        } else if (!FileUtils.isFileExist(bookUrl)) {
            return new Result.Error(new Exception("未找到该文件"));
        }
        try {
            coverUrl = getCoverUrl();
            return addBook(title, author, bookUrl, coverUrl);
        } catch (Exception e) {
            return new Result.Error(e);
        }
    }

    /**
     * 从数据库中删除书籍
     */
    public void deleteBook() {
        deleteBook(bookUrl);
    }

    /**
     * @return 数据库中的实例
     */
    public LibraryBookEntity findBook() {
        return findBook(bookUrl);
    }

    /**
     * 将封面写入缓存，返回缓存封面uri
     */
    private String getCoverUrl() throws Exception {
        String cachePath = FileUtils.getCachePath() + File.separator + "cover";
        return extractEpubCoverImage(cachePath);
    }

    /**
     * 将封面写入缓存
     */
    private String extractEpubCoverImage(String cachePath) throws Exception {
        String coverCacheUrl = "";
        if (book == null) {
            getBookFromBookUrl(bookUrl);
        }
        InputStream inputStream = book.getCoverImage().getInputStream();
        if (inputStream == null) {
            throw new RuntimeException("未找到封面");
        } else {
            byte[] data = readStream(inputStream);
            if (data != null) {
                Bitmap bitmapCover = BitmapFactory.decodeByteArray(data, 0, data.length);
                FileUtils.createFolderIfNotExists(cachePath);
                coverCacheUrl = cachePath + File.separator + title.hashCode() + System.currentTimeMillis() + ".jpg";
                FileOutputStream out = new FileOutputStream(new File(coverCacheUrl));
                bitmapCover.compress(Bitmap.CompressFormat.JPEG, 90, out);
                out.flush();
                out.close();
                inputStream.close();
            }
        }
        return coverCacheUrl;
    }

    public static Book getBookFromBookUrl(String bookUrl) throws Exception {
        if (FileUtils.isFileExist(bookUrl)) {
            EpubReader epubReader = new EpubReader();
            InputStream in = new FileInputStream(new File(bookUrl));
            Book book = epubReader.readEpub(in);
            in.close();
            return book;
        } else throw new Exception(MessageFormat.format("未找到地址为：{0}的文件", bookUrl));
    }

    public static InputStream getBookCoverInputStream(Book book) throws Exception {
        if (book != null) {
            return book.getCoverImage().getInputStream();
        } else throw new Exception("Book == null");
    }

    public static Result<String> extractEpubCoverImage(String cachePath, String oldImagePath, String imagePath) {
        String coverCacheUrl = "";
        try {
            if (imagePath != null && !imagePath.equals("")) {
                Bitmap bitmapCover = BitmapFactory.decodeFile(imagePath);
                FileUtils.createFolderIfNotExists(cachePath);
                coverCacheUrl = cachePath + File.separator + imagePath.hashCode() + System.currentTimeMillis() + ".jpg";
                FileUtils.renameFile(oldImagePath, coverCacheUrl);
                FileOutputStream out = new FileOutputStream(new File(coverCacheUrl));
                bitmapCover.compress(Bitmap.CompressFormat.JPEG, 90, out);
                out.flush();
                out.close();
            }
            return new Result.Success<>(coverCacheUrl);
        } catch (Exception e) {
            Log.i(TAG, "extractEpubCoverImage: " + e.getMessage());
            return new Result.Error(e);
        }

    }
}