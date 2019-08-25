package com.vickers.ebook_reader.View.adapter.holder;

import android.util.Log;

import com.vickers.ebook_reader.Helper.ImageLoadHelper;
import com.vickers.ebook_reader.R;
import com.vickers.ebook_reader.View.adapter.base.ViewHolderImpl;
import com.vickers.ebook_reader.data.entity.LibraryBookEntity;
import com.vickers.ebook_reader.widget.RatioImageView;

public class BookShelfHolder extends ViewHolderImpl<LibraryBookEntity> {
    private RatioImageView cover;
    private ImageLoadHelper imageLoadHelper;
    @Override
    protected int getItemLayoutId() {
        return R.layout.item_bookshelf_grid;
    }

    @Override
    public void initView() {
        cover=findById(R.id.image_book_cover);
        imageLoadHelper=new ImageLoadHelper<String,String,RatioImageView>(100,200,ImageLoadHelper.MOD_KEEP);
    }

    @Override
    public void onBindHolder(LibraryBookEntity data, int pos) {
        if(!data.getCoverUrl().equals("")){
            Log.i(TAG, "onBindHolder: "+data.getCoverUrl());
            imageLoadHelper.loadBitmap(data.getCoverUrl(),data.getCoverUrl(),cover);
        }
        else
            cover.setImageResource(R.drawable.img_cover_default);
    }
    private static final String TAG = "BookShelfHolder";
}
