/* Created by Vickers Jian on 2019/07 */
package com.vickers.ebook_reader.View.activites;

import android.annotation.SuppressLint;
import android.app.Activity;

import android.content.Intent;


import android.os.AsyncTask;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.vickers.ebook_reader.Base.mBaseActivity;
import com.vickers.ebook_reader.MyApplication;
import com.vickers.ebook_reader.R;
import com.vickers.ebook_reader.data.Result;
import com.vickers.ebook_reader.data.dao.UserEntityDao;
import com.vickers.ebook_reader.data.entity.UserEntity;
import com.vickers.mylibrary.impl.BaseActivity.AppActivityManager;

import java.util.Objects;

/**
 * 用户中心界面
 */
public class UserCenterActivity extends mBaseActivity {

    private static final String TAG = "UserCenterActivity";

    private EditText userId;
    private EditText dispalyName;
    private EditText password;
    private CardView cardViewEdit;
    private CardView cardViewLogout;
    private CardView cardViewSave;
    private CardView cardViewCancel;
    private Toolbar toolbar;

    private UserEntity user;
    private UserEntityDao userEntityDao;

    @Override
    protected void onActivityCreate() {
        super.onActivityCreate();
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right);
        setContentView(R.layout.activity_user_center);

    }

    @Override
    protected void bindView() {
        toolbar = findById(R.id.toolbar);
        userId = findById(R.id.edt_userId);
        dispalyName = findById(R.id.edt_dispalyname);
        password = findById(R.id.edt_password);
        cardViewEdit = findById(R.id.edit);
        cardViewLogout = findById(R.id.logout);
        cardViewSave = findById(R.id.save);
        cardViewCancel = findById(R.id.cancel);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
    }

    @Override
    protected void initData() {
        Intent intent = getIntent();
        user = UserEntityDao.findUserByUserId(intent.getStringExtra("userid"));
        userEntityDao = new UserEntityDao(user);
        userId.setText(user.getUserID());
        dispalyName.setText(user.getDispalyName());
        password.setText(user.getPassword());
    }

    @Override
    protected void bindEvent() {
        cardViewLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MyApplication.setUser(null);
                startActivity(new Intent(UserCenterActivity.this, UserLoginActivity.class));
                AppActivityManager.getInstance().finishActivity(BookshelfActivity.class, UserCenterActivity.class);
            }
        });
        cardViewEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editModeViewContrl(true);
            }
        });
        cardViewSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editModeViewContrl(false);
                if (!user.getUserID().equals(userId.getText().toString())
                        || !user.getDispalyName().equals(dispalyName.getText().toString())
                        || !user.getPassword().equals(password.getText().toString())) {
                    UserEntity toUpdata = new UserEntity(userId.getText().toString(),
                            dispalyName.getText().toString(), password.getText().toString());
                    new UpDataUserTask(UserCenterActivity.this).execute(user, toUpdata);
                }
            }
        });
        cardViewCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editModeViewContrl(false);
                userId.setText(user.getUserID());
                dispalyName.setText(user.getDispalyName());
                password.setText(user.getPassword());
            }
        });
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right);
    }

    private void editModeViewContrl(boolean flag) {
        userId.setEnabled(flag);
        dispalyName.setEnabled(flag);
        password.setEnabled(flag);
        if (flag) {
            cardViewEdit.setVisibility(View.GONE);
            cardViewSave.setVisibility(View.VISIBLE);
            cardViewCancel.setVisibility(View.VISIBLE);
        } else {
            cardViewEdit.setVisibility(View.VISIBLE);
            cardViewSave.setVisibility(View.GONE);
            cardViewCancel.setVisibility(View.GONE);
        }
    }

    private static class UpDataUserTask extends AsyncTask<UserEntity, Void, Result> {
        @SuppressLint("StaticFieldLeak")
        private UserCenterActivity activity;

        public UpDataUserTask(UserCenterActivity activity) {
            this.activity = activity;
        }

        @Override
        protected Result doInBackground(UserEntity... userEntities) {
            return UserEntityDao.updataUser(userEntities[0], userEntities[1]);
        }

        @Override
        protected void onPostExecute(Result result) {
            if (result instanceof Result.Success) {
                Toast.makeText(activity, "已保存", Toast.LENGTH_SHORT).show();
                activity.user = (UserEntity) ((Result.Success) result).getData();
                MyApplication.setUser(activity.user);
                activity.setResult(Activity.RESULT_OK);
            } else {
                Toast.makeText(activity, "保存失败," + ((Result.Error) result).getError().getMessage(), Toast.LENGTH_SHORT).show();
                activity.userId.setText(activity.user.getUserID());
                activity.dispalyName.setText(activity.user.getDispalyName());
                activity.password.setText(activity.user.getPassword());
            }
            super.onPostExecute(result);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}

























