/* Created by Vickers Jian on 2019/08 */
package com.vickers.ebook_reader.data.dao;

import android.content.ContentValues;
import android.util.Log;

import com.vickers.ebook_reader.Base.Result;
import com.vickers.ebook_reader.data.entity.LibraryBookEntity;
import com.vickers.ebook_reader.data.entity.UserEntity;
import com.vickers.ebook_reader.data.entity.UserLibraryBookEntity;

import org.litepal.LitePal;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.ListIterator;

import static com.vickers.ebook_reader.data.dao.LibraryBookEntityDao.findBook;
import static com.vickers.ebook_reader.data.dao.UserEntityDao.findUserByUserId;

/**
 * 对数据库的LibraryBookEntity表的数据处理
 */
public class UserLibraryBookEntityDao {

    private UserEntity user;
    private UserLibraryBookEntity userLibraryBookEntity;

    public UserLibraryBookEntityDao(UserLibraryBookEntity userLibraryBookEntity) {
        this.userLibraryBookEntity = userLibraryBookEntity;
    }

    public UserLibraryBookEntityDao(UserEntity user) {
        this.user = user;
    }

    /**
     * 添加用户和书籍的关联
     *
     * @param user 用户
     * @param book 书籍
     * @return 数据库中的实例
     */
    public static Result<UserLibraryBookEntity> addUserLibraryBookEntity(UserEntity user, LibraryBookEntity book) {
        if (findUserLibraryBookEntity(user, book) == null) {
            UserLibraryBookEntity userLibraryBookEntity = new UserLibraryBookEntity(user, book, 0, getDate());
            try {
                userLibraryBookEntity.saveThrows();
            } catch (Exception e) {
                return new Result.Error(e);
            }
            return new Result.Success<>(userLibraryBookEntity);
        } else
            return new Result.Error(new Exception("该书已存在"));
    }

    public static Result<UserLibraryBookEntity> updataUserLibraryBookEntity(UserLibraryBookEntity newUserLibraryBook) {
        try {
            boolean isChanged = false;
            UserLibraryBookEntity userLibraryBookEntity = findUserLibraryBookEntity(newUserLibraryBook);
            if (userLibraryBookEntity != null) {
                ContentValues values = new ContentValues();
                if (!newUserLibraryBook.getBookType().equals(userLibraryBookEntity.getBookType())) {
                    isChanged = true;
                    values.put("booktype", newUserLibraryBook.getBookType());
                }
                if (newUserLibraryBook.getBookRateOfProgress() != userLibraryBookEntity.getBookRateOfProgress()) {
                    isChanged = true;
                    values.put("bookrateofprogress", newUserLibraryBook.getBookRateOfProgress());
                }
                if (isChanged)
                    LitePal.update(UserLibraryBookEntity.class, values, userLibraryBookEntity.getId());
                return new Result.Success<>(newUserLibraryBook);
            } else throw new Exception("未找到该关系");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result.Error(e);
        }
    }
private static final String TAG = "UserLibrary";

    public static void deleteUserLibraryBookEntity(UserEntity user, LibraryBookEntity book) {
        UserLibraryBookEntity userLibraryBookEntity = findUserLibraryBookEntity(user, book);
        if (userLibraryBookEntity != null) {
            LitePal.delete(UserLibraryBookEntity.class, userLibraryBookEntity.getId());
        }
    }

    /**
     * 添加用户和书籍的关联
     *
     * @param userLibraryBookEntityList 关系列表
     */
    public static void addUserLibraryBookEntity(List<UserLibraryBookEntity> userLibraryBookEntityList) {
        List<UserLibraryBookEntity> toSave = new ArrayList<>();
        for (UserLibraryBookEntity user_book : userLibraryBookEntityList) {
            if (findUserLibraryBookEntity(user_book) == null) {
                toSave.add(user_book);
            }
        }
        LitePal.saveAll(toSave);
    }

