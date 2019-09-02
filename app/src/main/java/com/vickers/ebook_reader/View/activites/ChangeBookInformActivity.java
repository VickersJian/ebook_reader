package com.vickers.ebook_reader.View.activites;

import android.app.Activity;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Observer;
import android.content.ActivityNotFoundException;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.DocumentsContract;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.vickers.ebook_reader.Base.mBaseActivity;
import com.vickers.ebook_reader.Helper.ImageLoadHelper;
import com.vickers.ebook_reader.R;
import com.vickers.ebook_reader.data.Result;
import com.vickers.ebook_reader.data.dao.LibraryBookEntityDao;
import com.vickers.ebook_reader.data.entity.LibraryBookEntity;
import com.vickers.ebook_reader.utils.FileUtils;
import com.vickers.ebook_reader.widget.RatioImageView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Objects;

public class ChangeBookInformActivity extends mBaseActivity {

    private static final String TAG = "ChangeBookInform";

    private RatioImageView coverView;
    private TextView insideTitle;
    private ProgressBar progressBar;
    private EditText title;
    private EditText author;
    private String coverUrl;
    private MutableLiveData<String> LiveDatacoverUrl = new MutableLiveData<>();
    private Button save;
    private Button cancel;

    private LibraryBookEntity book;
    private int position;
    private ImageLoadHelper imageLoadHelper;

    @Override
    protected void onActivityCreate() {
        super.onActivityCreate();
        setContentView(R.layout.activity_change_book_inform);
    }


    @Override
    protected void bindView() {
        coverView = findById(R.id.image_book_cover);
        insideTitle = findById(R.id.textview_title);
        progressBar = findById(R.id.loading);
        title = findById(R.id.edt_title);
        author = findById(R.id.edt_author);
        save = findById(R.id.btn_save_change);
        cancel = findById(R.id.btn_cancel);
    }

