/* Created by Vickers Jian on 2019/08 */
package com.vickers.ebook_reader.Helper;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v4.util.LruCache;
import android.util.Log;
import android.widget.ImageView;

import com.vickers.ebook_reader.data.Result;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * 载入图片
 *
 * @param K 缓存键值类型
 * @param D 图片来源类型
 * @param V 显示图片的控件类型
 */
public class ImageLoadHelper<K, D, V extends ImageView> {

    private static final String TAG = "ImageLoadHelper";

    /**
     * MOD_KEEP 取(原宽度/指定宽度)和(原高度/指定高度)中比例最小的压缩
     * MOD_LIMITE 取(原宽度/指定宽度)和(原高度/指定高度)中比例最大的压缩
     */
    public static final int MOD_KEEP = 0;
    public static final int MOD_LIMITE = 1;

//    private K key;
//    private V imageView;
//    private D data;
    private LoadTask mLoadTask;
    private LruCache<K, Bitmap> mMemoryCache;
    private int reqWidth;
    private int reqHeight;
    private int MOD;

    public ImageLoadHelper(final int reqWidth, final int reqHeight, int MOD) {
        this.reqHeight = reqHeight;
        this.reqWidth = reqWidth;
        this.MOD = MOD;
        //缓存大小为可用内存的1/8
        int cacheSize = (int) (Runtime.getRuntime().maxMemory() / 1024) / 8;
        mMemoryCache = new LruCache<K, Bitmap>(cacheSize) {
            @Override
            protected int sizeOf(K key, Bitmap bitmap) {
                // 重写此方法来衡量每张图片的大小
                return bitmap.getByteCount() / 1024;
            }
        };
    }