    /**
     * 查找关联表
     *
     * @param user 用户
     * @param book 书籍
     * @return 数据库中的实例
     */
    public static UserLibraryBookEntity findUserLibraryBookEntity(UserEntity user, LibraryBookEntity book) {
        UserEntity userEntity;
        LibraryBookEntity bookEntity;
        if (user.getId() != 0 && book.getId() != 0) {
            userEntity = user;
            bookEntity = book;
        } else if (user.getId() == 0 && book.getId() != 0) {
            userEntity = findUserByUserId(user.getUserID());
            bookEntity = book;
        } else if (user.getId() != 0 && book.getId() == 0) {
            userEntity = user;
            bookEntity = findBook(book);
        } else {
            userEntity = findUserByUserId(user.getUserID());
            bookEntity = findBook(book);
        }
        if (userEntity != null && bookEntity != null) {
            List<UserLibraryBookEntity> userLibraryBookEntityList = LitePal
                    .where("userentity_id = ? and librarybookentity_id = ?", String.valueOf(userEntity.getId()), String.valueOf(bookEntity.getId()))
                    .find(UserLibraryBookEntity.class);
            if (userLibraryBookEntityList == null || userLibraryBookEntityList.size() == 0) {
                return null;
            } else {
                for (int i = 1; i < userLibraryBookEntityList.size(); i++) {
                    userLibraryBookEntityList.get(i).delete();
                }
            }
            return userLibraryBookEntityList.get(0);
        }
        return null;
    }

    /**
     * @param userLibraryBookEntity 关联关系
     * @see #findUserLibraryBookEntity(UserEntity, LibraryBookEntity) findUserLibraryBookEntity(UserEntity user, LibraryBookEntity book)的重载
     */
    public static UserLibraryBookEntity findUserLibraryBookEntity(UserLibraryBookEntity userLibraryBookEntity) {
        return findUserLibraryBookEntity(userLibraryBookEntity.getUser(), userLibraryBookEntity.getBook());
    }

    /**
     * 添加用户和书籍的关联
     *
     * @param book 书籍
     * @return 数据库中的实例
     */
    public Result<UserLibraryBookEntity> addBook(LibraryBookEntity book) {
        return addUserLibraryBookEntity(user, book);
    }


    /**
     * 添加用户和书籍的关联
     *
     * @param bookList 书籍列表
     */
    public void addBook(List<LibraryBookEntity> bookList) {
        List<UserLibraryBookEntity> userLibraryBookEntityList = new ArrayList<>();
        for (LibraryBookEntity book : bookList) {
            userLibraryBookEntityList.add(new UserLibraryBookEntity(user, book, 0, getDate()));
        }
        addUserLibraryBookEntity(userLibraryBookEntityList);
    }

    public void deleteBook(LibraryBookEntity bookEntity) {
        deleteUserLibraryBookEntity(user, bookEntity);
    }


    public void setProgress(int progress) {
        userLibraryBookEntity.setBookRateOfProgress(progress);
    }

    private static Date getDate() {
        return new Date(System.currentTimeMillis());
    }

    public static List<String> getBookTypeList(UserEntity user) {
        List<String> bookTypeList = new ArrayList<>();
        UserEntity userEntity;
        if (user.getId() != 0) {
            userEntity = user;
        } else {
            userEntity = findUserByUserId(user.getUserID());
            if (userEntity == null) {
                return bookTypeList;
            }
        }
        List<UserLibraryBookEntity> userLibraryBookEntityList = LitePal.where("userentity_id = ?",
                String.valueOf(userEntity.getId())).find(UserLibraryBookEntity.class);
        for (UserLibraryBookEntity type : userLibraryBookEntityList) {
            bookTypeList.add(type.getBookType());
        }
        //去除重复
        ListIterator<String> it = bookTypeList.listIterator(bookTypeList.size());
        while (it.hasPrevious()) {
            String type = it.previous();
            if (bookTypeList.indexOf(type) != bookTypeList.lastIndexOf(type)) {
                it.remove();
            }
        }
        return bookTypeList;
    }

}