    @Override
    protected void initData() {
        Intent intent = getIntent();
        position = intent.getIntExtra("position", -1);
        book = LibraryBookEntityDao.findBook(intent.getStringExtra("bookurl"));
        coverUrl = book.getCoverUrl();
        LiveDatacoverUrl.setValue(coverUrl);
        LiveDatacoverUrl.observe(this, new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                if (!Objects.equals(s, "") && FileUtils.isFileExist(s)) {
                    imageLoadHelper.loadBitmap(s, s, coverView);
                } else {
                    insideTitle.setVisibility(View.VISIBLE);
                }
            }
        });
        setResult(Activity.RESULT_CANCELED);
    }

    @Override
    protected void bindEvent() {
        initCoverEvent();
        initEditTextEvent();
        initButtonEvent();
    }

    private void initCoverEvent() {
        insideTitle.setText(book.getTitle());
        imageLoadHelper = new ImageLoadHelper<String, String, RatioImageView>(200, 400, ImageLoadHelper.MOD_KEEP);
        imageLoadHelper.setImageLoadAsyncTask(new ImageLoadHelper.LoadTask() {
            @Override
            public void onPreExecute() {
                insideTitle.setVisibility(View.GONE);
                progressBar.setVisibility(View.VISIBLE);
            }

            @Override
            public <D> Result doInBackground(D... data) {
                return null;
            }

            @Override
            public void onPostExecute(Result bitmap) {
                progressBar.setVisibility(View.GONE);
            }
        });
        coverView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                final String[] longclickitemlist = {"重置封面", "修改封面"};
                new AlertDialog.Builder(ChangeBookInformActivity.this)
                        .setTitle("封面设置")
                        .setItems(longclickitemlist,
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog,
                                                        int which) {
                                        switch (which) {
                                            case 0: {
                                                new ResetCoverTask(ChangeBookInformActivity.this)
                                                        .execute(book);
                                            }
                                            break;
                                            case 1: {
                                                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                                                intent.setType("image/*");
                                                intent.addCategory(Intent.CATEGORY_OPENABLE);
                                                try {
                                                    startActivityForResult(Intent.createChooser(intent, "选择文件"),
                                                            FILE_SELECT_CODE);
                                                } catch (ActivityNotFoundException ex) {
                                                    Toast.makeText(ChangeBookInformActivity.this, "亲，木有文件管理器啊-_-!!",
                                                            Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                            break;
                                        }
                                    }
                                })
                        .setNegativeButton("取消", null).show();
                return true;
            }
        });
    }

    private void initEditTextEvent() {
        title.setText(book.getTitle());
        author.setText(book.getAuthor());
    }

    private void initButtonEvent() {
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (title.getText().toString().equals(book.getTitle())
                        && author.getText().toString().equals(book.getAuthor())
                        && coverUrl.equals(book.getCoverUrl())) {
                    finish();
                } else {
                    book.setTitle(title.getText().toString());
                    book.setAuthor(author.getText().toString());
                    if (coverUrl.equals("reset")) {
                        String orignalCoverCachePath = FileUtils.getCachePath() + File.separator + "resetCashe";
                        try {
                            OutputStream out = new FileOutputStream(new File(orignalCoverCachePath));
                            imageLoadHelper.getBitmapFromMemCache("reset").compress(Bitmap.CompressFormat.JPEG, 90, out);
                            out.flush();
                            out.close();
                            book.setCoverUrl(orignalCoverCachePath);
                        } catch (Exception e) {

                        }
                    } else book.setCoverUrl(coverUrl);
                    new ChangeBookTask(ChangeBookInformActivity.this).execute(book);
                }
            }
        });
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @Override
    protected void onActivityResult(final int requestCode, int resultCode, @Nullable Intent data) {
        if (resultCode != Activity.RESULT_OK) {
            Log.e(TAG, "onActivityResult() error, resultCode: " + resultCode);
            super.onActivityResult(requestCode, resultCode, data);
            return;
        }
        if (requestCode == FILE_SELECT_CODE) {
            Uri uri = Objects.requireNonNull(data).getData();
            if (uri != null) {
                final String imagePath = FileUtils.getFilePathByUri(this, uri);
                if (imagePath != null && !imagePath.equals("")) {
                    coverUrl = imagePath;
                    LiveDatacoverUrl.setValue(coverUrl);
                } else {
                    Toast.makeText(this, "无法获取该图片", Toast.LENGTH_SHORT).show();
                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }


    static private class ChangeBookTask extends AsyncTask<LibraryBookEntity, Void, Result> {
        private Context context;

        ChangeBookTask(Context context) {
            this.context = context;
        }

        @Override
        protected Result doInBackground(LibraryBookEntity... bookEntities) {
            LibraryBookEntity book = bookEntities[0];
            return LibraryBookEntityDao.updataBook(book);
        }

        @Override
        protected void onPostExecute(Result result) {
            if (result instanceof Result.Success) {
                LibraryBookEntity book = (LibraryBookEntity) ((Result.Success) result).getData();
                ChangeBookInformActivity activity = (ChangeBookInformActivity) context;
                Toast.makeText(context, "已保存", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent();
                intent.putExtra("bookurl", book.getBookUrl());
                intent.putExtra("position", activity.position);
                activity.setResult(Activity.RESULT_OK, intent);
                activity.finish();
            } else {
                Toast.makeText(context,
                        "保存失败," + ((Result.Error) result).getError().getMessage(), Toast.LENGTH_SHORT).show();
            }
            String resetCachePath=FileUtils.getCachePath() + File.separator + "resetCashe";
            if(FileUtils.isFileExist(resetCachePath)){
                File resetCache=new File(resetCachePath);
                FileUtils.deleteFile(resetCache);
            }
        }
    }

    static private class ResetCoverTask extends AsyncTask<LibraryBookEntity, Void, Result> {
        private ChangeBookInformActivity activity;
        private ProgressBar progressBar;
        private ImageLoadHelper imageLoadHelper;
        private RatioImageView coverView;

        public ResetCoverTask(ChangeBookInformActivity context) {
            this.activity = context;
            this.progressBar = activity.progressBar;
            this.imageLoadHelper = activity.imageLoadHelper;
            this.coverView = activity.coverView;
        }

        @Override
        protected void onPreExecute() {
            progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected Result doInBackground(LibraryBookEntity... bookEntities) {
            try {
                InputStream inputStream = LibraryBookEntityDao.getBookCoverInputStream(
                        LibraryBookEntityDao.getBookFromBookUrl(
                                bookEntities[0].getBookUrl()));

                return new Result.Success<>(inputStream);
            } catch (Exception e) {
                e.printStackTrace();
                return new Result.Error(e);
            }
        }

        @Override
        protected void onPostExecute(Result result) {

            if (result instanceof Result.Success) {
                InputStream inputStream = (InputStream) ((Result.Success) result).getData();
                imageLoadHelper.loadBitmap("reset", inputStream, coverView);
                activity.coverUrl="reset";
                Log.i(TAG, "onPostExecute: ");
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                Toast.makeText(activity, "重置失败，" + ((Result.Error) result).getError().getMessage(),
                        Toast.LENGTH_SHORT).show();
            }
            progressBar.setVisibility(View.GONE);
        }
    }
}
