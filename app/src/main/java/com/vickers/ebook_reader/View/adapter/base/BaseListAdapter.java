package com.vickers.ebook_reader.View.adapter.base;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public abstract class BaseListAdapter<T> extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final String TAG = "BaseListAdapter";

    protected final List<T> mList = new ArrayList<>();
    protected OnItemClickListener mClickListener;
    protected OnItemLongClickListener mLongClickListener;

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        ViewHolder<T> viewHolder = createViewHolder(viewType);
        View itemView = viewHolder.createItemView(parent);
        //创建RecyclerView.ViewHolder
        return new RecyclerViewHolderImpl<>(itemView, viewHolder);
    }

    /**
     * 取得ViewHolder
     *
     * @return ViewHolder
     */
    protected abstract ViewHolder<T> createViewHolder(int viewType);

    @Override
    public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder holder, int position) {
        //确保Holder是RecyclerViewHolderImpl创建的
        if (holder instanceof RecyclerViewHolderImpl) {
            final ViewHolder iHolder = ((RecyclerViewHolderImpl) holder).holder;
            iHolder.onBindHolder(getItem(position), position);
            //设置点击事件
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mClickListener != null) {
                        mClickListener.onItemClick(v, holder.getLayoutPosition());
                    }
                    //adapter监听点击事件
                    iHolder.onClick();
                    onItemClick(v, holder.getLayoutPosition());
                }
            });
            //设置长点击事件
            holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    boolean isClicked = false;
                    if (mLongClickListener != null) {
                        isClicked = mLongClickListener.onItemLongClick(v, holder.getLayoutPosition());
                    }
                    //Adapter监听长点击事件
                    onItemLongClick(v, holder.getLayoutPosition());
                    return isClicked;
                }
            });
        }
    }

    /**
     * 获得item个数
     *
     * @return int
     */
    @Override
    public int getItemCount() {
        return mList.size();
    }

    /**
     * 设置item的点击事件
     */
    protected void onItemClick(View v, int pos) {
    }

    /**
     * 设置item的长点击事件
     */
    protected void onItemLongClick(View v, int pos) {
    }

    /**
     * 设置ItemClickListener
     */
    public void setOnItemClickListener(OnItemClickListener mListener) {
        this.mClickListener = mListener;
    }

    /**
     * 设置ItemLongClickListener
     */
    public void setOnItemLongClickListener(OnItemLongClickListener mListener) {
        this.mLongClickListener = mListener;
    }

    public void addItem(T value) {
        mList.add(value);
        notifyDataSetChanged();
    }

    public void addItem(int index, T value) {
        mList.add(index, value);
        notifyDataSetChanged();
    }

    public void addItems(List<T> values) {
        mList.addAll(values);
        notifyDataSetChanged();
    }

    public void removeItem(T value) {
        mList.remove(value);
        notifyDataSetChanged();
    }

    public void removeItems(List<T> value) {
        mList.removeAll(value);
        notifyDataSetChanged();
    }

    public T getItem(int position) {
        return mList.get(position);
    }

    public List<T> getItems() {
        return Collections.unmodifiableList(mList);
    }

    public int getItemSize() {
        return mList.size();
    }

    public void refreshItems(List<T> list) {
        mList.clear();
        mList.addAll(list);
        notifyDataSetChanged();
    }

    public void clear() {
        mList.clear();
    }

    /**
     * ItemClickListener内部类
     */
    public interface OnItemClickListener {
        void onItemClick(View view, int pos);
    }

    /**
     * ItemLongClickListener
     */
    public interface OnItemLongClickListener {
        boolean onItemLongClick(View view, int pos);
    }
}
