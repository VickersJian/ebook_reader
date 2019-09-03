/* Created by Vickers Jian on 2019/07 */
package com.vickers.ebook_reader.View.activites;

import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Observer;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.InputFilter;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextWatcher;
import android.text.method.LinkMovementMethod;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.vickers.ebook_reader.Base.mBaseActivity;
import com.vickers.ebook_reader.MyApplication;
import com.vickers.ebook_reader.Base.Result;

import com.vickers.ebook_reader.data.dao.UserEntityDao;
import com.vickers.ebook_reader.data.entity.UserEntity;

import com.vickers.ebook_reader.widget.Text.MyClickableSpan;
import com.vickers.ebook_reader.widget.Text.MyInputFilter;
import com.vickers.ebook_reader.R;


import static com.vickers.ebook_reader.utils.regExKey.PASSWORD_WORD;
import static com.vickers.ebook_reader.utils.regExKey.USER_NAME_WORD;

/**
 * 用户登录界面
 */
public class UserLoginActivity extends mBaseActivity {

    private Button btn_signIn;
    private TextView text_SignUp;
    private EditText edt_userId;
    private EditText edt_password;
    private CheckBox checkBox_remenber_password;
    private CheckBox checkBox_auto_login;
    private ProgressBar progressBar;

    private SharedPreferences UserPreferences;
    private MutableLiveData<Boolean> LoginResult = new MutableLiveData<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        UserPreferences = MyApplication.getUserPreferences();
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onActivityCreate() {
        setContentView(R.layout.activity_user_login);
        setSwipeBackEnable(false);//禁用滑动返回
    }

    @Override
    protected void bindView() {
        btn_signIn = findById(R.id.btn_login);
        text_SignUp = findById(R.id.text_sign_up);
        edt_userId = findById(R.id.edt_userId);
        edt_password = findById(R.id.edt_password);
        checkBox_remenber_password = findById(R.id.checkbox_remember_password);
        checkBox_auto_login = findById(R.id.checkbox_auto_login);
        progressBar = findById(R.id.progressbar_loading);
    }

    @Override
    protected void bindEvent() {
        initLoginButtonEvent();//用户登录
        initEditTextEvent();//用户输入提示
        initSignUpTextViewEvent();//用户注册
        initProgressBarEvent();//加载界面
        initCheckBoxEvent();//记住密码
    }

