package com.vickers.mylibrary.impl.BaseActivity;



import android.content.Intent;

import android.os.Bundle;

import android.util.Log;




import me.imid.swipebacklayout.lib.SwipeBackLayout;
import me.imid.swipebacklayout.lib.app.SwipeBackActivity;


public abstract class BaseActivity extends SwipeBackActivity {
    private SwipeBackLayout mSwipeBackLayout;
    public final static String START_SHEAR_ELE = "start_with_share_ele";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("BaseActivity", getClass().getSimpleName());

        AppActivityManager.getInstance().add(this);

        onActivityCreate();
        initData();
    }

    /**
     * 布局载入  setContentView()
     */
    protected void onActivityCreate() {

        // 可以调用该方法，设置是否允许滑动退出
        setSwipeBackEnable(true);
        mSwipeBackLayout = getSwipeBackLayout();

        // 设置滑动方向，可设置EDGE_LEFT, EDGE_RIGHT, EDGE_ALL, EDGE_BOTTOM
        mSwipeBackLayout.setEdgeTrackingEnabled(SwipeBackLayout.EDGE_LEFT);

        // 滑动退出的效果只能从边界滑动才有效果，如果要扩大touch的范围，可以调用这个方法
        //mSwipeBackLayout.setEdgeSize(200);

        //设置入场动画（如在style中禁用了过场动画）
        //overridePendingTransition(android.R.anim.slide_in_left, 0);
    }

    /**
     * 数据初始化
     */
    protected void initData(){

    };


    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        AppActivityManager.getInstance().remove(this);

    }



    protected void startActivityByAnim(Intent intent, int animIn, int animExit) {
        startActivity(intent);
        overridePendingTransition(animIn, animExit);
    }




}