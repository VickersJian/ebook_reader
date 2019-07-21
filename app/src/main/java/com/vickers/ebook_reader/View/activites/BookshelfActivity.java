package com.vickers.ebook_reader.View.activites;

import android.content.Intent;
import android.view.View;
import android.widget.Button;

import com.vickers.ebook_reader.Base.mBaseActivity;
import com.vickers.ebook_reader.R;



public class BookshelfActivity extends mBaseActivity {
    private Button btn_usercenter;

    @Override
    protected void onActivityCreate() {
        //禁用滑动返回
        setSwipeBackEnable(false);
        setContentView(R.layout.activity_bookshelf);
        initDrawer();
    }

    //初始化侧边栏
    private void initDrawer(){
        btn_usercenter=(Button)findViewById(R.id.btn_usercenter);
        btn_usercenter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityByAnim(new Intent(BookshelfActivity.this,UserCenterActivity.class),R.anim.slide_in_right,0);
            }
        });
    }
}
