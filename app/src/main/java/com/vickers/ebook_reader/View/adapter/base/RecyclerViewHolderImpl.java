package com.vickers.ebook_reader.View.adapter.base;

import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * 通过ViewHolder构建RecyclerView.ViewHolder
 */
public class RecyclerViewHolderImpl<T> extends RecyclerView.ViewHolder {

    protected ViewHolder<T> holder;

    public RecyclerViewHolderImpl(View itemView, ViewHolder<T> holder) {
        super(itemView);
        this.holder = holder;
        holder.initView();
    }

}
