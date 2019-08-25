/* Created by Vickers Jian on 2019/07 */
package com.vickers.ebook_reader.utils;

import android.text.InputFilter;
import android.widget.EditText;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

/**
 * 文本输入处理工具
 */
public class TextUtils {
    /**
     * 设置EditText可输入字符数
     * 该使用该方法会与其他过滤器冲突
     *
     * @param editText 需要设置的文本
     */
    public static void setEditTextLengthLimit(EditText editText, int maxlength) {
        editText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(maxlength)});
    }

    /**
     * 限制字符类型过滤器
     *
     * @param str   待处理字符串
     * @param regEx 允许的字符类型(正则表达式)
     * @return 仅包含允许的字符类型的字符串
     */
    public static String CharacterFilter(String str, String regEx) throws PatternSyntaxException {
        Pattern pattern = Pattern.compile(regEx);
        Matcher matcher = pattern.matcher(str);
        StringBuilder stringBuilder = new StringBuilder();
        while (matcher.find())
            stringBuilder.append(matcher.group());
        return stringBuilder.toString();
    }


}
