/* Created by Vickers Jian on 2019/08 */
package com.vickers.ebook_reader.View.adapter.base;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public abstract class ViewHolderImpl<T> implements ViewHolder<T> {
    private View view;
    private Context context;

    /**
     * @return item的布局
     */
    protected abstract int getItemLayoutId();

    /**
     * 实例化Item布局
     *
     * @param parent 根布局
     * @return Item的布局实例
     */
    @Override
    public View createItemView(ViewGroup parent) {
        //给定的上下文中获取布局加载器，并传入item布局，返回布局的实例(attachToRoot==false时)
        view = LayoutInflater.from(parent.getContext())
                .inflate(getItemLayoutId(), null, false);
        context = parent.getContext();
        return view;
    }

    @Override
    public abstract void onBindHolder(T data, int pos);

    /**
     * findViewById的简化
     */
    protected <V extends View> V findById(int id) {
        return (V) view.findViewById(id);
    }

    protected Context getContext() {
        return context;
    }

    /**
     * 获得Item的布局实例
     *
     * @return ItemView
     */
    protected View getItemView() {
        return view;
    }

    @Override
    public void onClick() {
    }
}
