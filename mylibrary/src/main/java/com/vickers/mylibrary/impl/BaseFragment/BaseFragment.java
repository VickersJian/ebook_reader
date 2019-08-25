/* Created by Vickers Jian on 2019/07 */
package com.vickers.mylibrary.impl.BaseFragment;

import android.content.Intent;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.Objects;

public abstract class BaseFragment extends Fragment {
    protected View view;
    protected Bundle savedInstanceState;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        this.savedInstanceState = savedInstanceState;
        view = createView(inflater, container);
        bindView();
        initData();
        bindEvent();
        return view;
    }

    /**
     * 事件触发绑定
     */
    protected void bindEvent() {

    }

    /**
     * 控件绑定
     */
    protected void bindView() {

    }

    /**
     * 数据初始化
     */
    protected void initData() {

    }

    /**
     * 加载布局
     */
    protected abstract View createView(LayoutInflater inflater, ViewGroup container);

    protected void startActivityByAnim(Intent intent, int animIn, int animExit) {
        startActivity(intent);
        Objects.requireNonNull(getActivity()).overridePendingTransition(animIn, animExit);
    }

}
