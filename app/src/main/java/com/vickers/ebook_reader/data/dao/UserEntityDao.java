/* Created by Vickers Jian on 2019/08 */
package com.vickers.ebook_reader.data.dao;

import com.vickers.ebook_reader.data.Result;
import com.vickers.ebook_reader.data.entity.LibraryBookEntity;
import com.vickers.ebook_reader.data.entity.UserEntity;
import com.vickers.ebook_reader.data.entity.UserLibraryBookEntity;

import org.litepal.LitePal;

import java.util.ArrayList;
import java.util.List;

import nl.siegmann.epublib.domain.Book;

/**
 * 对数据库的UserEntity表的数据处理
 */
public class UserEntityDao {

    private UserEntity user;

    public UserEntityDao(UserEntity user) {
        this.user = user;
    }

    /**
     * 添加用户
     *
     * @param userId      用户名
     * @param diaplayName 昵称
     * @param password    密码
     * @return 用户在数据库中实例
     */
    public static Result<UserEntity> addUser(String userId, String diaplayName, String password) {
        if (findUserByUserId(userId) == null) {
            UserEntity user = new UserEntity(userId, diaplayName, password);
            try {
                user.saveThrows();
            } catch (Exception e) {
                return new Result.Error(e);
            }
            return new Result.Success<>(user);
        } else return new Result.Error(new Exception("注册失败，该用户名已存在"));
    }

    /**
     * 用户添加书籍
     *
     * @param bookList 要添加的书籍列表
     */
    public void addBook(List<LibraryBookEntity> bookList) {
        List<LibraryBookEntity> toAdd = LibraryBookEntityDao.addBook(bookList);
        new UserLibraryBookEntityDao(user).addBook(toAdd);
    }

    /**
     * 用户添加书籍
     *
     * @param book 要添加的书籍
     * @return 添加成功或失败
     */
    public Result<UserLibraryBookEntity> addBook(LibraryBookEntity book) {
        Result result = LibraryBookEntityDao.addBook(book);
        return addUserBook(result,LibraryBookEntityDao.findBook(book));
    }

    public Result<UserLibraryBookEntity> addBook(Book book, String bookUrl) {
        LibraryBookEntityDao libraryBookEntityDao = new LibraryBookEntityDao(book, bookUrl);
        Result result = libraryBookEntityDao.addBook();
        return addUserBook(result,libraryBookEntityDao.findBook());
    }

    private Result<UserLibraryBookEntity> addUserBook(Result result,LibraryBookEntity bookInDataBase){
        if (result instanceof Result.Success)
            return new UserLibraryBookEntityDao(user).addBook((LibraryBookEntity) ((Result.Success) result).getData());
        else if (bookInDataBase!= null) {
            return new UserLibraryBookEntityDao(user).addBook(bookInDataBase);
        } else {
            return new Result.Error(new Exception("无法添加该书"));
        }
    }

    public void deleteBook(LibraryBookEntity bookEntity) {
        new UserLibraryBookEntityDao(user).deleteBook(bookEntity);
    }

    /**
     * 处理用户登录事件
     *
     * @param userId   用户名
     * @param password 密码
     * @return 成功登录的用户在数据库中实例
     */
    public static Result<UserEntity> login(String userId, String password) {
        try {
            List<UserEntity> userlist = LitePal
                    .where("userId=? and password=?", userId, password).limit(1)
                    .find(UserEntity.class);
            if (userlist.isEmpty())
                throw new Exception("登录失败，用户名或密码错误");
            else return new Result.Success<>(userlist.get(0));
        } catch (Exception e) {
            return new Result.Error(e);
        }
    }

    /**
     * 通过用户名查找用户
     *
     * @param userId 用户名
     * @return 用户在数据库中实例
     */
    public static UserEntity findUserByUserId(String userId) {
        List<UserEntity> userList = LitePal.where("userId = ?", userId).find(UserEntity.class);
        if (userList == null || userList.size() == 0) {
            return null;
        } else {
            for (int i = 1; i < userList.size(); i++) {
                userList.get(i).delete();
            }
        }
        return userList.get(0);
    }

    /**
     * 获得用户所拥有的所有书籍
     *
     * @return List 用户的书籍列表
     */
    public List<LibraryBookEntity> getLibrary() {
        List<UserLibraryBookEntity> userLibraryBookEntityList = LitePal
                .where("userentity_id = ?", String.valueOf(user.getId()))
                .find(UserLibraryBookEntity.class);
        List<LibraryBookEntity> Books = new ArrayList<>();
        for (UserLibraryBookEntity user_book : userLibraryBookEntityList) {
            Books.add(LitePal.find(LibraryBookEntity.class, user_book.getLibrarybookentity_id()));
        }
        return Books;
    }
}
