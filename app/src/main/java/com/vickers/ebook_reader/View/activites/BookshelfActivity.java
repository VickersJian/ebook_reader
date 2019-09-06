/* Created by Vickers Jian on 2019/07 */
package com.vickers.ebook_reader.View.activites;

import android.app.Activity;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Observer;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.support.v7.widget.GridLayoutManager;

import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.jaeger.library.StatusBarUtil;
import com.vickers.ebook_reader.Base.mBaseActivity;
import com.vickers.ebook_reader.MyApplication;
import com.vickers.ebook_reader.R;


import com.vickers.ebook_reader.View.adapter.BookViewAdapter;
import com.vickers.ebook_reader.View.adapter.base.BaseListAdapter;
import com.vickers.ebook_reader.Base.Result;

import com.vickers.ebook_reader.data.dao.LibraryBookEntityDao;
import com.vickers.ebook_reader.data.dao.UserEntityDao;
import com.vickers.ebook_reader.data.entity.LibraryBookEntity;

import com.vickers.ebook_reader.data.entity.UserEntity;
import com.vickers.ebook_reader.utils.FileUtils;


import java.io.File;
import java.io.FileInputStream;
import java.util.Arrays;

import java.io.InputStream;

import java.util.List;

import nl.siegmann.epublib.domain.Book;
import nl.siegmann.epublib.epub.EpubReader;

import static com.vickers.ebook_reader.data.dao.UserEntityDao.findUserByUserId;


/**
 * 书架界面
 */
public class BookshelfActivity extends mBaseActivity {

