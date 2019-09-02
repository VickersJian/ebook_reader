/* Created by Vickers Jian on 2019/07 */
package com.vickers.ebook_reader.Base;


import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;


import com.vickers.ebook_reader.R;
import com.vickers.mylibrary.impl.BaseActivity.BaseActivity;
import com.vickers.mylibrary.utils.PermissionUtils;

import org.litepal.LitePal;

public abstract class mBaseActivity extends BaseActivity {
    protected final int FILE_SELECT_CODE = 1;
    protected final int CHANGE_BOOK_INFOR_CODE = 2;
    protected final int SEARCH_BOOK_CODE = 3;
    protected final int USER_CENTER_CODE = 4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (PermissionUtils.isGrantExternalRW(this, 1)) {

        }
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    }

    protected <V extends View> V findById(int id) {
        return (V) findViewById(id);
    }

}