    /**
     * 压缩解析图片
     *
     * @param context   上下文，当图片不为Resources类型的可设为null
     * @param data      图片数据，类型仅允许为Resources、InputStream、String(图片路径)
     * @param reqWidth  指定的宽
     * @param reqHeight 指定的高
     * @param MOD       压缩模式<p>
     *                  MOD_KEEP 取(原宽度/指定宽度)和(原高度/指定高度)中比例最小的压缩<p>
     *                  MOD_LIMITE 取(原宽度/指定宽度)和(原高度/指定高度)中比例最大的压缩
     * @return 压缩后的图片
     */
    public static <E> Bitmap decodeSampledBitmap(Context context, E data, int reqWidth, int reqHeight, int MOD) {
        // 第一次解析将inJustDecodeBounds设置为true，来获取图片大小
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        if (context != null && data.equals(int.class)) {
            BitmapFactory.decodeResource(context.getResources(), (int) data, options);
            options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight, MOD);
            options.inJustDecodeBounds = false;
            return BitmapFactory.decodeResource(context.getResources(), (int) data, options);
        } else if (data instanceof InputStream) {
            InputStream inputStream= (InputStream) data;
            ByteArrayOutputStream copy = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            int len;
            try {
                while ((len = inputStream.read(buffer)) > -1 ) {
                    copy.write(buffer, 0, len);
                }
                copy.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
            InputStream toCalculate = new ByteArrayInputStream(copy.toByteArray());
            InputStream toReturn = new ByteArrayInputStream(copy.toByteArray());
            BitmapFactory.decodeStream(toCalculate, null, options);
            options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight, MOD);
            options.inJustDecodeBounds = false;
            Bitmap bitmap= BitmapFactory.decodeStream(toReturn, null, options);
            Log.i(TAG, "decodeSampledBitmap: "+options.inSampleSize);
            return bitmap;
        } else if (data instanceof String) {
            BitmapFactory.decodeFile((String) data, options);
            options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight, MOD);
            options.inJustDecodeBounds = false;
            return BitmapFactory.decodeFile((String) data, options);
        } else
            return null;
    }

    /**
     * 计算压缩率
     *
     * @param options   图片的BitmapFactory.Options
     * @param reqWidth  指定的宽
     * @param reqHeight 指定的高
     * @param MOD       压缩模式<p>
     *                  MOD_KEEP 取(原宽度/指定宽度)和(原高度/指定高度)中比例最小的压缩<p>
     *                  MOD_LIMITE 取(原宽度/指定宽度)和(原高度/指定高度)中比例最大的压缩
     * @return int 压缩率
     */
    public static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight, int MOD) {
        // 源图片的高度和宽度
        final int height = options.outHeight;
        final int width = options.outWidth;
        Log.i(TAG, "calculateInSampleSize: " + width);
        Log.i(TAG, "calculateInSampleSize: " + height);
        int inSampleSize = 1;
        if (height > reqHeight || width > reqWidth) {
            // 计算出实际宽高和目标宽高的比率
            final int heightRatio = Math.round((float) height / (float) reqHeight);
            final int widthRatio = Math.round((float) width / (float) reqWidth);
            if (MOD == MOD_KEEP)
                inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
            else
                inSampleSize = heightRatio > widthRatio ? heightRatio : widthRatio;
        }
        Log.i(TAG, "calculateInSampleSize: " + inSampleSize);
        return inSampleSize;
    }

    /**
     * 载入图片
     *
     * @param key       缓存键值
     * @param data      图片数据
     * @param imageView 需要显示的控件
     */
    public void loadBitmap(K key, D data, V imageView) {
        final Bitmap bitmap = getBitmapFromMemCache(key);
        if (bitmap != null) {
            Log.i(TAG, "loadBitmap: " + "old");
            imageView.setImageBitmap(bitmap);
        } else {
            BitmapLoadTask<K, D, V> bitmapLoadTask = new BitmapLoadTask(mLoadTask, this, key, imageView, reqWidth, reqHeight, MOD);
            bitmapLoadTask.execute(data);
            Log.i(TAG, "loadBitmap: " + "new");
        }
    }

    /**
     * 添加图片到缓存
     *
     * @param key    键值
     * @param bitmap 图片
     */
    public void addBitmapToMemoryCache(K key, Bitmap bitmap) {
        if (getBitmapFromMemCache(key) == null) {
            mMemoryCache.put(key, bitmap);
        }
    }

    /**
     * 从缓存中得到图片
     *
     * @param key 所需图片键值
     * @return Bitmap
     */
    public Bitmap getBitmapFromMemCache(K key) {
        return mMemoryCache.get(key);
    }


    public interface LoadTask {
        void onPreExecute();

        <D> Result doInBackground(D... data);

        void onPostExecute(Result result);
    }

    /**
     * 设置图片加载方式
     * 返回null则为默认加载方式
     * 否则默认加载方式将会失效
     * <p>
     * K--缓存键值类型<p>
     * D--图片来源类型<p>
     */
    public void setImageLoadAsyncTask(LoadTask loadTask) {
        this.mLoadTask = loadTask;
    }

    /**
     * 内部类，用于异步加载图片
     * <p>
     * K 缓存键值类型<p>
     * D 图片来源类型<p>
     * V 显示图片的控件类型
     */
    private static class BitmapLoadTask<K, D, V extends ImageView> extends AsyncTask<D, Void, Result> {
        private LoadTask mLoadTask;
        private K key;
        private V imageView;
        private ImageLoadHelper imageLoadHelper;
        private int reqWidth;
        private int reqHeight;
        private int MOD;

        public BitmapLoadTask(LoadTask mLoadTask, ImageLoadHelper imageLoadHelper, K key, V imageView, int reqWidth, int reqHeight, int MOD) {
            this.mLoadTask = mLoadTask;
            this.imageLoadHelper = imageLoadHelper;
            this.key = key;
            this.imageView = imageView;
            this.reqHeight = reqHeight;
            this.reqWidth = reqWidth;
            this.MOD = MOD;
        }

        @Override
        protected void onPreExecute() {
            mLoadTask.onPreExecute();
        }

        @Override
        protected Result doInBackground(D... data) {
            if(mLoadTask.doInBackground(data)==null)
                return new Result.Success<>(decodeSampledBitmap(null,data[0],reqWidth,reqHeight,MOD));
            else return mLoadTask.doInBackground(data);
        }

        @Override
        protected void onPostExecute(Result result) {
            mLoadTask.onPostExecute(result);
            if (result instanceof Result.Success) {
                if (((Result.Success) result).getData() instanceof Bitmap) {
                    Bitmap bitmap = (Bitmap) ((Result.Success) result).getData();
                    imageView.setImageBitmap(bitmap);
                    imageLoadHelper.addBitmapToMemoryCache(key, bitmap);
                }
            }
        }
    }
}