    /**
     * 设置登录Button事件
     */
    private void initLoginButtonEvent() {
        btn_signIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBar.setVisibility(View.VISIBLE);
                viewControler(false);
                //输入检测
                if (edt_userId.getText().toString().isEmpty()) {
                    edt_userId.setError(getResources().getString(R.string.name_null_error));
                    LoginResult.setValue(false);
                    //重新获取焦点
                    edt_userId.requestFocus();
                } else if (edt_password.getText().toString().isEmpty()) {
                    edt_password.setError(getResources().getString(R.string.password_null_error));
                    LoginResult.setValue(false);
                    //重新获取焦点
                    edt_password.requestFocus();
                } else {
                    //身份验证
                    Result<UserEntity> result = UserEntityDao.login(edt_userId.getText().toString(),
                            edt_password.getText().toString());
                    if (result instanceof Result.Success) {
                        MyApplication.setUser(((Result.Success<UserEntity>) result).getData());
                        LoginResult.setValue(true);
                        //记住密码
                        SharedPreferences.Editor editor = UserPreferences.edit();
                        editor.putString("user", edt_userId.getText().toString());
                        if (checkBox_remenber_password.isChecked()) {
                            editor.putString("password", edt_password.getText().toString());
                            editor.putBoolean("remember_password", true);
                        } else {
                            editor.putString("password", "");
                            editor.putBoolean("remember_password", false);
                        }
                        if (checkBox_auto_login.isChecked())
                            editor.putBoolean("auto_login", true);
                        else editor.putBoolean("auto_login", false);
                        editor.apply();
                        startActivity(new Intent(UserLoginActivity.this, BookshelfActivity.class));
                        finish();
                    } else {
                        Toast.makeText(getApplicationContext(), ((Result.Error) result).getError().getMessage(), Toast.LENGTH_SHORT).show();
                        LoginResult.setValue(false);
                    }
                }
            }
        });
    }

    /**
     * 设置editText输入响应事件.
     */
    private void initEditTextEvent() {

        //添加过滤器
        edt_userId.setFilters(new InputFilter[]{
                new MyInputFilter.EditTextLengthFilter(this, edt_userId, 20, R.string.overlength_error_20),
                new MyInputFilter.EditTextCharacterFilter(this, edt_userId, USER_NAME_WORD, R.string.word_error)
        });
        edt_password.setFilters(new InputFilter[]{
                new MyInputFilter.EditTextLengthFilter(this, edt_password, 30, R.string.overlength_error_30),
                new MyInputFilter.EditTextCharacterFilter(this, edt_password, PASSWORD_WORD, R.string.word_error)
        });

        //填写上一次登录的用户名
        edt_userId.setText(UserPreferences.getString("user", ""));

        edt_userId.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                //检测登录用户是否更改，是则去除上一次登录的用户信息
                if (!s.toString().equals(UserPreferences.getString("user", ""))) {
                    edt_password.setText("");
                    SharedPreferences.Editor editor = UserPreferences.edit();
                    editor.clear();
                    editor.apply();
                }
            }
        });

        edt_password.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!s.toString().equals(UserPreferences.getString("password", ""))) {
                    SharedPreferences.Editor editor = UserPreferences.edit();
                    editor.putBoolean("remember_password", false);
                    editor.apply();
                }
            }
        });

        //设置enter键显示的标签
        edt_password.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
                //响应登录按钮
                if (actionId == EditorInfo.IME_ACTION_DONE || actionId == getResources().getInteger(R.integer.ACTION_SIGN_IN)) {
                    btn_signIn.performClick();
                }
                return false;
            }
        });
    }

    /**
     * 设置可点击TextView及其事件（用户注册）.
     */
    private void initSignUpTextViewEvent() {
        text_SignUp.setHighlightColor(getResources().getColor(R.color.md_grey_600));
        SpannableString SignUpString = new SpannableString(getResources().getString(R.string.sign_up_long));
        //设置点击事件
        SignUpString.setSpan(new MyClickableSpan(this, SignUpString, R.color.md_light_blue_600,
                R.color.md_deep_orange_800) {
            @Override
            public void onClick(View widget) {
                super.onClick(widget);
                //进入注册界面
                startActivity(new Intent(UserLoginActivity.this, UserSignUpActivity.class));
            }
        }, 0, SignUpString.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        text_SignUp.setText(SignUpString);
        text_SignUp.setMovementMethod(LinkMovementMethod.getInstance());//开始响应点击事件
    }

    /**
     * 加载界面事件
     */
    private void initProgressBarEvent() {
        LoginResult.observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(@Nullable Boolean aBoolean) {
                progressBar.setVisibility(View.GONE);
                viewControler(true);
            }
        });
    }

    /**
     * CheckBox事件
     */
    private void initCheckBoxEvent() {

        if (UserPreferences.getBoolean("remember_password", false)) {
            edt_password.setText(UserPreferences.getString("password", ""));
            checkBox_remenber_password.setChecked(true);
        }
        if (!checkBox_auto_login.isChecked()) {
            SharedPreferences.Editor editor = MyApplication.getUserPreferences().edit();
            editor.putBoolean("auto_login", false);
            editor.apply();
        }
        checkBox_remenber_password.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (!isChecked) {
                    checkBox_auto_login.setChecked(false);
                    SharedPreferences.Editor editor = UserPreferences.edit();
                    editor.putString("password", "");
                    editor.putBoolean("remember_password", false);
                    editor.apply();
                    edt_password.setText("");
                }
            }
        });
        checkBox_auto_login.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    checkBox_remenber_password.setChecked(true);
                }
            }
        });
    }

    /**
     * 控件设置
     */
    private void viewControler(boolean flag) {
        btn_signIn.setEnabled(flag);
        edt_userId.setEnabled(flag);
        edt_password.setEnabled(flag);
    }
}
