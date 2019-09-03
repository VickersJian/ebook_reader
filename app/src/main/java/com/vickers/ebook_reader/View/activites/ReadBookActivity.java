package com.vickers.ebook_reader.View.activites;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.AsyncTask;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.vickers.ebook_reader.Base.mBaseActivity;
import com.vickers.ebook_reader.R;


import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import nl.siegmann.epublib.domain.Book;
import nl.siegmann.epublib.domain.Metadata;
import nl.siegmann.epublib.epub.EpubReader;

public class ReadBookActivity extends mBaseActivity {

    private TextView bookText;
    private ProgressBar progressBar;
    private Book book;

    @Override
    protected void onActivityCreate() {
        super.onActivityCreate();
        setContentView(R.layout.activity_read_book);
    }

    @Override
    protected void bindView() {
        progressBar = findById(R.id.loading);
        bookText = findById(R.id.book_text);
    }

    @Override
    protected void initData() {
        new LoadBookTask(this).execute();
    }

    @Override
    protected void bindEvent() {
        initBookText();
    }

    private void initBookText() {



    }

    private static class LoadBookTask extends AsyncTask<Void, Void, String> {
        @SuppressLint("StaticFieldLeak")
        private ReadBookActivity activity;

        public LoadBookTask(ReadBookActivity activity) {
            this.activity = activity;
        }

        @Override
        protected void onPreExecute() {
            activity.progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected String doInBackground(Void... voids) {
            Intent intent = activity.getIntent();
            try {
                EpubReader reader = new EpubReader();
                InputStream in = new FileInputStream(new File(intent.getStringExtra("bookurl")));
                activity.book = reader.readEpub(in);
                in.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            if(activity.book!=null){
            Metadata metadata = activity.book.getMetadata();
            return "作者：" + (metadata.getAuthors().isEmpty() ? "无信息" : metadata.getAuthors().get(0)) +
                    "\n出版社：" + (metadata.getPublishers().isEmpty() ? "无信息" : metadata.getPublishers().get(0)) +
                    "\n出版时间：" + (metadata.getDates().isEmpty() ? "无信息" : metadata.getDates().get(0).getValue()) +
                    "\n书名：" + (metadata.getTitles().isEmpty() ? "无信息" : metadata.getTitles().get(0)) +
                    "\n简介：" + (metadata.getDescriptions().isEmpty() ? "无信息" : metadata.getDescriptions().get(0)) +
                    "\n语言：" + (metadata.getLanguage().isEmpty() ? "无信息" : metadata.getLanguage());
            }
            return null;
        }

        @Override
        protected void onPostExecute(String string) {
            activity.bookText.setTextSize(15);
            activity.bookText.setText(string);
            activity.progressBar.setVisibility(View.GONE);
        }
    }
}
