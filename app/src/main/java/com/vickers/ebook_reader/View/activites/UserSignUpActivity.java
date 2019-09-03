/* Created by Vickers Jian on 2019/07 */
package com.vickers.ebook_reader.View.activites;

import android.text.InputFilter;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.vickers.ebook_reader.Base.mBaseActivity;
import com.vickers.ebook_reader.R;
import com.vickers.ebook_reader.Base.Result;

import com.vickers.ebook_reader.data.dao.UserEntityDao;
import com.vickers.ebook_reader.widget.Text.MyInputFilter;

import static com.vickers.ebook_reader.utils.regExKey.PASSWORD_WORD;
import static com.vickers.ebook_reader.utils.regExKey.USER_NAME_WORD;

/**
 * 用户注册界面
 */
public class UserSignUpActivity extends mBaseActivity {

    private EditText edt_userId;
    private EditText edt_displayName;
    private EditText edt_password;
    private Button btn_signUp;

//    private UserModelDao userModelDao=new UserModelDao();

    @Override
    protected void onActivityCreate() {
        super.onActivityCreate();
        setContentView(R.layout.activity_user_sign_up);
    }

    @Override
    protected void bindView() {
        edt_userId = findById(R.id.edt_userId);
        edt_displayName = findById(R.id.edt_dispalyName);
        edt_password = findById(R.id.edt_password);
        btn_signUp = findById(R.id.btn_sign_up);
    }

    @Override
    protected void bindEvent() {
        initSignupButtonEvent();//设置登录Button事件
        initEditTextEvent();//设置editText输入响应事件
    }

    /**
     * 设置editText输入响应事件
     */
    private void initEditTextEvent() {
        //添加过滤器
        edt_userId.setFilters(new InputFilter[]{
                new MyInputFilter.EditTextLengthFilter(this, edt_userId, 20, R.string.overlength_error_20),
                new MyInputFilter.EditTextCharacterFilter(this, edt_userId, USER_NAME_WORD, R.string.word_error)
        });
        edt_displayName.setFilters(new InputFilter[]{
                new MyInputFilter.EditTextLengthFilter(this, edt_password, 30, R.string.overlength_error_30),
                new MyInputFilter.EditTextCharacterFilter(this, edt_password, USER_NAME_WORD, R.string.word_error)
        });
        edt_password.setFilters(new InputFilter[]{
                new MyInputFilter.EditTextLengthFilter(this, edt_password, 30, R.string.overlength_error_30),
                new MyInputFilter.EditTextCharacterFilter(this, edt_password, PASSWORD_WORD, R.string.word_error)
        });

        //设置密码框响应键盘登录按钮
        edt_password.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
                //响应登录按钮
                if (actionId == EditorInfo.IME_ACTION_DONE || actionId == getResources().getInteger(R.integer.ACTION_SIGN_UP)) {
                    btn_signUp.performClick();
                }
                return false;
            }
        });
    }

    /**
     * 设置登录Button事件
     */
    private void initSignupButtonEvent() {
        btn_signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //输入检测
                if (edt_userId.getText().toString().isEmpty()) {
                    edt_userId.setError(getResources().getString(R.string.name_null_error));
                    //重新获取焦点
                    edt_userId.requestFocus();
                } else if (edt_password.getText().toString().isEmpty()) {
                    edt_password.setError(getResources().getString(R.string.password_null_error));
                    //重新获取焦点
                    edt_password.requestFocus();
                } else {
                    Result result = UserEntityDao.addUser(edt_userId.getText().toString(), edt_displayName.getText().toString(),
                            edt_password.getText().toString());
                    if (result instanceof Result.Success) {
                        Toast.makeText(getApplicationContext(), "注册成功！", Toast.LENGTH_SHORT).show();
                        finish();
                    } else {
                        Toast.makeText(getApplicationContext(), ((Result.Error) result).getError().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }
}
