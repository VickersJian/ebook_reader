/* Created by Vickers Jian on 2019/08 */
package com.vickers.ebook_reader.widget;

import android.content.Context;
import android.content.res.TypedArray;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;

import android.support.v7.widget.AppCompatImageView;
import android.util.AttributeSet;

import com.vickers.ebook_reader.R;

/**
 * 优先级从顺序：<p>
 * IsWidthFitDrawableSizeRatio<p>
 * IsHeightFitDrawableSizeRatio<p>
 * WidthRatio<p>
 * HeightRatio<p>
 * 即如果设置了IsWidthFitDrawableSizeRatio为true，则优先级较低的三个值不生效
 **/
public class RatioImageView extends AppCompatImageView {

    private float DrawableSizeRatio = -1f; // src图片(前景图)的宽高比例
    // 根据前景图宽高比例测量View,防止图片缩放变形
    private boolean IsWidthFitDrawableSizeRatio; // 宽度是否根据src图片(前景图)的比例来测量（高度已知）
    private boolean IsHeightFitDrawableSizeRatio; // 高度是否根据src图片(前景图)的比例来测量（宽度已知）
    // 宽高比例
    private float WidthRatio = -1; // 宽度 = 高度*mWidthRatio
    private float HeightRatio = -1; // 高度 = 宽度*mHeightRatio

    public RatioImageView(Context context) {
        super(context);
    }

    public RatioImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
        // 一定要有此代码
        if (getDrawable() != null) {
            DrawableSizeRatio = 1f * getDrawable().getIntrinsicWidth()
                    / getDrawable().getIntrinsicHeight();
        }
    }

    public RatioImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    /**
     * 初始化变量
     */
    private void init(AttributeSet attrs) {

        TypedArray a = getContext().obtainStyledAttributes(attrs,
                R.styleable.RatioImageView);
        IsWidthFitDrawableSizeRatio = a.getBoolean(R.styleable.RatioImageView_is_width_fix_drawable_size_ratio,
                IsWidthFitDrawableSizeRatio);
        IsHeightFitDrawableSizeRatio = a.getBoolean(R.styleable.RatioImageView_is_height_fix_drawable_size_ratio,
                IsHeightFitDrawableSizeRatio);
        HeightRatio = a.getFloat(
                R.styleable.RatioImageView_height_to_width_ratio, HeightRatio);
        WidthRatio = a.getFloat(
                R.styleable.RatioImageView_width_to_height_ratio, WidthRatio);
        a.recycle();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        // 优先级从大到小：
        // mIsWidthFitDrawableSizeRatio mIsHeightFitDrawableSizeRatio
        // mWidthRatio mHeightRatio
        if (DrawableSizeRatio > 0) {
            // 根据前景图宽高比例来测量view的大小
            if (IsWidthFitDrawableSizeRatio) {
                WidthRatio = DrawableSizeRatio;
            } else if (IsHeightFitDrawableSizeRatio) {
                HeightRatio = 1 / DrawableSizeRatio;
            }
        }

        if (HeightRatio > 0 && WidthRatio > 0) {
            throw new RuntimeException("高度和宽度不能同时设置百分比！！");
        }

        if (WidthRatio > 0) { // 高度已知，根据比例，设置宽度
            int height = MeasureSpec.getSize(heightMeasureSpec);
            super.onMeasure(MeasureSpec.makeMeasureSpec(
                    (int) (height * WidthRatio), MeasureSpec.EXACTLY),
                    MeasureSpec.makeMeasureSpec(height, MeasureSpec.EXACTLY));
        } else if (HeightRatio > 0) { // 宽度已知，根据比例，设置高度
            int width = MeasureSpec.getSize(widthMeasureSpec);
            super.onMeasure(MeasureSpec.makeMeasureSpec(width,
                    MeasureSpec.EXACTLY), MeasureSpec.makeMeasureSpec(
                    (int) (width * HeightRatio), MeasureSpec.EXACTLY));
        } else { // 系统默认测量
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        }

    }

    @Override
    public void setImageResource(int resId) {
        super.setImageResource(resId);
        if (getDrawable() != null) {
            DrawableSizeRatio = 1f * getDrawable().getIntrinsicWidth()
                    / getDrawable().getIntrinsicHeight();
            if (DrawableSizeRatio > 0
                    && (IsWidthFitDrawableSizeRatio || IsHeightFitDrawableSizeRatio)) {
                requestLayout();
            }
        }
    }

    @Override
    public void setImageDrawable(Drawable drawable) {
        super.setImageDrawable(drawable);
        if (getDrawable() != null) {
            DrawableSizeRatio = 1f * getDrawable().getIntrinsicWidth()
                    / getDrawable().getIntrinsicHeight();
            if (DrawableSizeRatio > 0
                    && (IsWidthFitDrawableSizeRatio || IsHeightFitDrawableSizeRatio)) {
                requestLayout();
            }
        }
    }

    @Override
    public void setImageBitmap(Bitmap bm) {
        super.setImageBitmap(bm);
        if (getDrawable() != null) {
            DrawableSizeRatio = 1f * getDrawable().getIntrinsicWidth()
                    / getDrawable().getIntrinsicHeight();
            if (DrawableSizeRatio > 0
                    && (IsWidthFitDrawableSizeRatio || IsHeightFitDrawableSizeRatio)) {
                requestLayout();
            }
        }
    }
}
