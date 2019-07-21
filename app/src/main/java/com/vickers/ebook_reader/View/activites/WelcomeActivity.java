package com.vickers.ebook_reader.View.activites;


import android.animation.Animator;
import android.animation.ValueAnimator;

import android.content.Intent;


import android.widget.ImageView;

import com.vickers.ebook_reader.Base.mBaseActivity;
import com.vickers.ebook_reader.R;



public class WelcomeActivity extends mBaseActivity {

    private ImageView image_welcome;

    @Override
    protected void onActivityCreate(){
        setContentView(R.layout.activity_welcome);
        //禁用滑动返回
        setSwipeBackEnable(false);
        //过渡动画
        ValueAnimator animator = ValueAnimator.ofFloat(1f, 0f).setDuration(800);
        image_welcome=(ImageView)findViewById(R.id.welcome_page);
        animator.setStartDelay(500);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float value=(float)animation.getAnimatedValue();
                image_welcome.setAlpha(value);
            }
        });
        animator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                startActivityByAnim(new Intent(WelcomeActivity.this,BookshelfActivity.class),R.anim.fade_in,R.anim.fade_out);
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


