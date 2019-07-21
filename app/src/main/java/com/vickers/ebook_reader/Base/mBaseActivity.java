package com.vickers.ebook_reader.Base;

import android.os.Bundle;

import com.vickers.mylibrary.impl.BaseActivity.BaseActivity;
import com.vickers.mylibrary.utils.PermissionUtils;

public abstract class mBaseActivity extends BaseActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (PermissionUtils.isGrantExternalRW(this, 1)) {

        }
    }
}
