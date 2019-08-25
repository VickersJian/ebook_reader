/* Created by Vickers Jian on 2019/08 */
package com.vickers.ebook_reader.Base;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.vickers.mylibrary.impl.BaseFragment.BaseFragment;

public abstract class mBaseFragment extends BaseFragment {

    private View view;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected View createView(LayoutInflater inflater, ViewGroup container) {
        return inflater.inflate(createLayoutId(), container, false);
    }

    /**
     * @return LayoutId 需要加载的布局文件
     */
    public abstract int createLayoutId();

}
