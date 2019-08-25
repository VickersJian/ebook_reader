/* Created by Vickers Jian on 2019/07 */
package com.vickers.ebook_reader.View.activites;

import android.app.Activity;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Observer;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
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
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.jaeger.library.StatusBarUtil;
import com.vickers.ebook_reader.Base.mBaseActivity;
import com.vickers.ebook_reader.MyApplication;
import com.vickers.ebook_reader.R;

import com.vickers.ebook_reader.View.adapter.BookShelfViewAdapter;
import com.vickers.ebook_reader.View.adapter.GridViewAdapter;
import com.vickers.ebook_reader.View.adapter.base.BaseListAdapter;
import com.vickers.ebook_reader.data.Result;
import com.vickers.ebook_reader.data.dao.LibraryBookEntityDao;
import com.vickers.ebook_reader.data.dao.UserEntityDao;
import com.vickers.ebook_reader.data.entity.LibraryBookEntity;
import com.vickers.ebook_reader.data.entity.UserEntity;
import com.vickers.ebook_reader.utils.FileUtils;
import com.vickers.ebook_reader.utils.WidgetUtils;

import org.litepal.LitePal;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import nl.siegmann.epublib.domain.Book;
import nl.siegmann.epublib.domain.MediaType;
import nl.siegmann.epublib.epub.EpubReader;
import nl.siegmann.epublib.service.MediatypeService;


/**
 * 书架界面
 */
public class BookshelfActivity extends mBaseActivity {

    private static final String TAG = "BookshelfActivity";

    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private InputStream inputStream = null;
    private Toolbar toolbar;
    private RecyclerView recyclerView;
    private UserEntityDao userEntityDao;
    private BookShelfViewAdapter bookShelfViewAdapter;
    private MutableLiveData<List<LibraryBookEntity>> displayBookList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        immersiveStatusBar();
    }

    @Override
    protected void onActivityCreate() {
        //禁用滑动返回
        setSwipeBackEnable(false);
        setContentView(R.layout.activity_bookshelf);
    }


    @Override
    protected void bindView() {
        drawerLayout = findById(R.id.drawer_layout);
        navigationView = findById(R.id.nav_view);
        toolbar = findById(R.id.toolbar);
        setSupportActionBar(toolbar);
        recyclerView = findById(R.id.recyclerview_bookshelf);
    }

    @Override
    protected void initData() {
        userEntityDao = new UserEntityDao(MyApplication.getUser());
        displayBookList = new MutableLiveData<>();
        bookShelfViewAdapter = new BookShelfViewAdapter();
        recyclerView.setAdapter(bookShelfViewAdapter);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 3);
        recyclerView.setLayoutManager(gridLayoutManager);
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                displayBookList.postValue(userEntityDao.getLibrary());
            }
        });
        displayBookList.observe(this, new Observer<List<LibraryBookEntity>>() {
            @Override
            public void onChanged(@Nullable List<LibraryBookEntity> bookEntityList) {
                bookShelfViewAdapter.refreshItems(displayBookList.getValue());
            }
        });
    }

    @Override
    protected void bindEvent() {
        initDrawerEvent();
        initToolBarEvent();
        initOnItemLongClick();
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
                    startActivityByAnim(new Intent(BookshelfActivity.this, UserCenterActivity.class), R.anim.slide_in_right, R.anim.slide_out_right);
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
                int id = menuItem.getItemId();
                if (id == R.id.menu_add_book) {
                    Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                    intent.setType("file/*");
                    intent.addCategory(Intent.CATEGORY_OPENABLE);
                    try {
                        startActivityForResult(Intent.createChooser(intent, "选择文件"), FILE_SELECT_CODE);
                    } catch (android.content.ActivityNotFoundException ex) {
                        Toast.makeText(BookshelfActivity.this, "亲，木有文件管理器啊-_-!!", Toast.LENGTH_SHORT).show();
                    }
                    return true;
                }
                return false;
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO Auto-generated method stub
        if (resultCode != Activity.RESULT_OK) {
            Log.e(TAG, "onActivityResult() error, resultCode: " + resultCode);
            super.onActivityResult(requestCode, resultCode, data);
            return;
        }
        if (requestCode == FILE_SELECT_CODE) {
            Uri uri = data.getData();
            if (uri.getPath() != null && uri.getPath().endsWith(".epub")) {
                final String filePath = uri.getPath();
                Log.i(TAG, "onActivityResult: " + filePath);
                if (filePath != null) {
                    new AddBookAsyncTask(userEntityDao, BookshelfActivity.this, displayBookList)
                            .execute(filePath);
                }
            }else{
                Toast.makeText(this, "仅支持.epub类型的电子书", Toast.LENGTH_SHORT).show();
            }
            super.onActivityResult(requestCode, resultCode, data);
        }
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
        StatusBarUtil.setColorForDrawerLayout(this, drawerLayout, getResources().getColor(R.color.md_light_statusbar), 0);
        navigationView.setPadding(0, WidgetUtils.getStatusBarHeight(this), 0, 0);

    }

    /**
     * 设置长点击事件
     */
    private void initOnItemLongClick() {
        final String[] longclickitemlist = {"修改", "删除"};
        bookShelfViewAdapter.setOnItemLongClickListener(
                new BaseListAdapter.OnItemLongClickListener() {
                    @Override
                    public boolean onItemLongClick(View view, int pos) {
                        final int position = pos;
                        new AlertDialog.Builder(BookshelfActivity.this)
                                .setItems(longclickitemlist,
                                        new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog,
                                                                int which) {
                                                switch (which) {
                                                    case 0:
                                                        break;
                                                    case 1:
                                                        userEntityDao.deleteBook(bookShelfViewAdapter.getItem(position));
                                                }
                                                AsyncTask.execute(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        displayBookList.postValue(userEntityDao.getLibrary());
                                                    }
                                                });
                                            }
                                        })
                                .setNegativeButton("取消",
                                        new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog,
                                                                int which) {
                                                // TODO Auto-generated method stub

                                            }
                                        }).show();
                        return true;
                    }
                });
    }


    static class AddBookAsyncTask extends AsyncTask<String, Void, Result> {
        private UserEntityDao userEntityDao;
        private Context context;
        private MutableLiveData<List<LibraryBookEntity>> displayBookList;

        public AddBookAsyncTask(UserEntityDao userEntityDao, Context context, MutableLiveData<List<LibraryBookEntity>> displayBookList) {
            this.userEntityDao = userEntityDao;
            this.context = context;
            this.displayBookList = displayBookList;
        }

        @Override
        protected void onPreExecute() {
            Toast.makeText(context, "正在添加...", Toast.LENGTH_SHORT).show();
        }

        @Override
        protected void onPostExecute(Result result) {
            if (result instanceof Result.Success)
                Toast.makeText(context, "添加成功", Toast.LENGTH_SHORT).show();
            else
                Toast.makeText(context, "添加失败," + ((Result.Error) result).getError().getMessage(), Toast.LENGTH_SHORT).show();
        }

        @Override
        protected Result doInBackground(String... strings) {
            try {
                String filePath = strings[0];
                EpubReader epubReader = new EpubReader();
                InputStream in = new FileInputStream(new File(filePath));
                Book book = epubReader.readEpub(in);
                Result result = userEntityDao.addBook(book, filePath);
                displayBookList.postValue(userEntityDao.getLibrary());
                in.close();
                return result;
            } catch (Exception e) {
                Log.e(TAG, "onActivityResult: " + e.getMessage());
                e.printStackTrace();
                return new Result.Error(e);
            }
        }
    }
}
