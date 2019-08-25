/* Created by Vickers Jian on 2019/07 */
package com.vickers.ebook_reader.widget.Text;

import android.content.Context;
import android.support.annotation.StringRes;
import android.text.InputFilter;
import android.text.Spanned;
import android.widget.EditText;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 文本输入过滤器
 */
public class MyInputFilter {

    private MyInputFilter(){

    }

    /**
     * editText限制字符类型过滤器
     */
    public static class EditTextCharacterFilter implements InputFilter {

        private EditText editText;
        private String regEx;
        private String character_error;

        /**
         * @param editText        需要进行错误提示的editText
         * @param regEx           允许的字符类型(正则表达式)
         * @param character_error 显示错误提示
         */
        public EditTextCharacterFilter(EditText editText, String regEx, String character_error) {
            this.editText = editText;
            this.regEx = regEx;
            this.character_error = character_error;
        }

        public EditTextCharacterFilter(Context context, EditText editText, String regEx, @StringRes Integer character_error) {
            this(editText, regEx, context.getString(character_error));
        }

        @Override
        public CharSequence filter(CharSequence source, int start, int end, Spanned dest,
                                   int dstart, int dend) {
//            Log.i("CharacterFilter", source.toString());
            if (end - start <= 0) {//判断source是否为"",是则不过滤，否则过滤
                return null;
            } else {
                Pattern pattern = Pattern.compile(regEx);
                Matcher matcher = pattern.matcher(source);
//                Log.i("CharacterFilter", String.valueOf(source == ""));
                if (matcher.matches()) {
                    return null;
                } else {
                    editText.setError(character_error);
                    return "";
                }
            }
        }

        /**
         * 返回允许字符类型的正则表达式
         */
        public String getregEx() {
            return regEx;
        }
    }

    /**
     * editText长度限制过滤器
     */
    public static class EditTextLengthFilter implements InputFilter {

        private EditText editText;
        private int maxLenth;
        private String overlength_error;

        /**
         * @param editText         需要进行错误提示的editText
         * @param maxLenth         最大输入字符数
         * @param overlength_error 显示错误提示
         */
        public EditTextLengthFilter(EditText editText, int maxLenth, String overlength_error) {
            this.editText = editText;
            this.maxLenth = maxLenth;
            this.overlength_error = overlength_error;
        }

        public EditTextLengthFilter(Context context, EditText editText, int maxLenth, @StringRes Integer errorString) {
            this(editText, maxLenth, context.getResources().getString(errorString));
        }

        @Override
        public CharSequence filter(CharSequence source, int start, int end, Spanned dest,
                                   int dstart, int dend) {
            int keep = maxLenth - (dest.length() - (dend - dstart));
//            Log.i("LengthFilter", source.toString() + " start " + start + " end " + end + " keep " + keep);
            if (keep <= 0) {
                //超出后错误提示
                editText.setError(overlength_error);
                return "";
            } else if (keep >= end - start) {
                return null; // keep original
            } else {
                keep += start;
                if (Character.isHighSurrogate(source.charAt(keep - 1))) {
                    --keep;
                    if (keep == start) {
                        return "";
                    }
                }
                return source.subSequence(start, keep);
            }
        }

        /**
         * @return 此输入筛选器强制的最大长度
         */
        public int getmaxLenth() {
            return maxLenth;
        }
    }

}
