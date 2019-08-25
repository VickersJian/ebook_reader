/* Created by Vickers Jian on 2019/07 */
package com.vickers.ebook_reader.widget.Text;

import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import android.content.Context;

import static com.vickers.ebook_reader.utils.TextUtils.CharacterFilter;

/**
 * 监测键盘输入
 */
public class MyTextWatcher {

    public static class DetetctLenthTextWatcher implements TextWatcher {

        private EditText editText;

        private Context content;

        private int max_lenth;

        private String regEx;

        private String word_error;

        private String overlength_error;

        public DetetctLenthTextWatcher(Context content, EditText editText, int max_lenth, String overlength_error, String regEx, String word_error) {
            this.content = content;
            this.editText = editText;
            this.max_lenth = max_lenth;
            this.overlength_error = overlength_error;
            this.regEx = regEx;
            this.word_error = word_error;
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {

            String str = CharacterFilter(s.toString(), regEx);
            if (!s.toString().equals(str)) {
                editText.setError(word_error);
            }

            if (s.length() > max_lenth) {
                editText.setError(overlength_error);
            }

        }
    }
}
