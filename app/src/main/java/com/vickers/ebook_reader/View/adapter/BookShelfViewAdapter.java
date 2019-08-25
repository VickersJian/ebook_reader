package com.vickers.ebook_reader.View.adapter;

import com.vickers.ebook_reader.View.adapter.base.BaseListAdapter;
import com.vickers.ebook_reader.View.adapter.base.ViewHolder;

import com.vickers.ebook_reader.View.adapter.holder.BookShelfHolder;
import com.vickers.ebook_reader.data.entity.LibraryBookEntity;

public class BookShelfViewAdapter extends BaseListAdapter<LibraryBookEntity> {

    @Override
    protected ViewHolder<LibraryBookEntity> createViewHolder(int viewType) {
        return new BookShelfHolder();
    }


}
