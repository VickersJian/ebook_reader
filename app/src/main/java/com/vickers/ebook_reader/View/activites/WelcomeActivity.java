/* Created by Vickers Jian on 2019/07 */
package com.vickers.ebook_reader.View.activites;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.Toast;

import com.vickers.ebook_reader.Base.mBaseActivity;
import com.vickers.ebook_reader.MyApplication;
import com.vickers.ebook_reader.R;

import static com.vickers.ebook_reader.data.dao.UserEntityDao.findUserByUserId;


/**
 * 欢迎界面
 */
public class WelcomeActivity extends mBaseActivity {

    private ImageView image_welcome;

    private SharedPreferences UserPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        UserPreferences = MyApplication.getUserPreferences();
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onActivityCreate() {
        setContentView(R.layout.activity_welcome);
        image_welcome = (ImageView) findViewById(R.id.welcome_page);
        super.onActivityCreate();

        //禁用滑动返回
        setSwipeBackEnable(false);
        //设置图片颜色
        image_welcome.setColorFilter(getResources().getColor(R.color.md_black_1000), PorterDuff.Mode.SRC_ATOP);
        //过场动画设置
        ValueAnimator animator = ValueAnimator.ofFloat(1f, 0f).setDuration(800);
        animator.setStartDelay(500);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float value = (float) animation.getAnimatedValue();
                image_welcome.setAlpha(value);
            }
        });
        animator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                if (UserPreferences.getBoolean("auto_login", false)) {
                    if (UserPreferences.getString("user", "") != null) {
                        MyApplication.setUser(findUserByUserId(UserPreferences.getString("user", "")));
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

            @Override
            public void onAnimationEnd(Animator animation) {

            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        animator.start();
    }
}


