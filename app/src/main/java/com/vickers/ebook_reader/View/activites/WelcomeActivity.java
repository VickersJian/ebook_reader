/* Created by Vickers Jian on 2019/07 */
package com.vickers.ebook_reader.View.activites;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.vickers.ebook_reader.Base.mBaseActivity;
import com.vickers.ebook_reader.MyApplication;
import com.vickers.ebook_reader.R;
import com.vickers.ebook_reader.data.entity.UserEntity;

import static com.vickers.ebook_reader.data.dao.UserEntityDao.findUserByUserId;


/**
 * 欢迎界面
 */
public class WelcomeActivity extends mBaseActivity {

    private TextView welcomeText;
    private SharedPreferences UserPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        UserPreferences = MyApplication.getUserPreferences();
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onActivityCreate() {
        setContentView(R.layout.activity_welcome);
        super.onActivityCreate();
        //禁用滑动返回
        setSwipeBackEnable(false);

    }

    @Override
    protected void bindView() {
        welcomeText=findById(R.id.tv_welcome);
    }

    @Override
    protected void bindEvent() {
        welcomeText.setTextColor(getResources().getColor(R.color.md_pink_200));
        welcomeText.setTextSize(20);
        //过场动画设置
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (UserPreferences.getBoolean("auto_login", false)) {
                    String userId=UserPreferences.getString("user", "");
                    UserEntity user;
                    if (userId != null && (user=findUserByUserId(userId)) != null) {
                        MyApplication.setUser(user);
                        startActivityByAnim(new Intent(WelcomeActivity.this, BookshelfActivity.class),
                                android.R.anim.fade_in, android.R.anim.fade_out);
                    } else {
                        Toast.makeText(getApplicationContext(), "登录失败，失效的登录信息", Toast.LENGTH_SHORT).show();
                        SharedPreferences.Editor editor = UserPreferences.edit();
                        editor.putBoolean("auto_login", false);
                        editor.apply();
                        startActivityByAnim(new Intent(WelcomeActivity.this, UserLoginActivity.class),
                                android.R.anim.fade_in, android.R.anim.fade_out);
                    }
                } else {
                    startActivityByAnim(new Intent(WelcomeActivity.this, UserLoginActivity.class),
                            android.R.anim.fade_in, android.R.anim.fade_out);
                }
                finish();
            }
        }, 800);
    }
}


