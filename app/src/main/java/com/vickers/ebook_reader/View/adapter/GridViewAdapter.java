/* Created by Vickers Jian on 2019/08 */
package com.vickers.ebook_reader.View.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


import com.vickers.ebook_reader.R;
import com.vickers.ebook_reader.View.adapter.base.BaseListAdapter;
import com.vickers.ebook_reader.View.adapter.base.RecyclerViewHolderImpl;
import com.vickers.ebook_reader.View.adapter.base.ViewHolder;
import com.vickers.ebook_reader.View.adapter.base.ViewHolderImpl;
import com.vickers.ebook_reader.data.entity.LibraryBookEntity;
import com.vickers.ebook_reader.widget.RatioImageView;

import java.util.List;

public class GridViewAdapter extends RecyclerView.Adapter<GridViewAdapter.InnerHolder> {

    private final List<LibraryBookEntity> libraryBook;

    public GridViewAdapter(List<LibraryBookEntity> UserLibrary) {
        this.libraryBook = UserLibrary;
    }

    /**
     * 这个方法用于创建item的View
     */
    @NonNull
    @Override
    public InnerHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //传入item界面
        //    1.实例化item布局，得到view
        //    2.创建InnerHolder
        View view = View.inflate(parent.getContext(), R.layout.item_bookshelf_grid, null);
        return new InnerHolder(view);
    }


    /**
     * 用于绑定Holder，一般用于数据设置
     *
     * @param innerHolder
     * @param i
     */
    @Override
    public void onBindViewHolder(@NonNull InnerHolder innerHolder, int i) {
        //设置数据
        innerHolder.setData(libraryBook.get(i));
    }

    /**
     * @return int item个数
     */
    @Override
    public int getItemCount() {
        if (libraryBook != null) {
            return libraryBook.size();
        }
        return 0;
    }


    public class InnerHolder extends RecyclerView.ViewHolder {

        private RatioImageView BookCover;


        public InnerHolder(@NonNull View itemView) {
            super(itemView);
            //找到item的控件
            BookCover = itemView.findViewById(R.id.image_book_cover);
        }

        /**
         * 用于设置数据
         */
        public void setData(LibraryBookEntity book) {
            BookCover.setImageResource(R.drawable.img_cover_default);
        }

        private void LoadBookCover(String coverUrl) {

        }
    }
}
