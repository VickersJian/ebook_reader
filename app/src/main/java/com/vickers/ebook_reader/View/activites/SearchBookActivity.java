package com.vickers.ebook_reader.View.activites;

import android.app.Activity;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Observer;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.support.v7.widget.SearchView;
import android.view.View;
import android.widget.ProgressBar;

import com.vickers.ebook_reader.Base.mBaseActivity;
import com.vickers.ebook_reader.R;
import com.vickers.ebook_reader.View.adapter.BookViewAdapter;
import com.vickers.ebook_reader.View.adapter.base.BaseListAdapter;
import com.vickers.ebook_reader.data.dao.LibraryBookEntityDao;
import com.vickers.ebook_reader.data.entity.LibraryBookEntity;
import com.vickers.ebook_reader.data.entity.UserEntity;
import com.vickers.ebook_reader.data.dao.UserEntityDao;
import com.vickers.ebook_reader.utils.TextUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SearchBookActivity extends mBaseActivity {

    private static final String TAG = "SearchBookActivity";

    private UserEntity user;
    private UserEntityDao userEntityDao;
    private Toolbar toolbar;
    private SearchView searchView;
    private List<LibraryBookEntity> userBookList;
    private int userBookListPosition;
    private List<LibraryBookEntity> searchResultList;
    private ProgressBar progressBar;
    private MutableLiveData<List<LibraryBookEntity>> displayBookList = new MutableLiveData<>();
    private RecyclerView recyclerView;
    private BookViewAdapter searchResultAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setResult(Activity.RESULT_OK);
    }

    @Override
    protected void onActivityCreate() {
        super.onActivityCreate();
        setSwipeBackEnable(false);
        setContentView(R.layout.activity_search_book);
    }

    @Override
    protected void bindView() {
        toolbar = findById(R.id.toolbar);
        searchView = findById(R.id.searchView);
        recyclerView = findById(R.id.recyclerview_serchresult);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        progressBar = findById(R.id.loading);
    }

    @Override
    protected void initData() {
        Intent intent = getIntent();
        user = UserEntityDao.findUserByUserId(intent.getStringExtra("userid"));
        userEntityDao = new UserEntityDao(user);
        userBookList = userEntityDao.getLibrary(null);
        searchResultAdapter = new BookViewAdapter();
        recyclerView.setAdapter(searchResultAdapter);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 3);
        recyclerView.setLayoutManager(gridLayoutManager);
        searchResultList = new ArrayList<>();
        displayBookList.observe(this, new Observer<List<LibraryBookEntity>>() {
            @Override
            public void onChanged(@Nullable List<LibraryBookEntity> bookEntityList) {
                searchResultAdapter.refreshItems(displayBookList.getValue());
                progressBar.setVisibility(View.GONE);
            }
        });
    }

    @Override
    protected void bindEvent() {
        initToolbar();
        initSearchView();
        initOnItemClick();
    }

    private void initSearchView() {
        searchView.onActionViewExpanded();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return true;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                if (!searchResultList.isEmpty()) {
                    searchResultList.clear();
                }
                if (!s.isEmpty()) {
                    for (LibraryBookEntity book : userBookList) {
                        if (s.matches(book.getTitle())||s.matches(book.getAuthor())) {
                            searchResultList.add(book);
                        } else {
                            Pattern pattern = Pattern.compile("[" + s + "]");
                            Matcher matcher1 = pattern.matcher(book.getTitle());
                            Matcher matcher2 = pattern.matcher(book.getAuthor());
                            if (matcher1.find())
                                searchResultList.add(book);
                            else if (matcher2.find()) {
                                searchResultList.add(book);
                            }
                        }
                    }
                }
                refreshDisplayBookList();
                return true;
            }
        });
    }

    private void initToolbar() {

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void refreshDisplayBookList() {
        progressBar.setVisibility(View.VISIBLE);
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                displayBookList.postValue(searchResultList);
            }
        });
    }

    /**
     * 设置长点击事件
     */
    private void initOnItemClick() {
        searchResultAdapter.setOnItemClickListener(new BaseListAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int pos) {
                Intent intent=new Intent(SearchBookActivity.this,ReadBookActivity.class);
                intent.putExtra("bookurl",searchResultAdapter.getItem(pos).getBookUrl());
                startActivityByAnim(intent,R.anim.slide_in_right,R.anim.slide_out_right);
            }
        });
        final String[] longclickitemlist = {"修改", "删除"};
        searchResultAdapter.setOnItemLongClickListener(
                new BaseListAdapter.OnItemLongClickListener() {
                    @Override
                    public boolean onItemLongClick(View view, int pos) {
                        final int position = pos;
                        new AlertDialog.Builder(SearchBookActivity.this)
                                .setItems(longclickitemlist,
                                        new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog,
                                                                int which) {
                                                switch (which) {
                                                    case 0:
                                                        Intent intent = new Intent(SearchBookActivity.this,
                                                                ChangeBookInformActivity.class);
                                                        userBookListPosition = userBookList.indexOf(searchResultAdapter.getItem(position));
                                                        intent.putExtra("bookurl",
                                                                searchResultAdapter.getItem(position).getBookUrl());
                                                        intent.putExtra("position", position);
                                                        startActivityForResultByAnim(intent, CHANGE_BOOK_INFOR_CODE,
                                                                R.anim.slide_in_right, R.anim.slide_out_right);
                                                        break;
                                                    case 1:
                                                        userEntityDao.deleteBook(searchResultAdapter.getItem(position));
                                                        searchResultList.remove(searchResultAdapter.getItem(position));
                                                        userBookList.remove(searchResultAdapter.getItem(position));
                                                        refreshDisplayBookList();
                                                }
                                            }
                                        })
                                .setNegativeButton("取消", null).show();
                        return true;
                    }
                });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (resultCode != Activity.RESULT_OK) {
            Log.e(TAG, "onActivityResult() error, resultCode: " + resultCode);
            super.onActivityResult(requestCode, resultCode, data);
            return;
        }
        if (requestCode == CHANGE_BOOK_INFOR_CODE) {
            Log.i(TAG, "onActivityResult: " + data.getIntExtra("position", 0));
            searchResultList.set(data.getIntExtra("position", 0),
                    LibraryBookEntityDao.findBook(data.getStringExtra("bookurl")));
            userBookList.set(userBookListPosition, LibraryBookEntityDao.findBook(data.getStringExtra("bookurl")));
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onResume() {
        refreshDisplayBookList();
        super.onResume();
    }
}
