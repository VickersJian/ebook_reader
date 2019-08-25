/* Created by Vickers Jian on 2019/07 */
package com.vickers.ebook_reader.widget.Text;

import android.support.annotation.ColorRes;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.style.ClickableSpan;
import android.view.View;
import android.widget.TextView;
import android.text.style.UnderlineSpan;
import android.text.style.ForegroundColorSpan;
import android.content.Context;

/**
 * 可点击文本设置
 * 点击后添加下划线
 */
public class MyClickableSpan extends ClickableSpan {

    private int original_color;
    private int changed_color;
    private SpannableString spannableString;

    /**
     * @param spannableString 要设置的文本
     * @param original_color  点击前的字体颜色
     * @param changed_color   点击后的字体颜色
     */
    public MyClickableSpan(SpannableString spannableString, int original_color,
                           int changed_color) {
        this.spannableString = spannableString;
        this.original_color = original_color;
        this.changed_color = changed_color;
    }

    public MyClickableSpan(Context context, SpannableString spannableString, @ColorRes Integer original_color,
                           @ColorRes Integer changed_color) {
        this(spannableString, context.getResources().getColor(original_color),
                context.getResources().getColor(changed_color));
    }

    @Override
    public void updateDrawState(TextPaint ds) {
        //原先样式
        ds.setColor(original_color);
        ds.setUnderlineText(false);
    }

    @Override
    public void onClick(View widget) {
        //点击后的样式
        spannableString.setSpan(new UnderlineSpan(),
                0, spannableString.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannableString.setSpan(new ForegroundColorSpan(changed_color),
                0, spannableString.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        if (widget instanceof TextView) {
            ((TextView) widget).setText(spannableString);
        }
    }
}
