/* Created by Vickers Jian on 2019/08 */
package com.vickers.ebook_reader.View.adapter.base;

import android.view.View;
import android.view.ViewGroup;
/**
 * MyViewHolder
 */
public interface ViewHolder<T> {
    /**
     * @param parent 根布局
     * @return Item的布局实例
     */
    View createItemView(ViewGroup parent);

    /**
     * 绑定控件
     */
    void initView();

    /**
     * 数据设置,需要在构建Adapter时调用
     *
     * @param data 数据
     * @param pos  位置
     */
    void onBindHolder(T data, int pos);

    /**
     * 点击事件设置
     */
    void onClick();
}
