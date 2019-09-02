package com.vickers.ebook_reader.View.adapter.holder;

import android.graphics.Bitmap;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.vickers.ebook_reader.Helper.ImageLoadHelper;
import com.vickers.ebook_reader.R;
import com.vickers.ebook_reader.View.adapter.base.ViewHolderImpl;
import com.vickers.ebook_reader.data.Result;
import com.vickers.ebook_reader.data.entity.LibraryBookEntity;
import com.vickers.ebook_reader.utils.FileUtils;
import com.vickers.ebook_reader.widget.RatioImageView;

public class BookShelfHolder extends ViewHolderImpl<LibraryBookEntity> {
    private TextView title;
    private RatioImageView cover;
    private ImageLoadHelper imageLoadHelper;

    @Override
    protected int getItemLayoutId() {
        return R.layout.item_bookshelf_grid;
    }

    @Override
    public void initView() {
        cover = findById(R.id.image_book_cover);
        title = findById(R.id.textview_title);
        imageLoadHelper = new ImageLoadHelper<String, String, RatioImageView>(100, 200, ImageLoadHelper.MOD_KEEP);
    }

    @Override
    public void onBindHolder(LibraryBookEntity data, int pos) {
        final LibraryBookEntity book = data;
        if (!data.getCoverUrl().equals("") && FileUtils.isFileExist(data.getCoverUrl())) {
            Log.i(TAG, "onBindHolder: " + data.getCoverUrl());
            imageLoadHelper.setImageLoadAsyncTask(new ImageLoadHelper.LoadTask() {
                @Override
                public void onPreExecute() {
                    title.setText(book.getTitle());
                }

                @Override
                public <D> Result doInBackground(D... data) {
                    return null;
                }

                @Override
                public void onPostExecute(Result resultImage) {
                    title.setVisibility(View.GONE);
                }
            });
            imageLoadHelper.loadBitmap(book.getCoverUrl(), book.getCoverUrl(), cover);
        } else title.setText(book.getTitle());
    }

    private static final String TAG = "BookShelfHolder";
}
