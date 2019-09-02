/* Created by Vickers Jian on 2019/07 */
package com.vickers.ebook_reader;

import android.app.Application;
import android.content.SharedPreferences;
import android.os.AsyncTask;

import com.vickers.ebook_reader.data.dao.LibraryBookEntityDao;
import com.vickers.ebook_reader.data.dao.UserLibraryBookEntityDao;
import com.vickers.ebook_reader.data.entity.LibraryBookEntity;
import com.vickers.ebook_reader.data.entity.UserEntity;
import com.vickers.ebook_reader.utils.FileUtils;

import org.litepal.LitePal;

import java.io.File;
import java.io.FileFilter;
import java.util.List;
import java.util.ListIterator;

import static com.vickers.ebook_reader.data.dao.UserEntityDao.findUserByUserId;
import static org.litepal.LitePal.findAll;

public class MyApplication extends Application {

    private UserEntity user = null;
    private SharedPreferences UserPreferences;
    private static MyApplication instance;

    @Override
    public void onCreate() {
        super.onCreate();
        LitePal.initialize(this);
        instance = this;
        UserPreferences = getSharedPreferences("User", MODE_PRIVATE);
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                final List<LibraryBookEntity> Library = LitePal.findAll(LibraryBookEntity.class);
                ListIterator<LibraryBookEntity> it = Library.listIterator();
                while (it.hasNext()) {
                    LibraryBookEntity book = it.next();
                    if (!FileUtils.isFileExist(book.getBookUrl())) {
                        List<UserEntity> userList = LibraryBookEntityDao.getUsers(book);
                        if (userList != null)
                            for (UserEntity user : userList) {
                                UserLibraryBookEntityDao.deleteUserLibraryBookEntity(user, book);
                            }
                        LitePal.delete(LibraryBookEntity.class, book.getId());
                        it.remove();
                    }
                }
                File coverCacheFile = new File(FileUtils.getCachePath() + File.separator + "cover");
                if (coverCacheFile.exists()) {
                    File[] deletes = coverCacheFile.listFiles(new FileFilter() {
                        @Override
                        public boolean accept(File pathname) {
                            for (LibraryBookEntity bookEntity : Library) {
                                if (pathname.getAbsolutePath().equals(bookEntity.getCoverUrl()))
                                    return false;
                            }
                            return true;
                        }
                    });
                    for (File delete : deletes) {
                        delete.delete();
                    }
                }
            }
        });
    }

    public static MyApplication getInstance() {
        return instance;
    }

    public static void setUser(UserEntity user) {
        getInstance().user = user;
    }

    public static UserEntity getUser() {
        return getInstance().user;
    }

    public static SharedPreferences getUserPreferences() {
        return getInstance().UserPreferences;
    }


}
