/* Created by Vickers Jian on 2019/07 */
package com.vickers.ebook_reader;

import android.app.Application;
import android.content.SharedPreferences;

import com.vickers.ebook_reader.data.entity.UserEntity;

public class MyApplication extends Application {

    private static UserEntity user = null;
    private SharedPreferences UserPreferences;
    private static MyApplication instance;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        UserPreferences = getSharedPreferences("User", MODE_PRIVATE);
    }

    public static MyApplication getInstance() {
        return instance;
    }

    public static void setUser(UserEntity user) {
        MyApplication.user = user;
    }

    public static UserEntity getUser() {
        return user;
    }

    public static SharedPreferences getUserPreferences() {
        return getInstance().UserPreferences;
    }


}