    private static final String TAG = "BookshelfActivity";
    private UserEntity user;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private View headerView;
    private TextView displayName;
    private TextView userId;
    private ProgressBar progressBar;
    private Toolbar toolbar;
    private CardView cardViewSearch;
    private RecyclerView recyclerView;
    private UserEntityDao userEntityDao;
    private BookViewAdapter bookShelfViewAdapter;
    private String bookType = null;
    private List<LibraryBookEntity> userBookList;
    private MutableLiveData<List<LibraryBookEntity>> displayBookList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (MyApplication.getUser() == null) {
            String userId = MyApplication.getUserPreferences().getString("user", "");
            user = findUserByUserId(userId);
            MyApplication.setUser(user);
        } else {
            user = MyApplication.getUser();
        }
        super.onCreate(savedInstanceState);
        immersiveStatusBar();
    }

    @Override
    protected void onRestart() {
        Log.i(TAG, "onRestart: ");
        super.onRestart();
    }

    @Override
    protected void onDestroy() {
        Log.i(TAG, "onDestroy: ");
        super.onDestroy();
    }

    @Override
    protected void onActivityCreate() {
        //禁用滑动返回
        setSwipeBackEnable(false);
        setContentView(R.layout.activity_bookshelf);
    }


    @Override
    protected void bindView() {
        userId = findById(R.id.userId);
        displayName = findById(R.id.dispalyname);
        drawerLayout = findById(R.id.drawer_layout);
        navigationView = findById(R.id.nav_view);
        headerView = navigationView.getHeaderView(0);
        userId = headerView.findViewById(R.id.userId);
        displayName = headerView.findViewById(R.id.dispalyname);
        toolbar = findById(R.id.toolbar);
        cardViewSearch = findById(R.id.card_search);
        setSupportActionBar(toolbar);
        recyclerView = findById(R.id.recyclerview_bookshelf);
        progressBar = findById(R.id.loading);
        progressBar.getIndeterminateDrawable().setColorFilter(getResources().getColor(R.color.md_pink_600), PorterDuff.Mode.SRC_ATOP);

    }

    @Override
    protected void initData() {
        userId.setTextColor(getResources().getColor(R.color.tv_text_default));
        userId.setText(user.getUserID());
        displayName.setTextColor(getResources().getColor(R.color.tv_text_default));
        displayName.setText(user.getDispalyName());
        userEntityDao = new UserEntityDao(user);
        displayBookList = new MutableLiveData<>();
        bookShelfViewAdapter = new BookViewAdapter();
        recyclerView.setAdapter(bookShelfViewAdapter);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 3);
        recyclerView.setLayoutManager(gridLayoutManager);
        userBookList = userEntityDao.getBook(null, userEntityDao.getOrder());
        refreshDisplayBookList();
        displayBookList.observe(this, new Observer<List<LibraryBookEntity>>() {
            @Override
            public void onChanged(@Nullable List<LibraryBookEntity> bookEntityList) {
                bookShelfViewAdapter.refreshItems(displayBookList.getValue());
                progressBar.setVisibility(View.GONE);
            }
        });
    }

    @Override
    protected void bindEvent() {
        initDrawerEvent();
        initToolBarEvent();
        initOnItemClick();
    }

    @Override
    protected void onResume() {
        refreshDisplayBookList();
        super.onResume();
    }

    /**
     * 初始化导航栏
     */
    private void initDrawerEvent() {
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                int id = menuItem.getItemId();
                if (id == R.id.nav_user_center) {
                    Intent intent = new Intent(BookshelfActivity.this, UserCenterActivity.class);
                    intent.putExtra("userid", user.getUserID());
                    startActivityForResultByAnim(intent, USER_CENTER_CODE,
                            R.anim.slide_in_right, R.anim.slide_out_right);
                }
                drawerLayout.closeDrawer(GravityCompat.START);
                return true;
            }
        });

    }

    /**
     * 初始化菜单
     */
    private void initToolBarEvent() {
        toolbar.inflateMenu(R.menu.menu_bookshelf);
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.menu_add_book: {
                        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                        intent.setType("*/*");
                        intent.addCategory(Intent.CATEGORY_OPENABLE);
                        try {
                            startActivityForResult(Intent.createChooser(intent, "选择文件"),
                                    FILE_SELECT_CODE);
                        } catch (android.content.ActivityNotFoundException ex) {
                            Toast.makeText(BookshelfActivity.this, "亲，木有文件管理器啊-_-!!",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                    break;
                    case R.id.menu_screen: {

                        List<String> bookTypeList = userEntityDao.getBookTypeList();
                        bookTypeList.add(0, "全部");
                        final String[] bookTypeArr = new String[bookTypeList.size()];
                        bookTypeList.toArray(bookTypeArr);
                        new AlertDialog.Builder(BookshelfActivity.this).setTitle("筛选")
                                .setItems(bookTypeArr, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        if (which == 0) {
                                            userBookList = userEntityDao.getBook(null, userEntityDao.getOrder());
                                            bookType = null;
                                        } else {
                                            userBookList = userEntityDao.getBook(bookTypeArr[which], userEntityDao.getOrder());
                                            bookType = bookTypeArr[which];
                                        }
                                        refreshDisplayBookList();
                                    }
                                })
                                .show();
                    }
                    break;
                    case R.id.menu_time_order_asc: {
                        orderSetting(userEntityDao.TIME_ASC);
                    }
                    break;
                    case R.id.menu_time_order_desc: {
                        orderSetting(userEntityDao.TIME_DESC);
                    }
                    break;
                    case R.id.menu_file_order_asc: {
                        orderSetting(userEntityDao.FILE_ASC);
                    }
                    break;
                    case R.id.menu_file_order_desc: {
                        orderSetting(userEntityDao.FILE_DESC);
                    }
                    break;
                    default:
                        return false;
                }
                return true;
            }
        });
        cardViewSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(BookshelfActivity.this, SearchBookActivity.class);
                intent.putExtra("userid", user.getUserID());
                startActivityForResultByAnim(intent, SEARCH_BOOK_CODE, R.anim.fade_in, R.anim.fade_out);
            }
        });
    }

    private void orderSetting(String order) {
        final String _order = order;
        progressBar.setVisibility(View.VISIBLE);
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                userEntityDao.setOrder(_order);
                userBookList = userEntityDao.getBook(bookType, userEntityDao.getOrder());
                displayBookList.postValue(userBookList);
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != Activity.RESULT_OK) {
            Log.e(TAG, "onActivityResult() error, resultCode: " + resultCode);
            super.onActivityResult(requestCode, resultCode, data);
            return;
        }
        switch (requestCode) {
            case FILE_SELECT_CODE: {
                Uri uri = data.getData();
                if (uri != null) {
                    final String filePath = FileUtils.getFilePathByUri(this, uri);
                    if (filePath != null && filePath.endsWith(".epub")) {
                        Log.i(TAG, "onActivityResult: " + filePath);
                        progressBar.setVisibility(View.VISIBLE);
                        new AddBookAsyncTask(BookshelfActivity.this)
                                .execute(filePath);
                    } else {
                        Toast.makeText(this, "仅支持.epub类型的电子书", Toast.LENGTH_SHORT).show();
                    }
                }
            }
            break;
            case CHANGE_BOOK_INFOR_CODE: {
                userBookList.set(data.getIntExtra("position", 0),
                        LibraryBookEntityDao.findBook(data.getStringExtra("bookurl")));
            }
            break;
            case SEARCH_BOOK_CODE: {
                userBookList = userEntityDao.getBook(null, userEntityDao.getOrder());
            }
            break;
            case USER_CENTER_CODE: {
                Toast.makeText(getApplicationContext(), "用户信息已更改，请重新登录", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(BookshelfActivity.this, UserLoginActivity.class));
                finish();
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_bookshelf, menu);
        return true;
    }

    /**
     * 沉浸式状态栏
     */
    private void immersiveStatusBar() {
        StatusBarUtil.setColorForDrawerLayout(this, drawerLayout, getResources()
                .getColor(R.color.md_light_statusbar), 0);

    }

    /**
     * 设置点击事件
     */
    private void initOnItemClick() {
        bookShelfViewAdapter.setOnItemClickListener(new BaseListAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int pos) {
                Intent intent = new Intent(BookshelfActivity.this, ReadBookActivity.class);
                intent.putExtra("bookurl", bookShelfViewAdapter.getItem(pos).getBookUrl());
                startActivityByAnim(intent, R.anim.slide_in_right, R.anim.slide_out_right);
            }
        });
        final String[] longclickitemlist = {"阅读情况", "修改", "删除"};
        bookShelfViewAdapter.setOnItemLongClickListener(
                new BaseListAdapter.OnItemLongClickListener() {
                    @Override
                    public boolean onItemLongClick(View view, int pos) {
                        final int position = pos;
                        new AlertDialog.Builder(BookshelfActivity.this).setTitle("更多")
                                .setItems(longclickitemlist,
                                        new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog,
                                                                int which) {
                                                switch (which) {
                                                    case 0: {
                                                        Intent intent=new Intent(BookshelfActivity.this,ReadingSituationActivity.class);
                                                        intent.putExtra("userid",user.getUserID());
                                                        intent.putExtra("bookurl",bookShelfViewAdapter.getItem(position).getBookUrl());
                                                        startActivityByAnim(intent, R.anim.slide_in_right,R.anim.slide_out_right);
                                                    }
                                                    break;
                                                    case 1: {
                                                        Intent intent = new Intent(BookshelfActivity.this,
                                                                ChangeBookInformActivity.class);
                                                        intent.putExtra("bookurl",
                                                                bookShelfViewAdapter.getItem(position).getBookUrl());
                                                        intent.putExtra("position", position);
                                                        intent.putExtra("userid", user.getUserID());
                                                        startActivityForResultByAnim(intent, CHANGE_BOOK_INFOR_CODE,
                                                                R.anim.slide_in_right, R.anim.slide_out_right);
                                                    }
                                                    break;
                                                    case 2: {
                                                        userEntityDao.deleteBook(bookShelfViewAdapter.getItem(position));
                                                        userBookList.remove(bookShelfViewAdapter.getItem(position));
                                                        refreshDisplayBookList();
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


    private void refreshDisplayBookList() {
        progressBar.setVisibility(View.VISIBLE);
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                displayBookList.postValue(userBookList);
            }
        });
    }

    private static class AddBookAsyncTask extends AsyncTask<String, Void, Result> {
        private UserEntityDao userEntityDao;
        private BookshelfActivity activity;


        private AddBookAsyncTask(BookshelfActivity activity) {
            this.activity = activity;
            this.userEntityDao = activity.userEntityDao;
        }

        @Override
        protected Result doInBackground(String... strings) {
            try {
                String filePath = strings[0];
                EpubReader epubReader = new EpubReader();
                InputStream in = new FileInputStream(new File(filePath));
                Book book = epubReader.readEpub(in);
                Result result = userEntityDao.addBook(book, filePath);
                activity.userBookList = userEntityDao.getBook(null, userEntityDao.getOrder());
                activity.refreshDisplayBookList();
                in.close();
                return result;
            } catch (Exception e) {
                Log.e(TAG, "onActivityResult: " + e.getMessage());
                e.printStackTrace();
                return new Result.Error(e);
            }
        }

        @Override
        protected void onPostExecute(Result result) {
            if (result instanceof Result.Success) {
                Toast.makeText(activity, "添加成功", Toast.LENGTH_SHORT).show();
            } else
                Toast.makeText(activity, "添加失败," + ((Result.Error) result).getError().getMessage(),
                        Toast.LENGTH_SHORT).show();
        }
    }
}
