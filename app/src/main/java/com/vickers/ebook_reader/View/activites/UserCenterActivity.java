package com.vickers.ebook_reader.View.activites;


import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.WindowManager;
import android.widget.ImageView;

import com.vickers.ebook_reader.Base.mBaseActivity;
import com.vickers.ebook_reader.R;


public class UserCenterActivity extends mBaseActivity {

    @Override
    protected void onActivityCreate() {
        overridePendingTransition(R.anim.slide_in_right,R.anim.slide_out_right);
        setContentView(R.layout.activity_user_center);
        ImageView imageView = (ImageView) findViewById(R.id.image_user_head);
//      imageView.setImageResource(R.drawable.image_user_head);

        BitmapFactory.Options options = new BitmapFactory.Options();
        BitmapFactory.decodeFile("/sdcard/image_user_head.jpg", options);
        //不去真的解析图片，只是获取图片的头部信息，宽高
        options.inJustDecodeBounds = true;
        //得到图片的真实宽高
        int imageHeight = options.outHeight;
        int imageWidth = options.outWidth;
        //得到屏幕的宽高
        WindowManager wm = (WindowManager) getSystemService(WINDOW_SERVICE);
        int screenHeight = wm.getDefaultDisplay().getHeight();
        int screenWidth = wm.getDefaultDisplay().getWidth();
        //得到缩放比例
        int scale = 1;
        int scaleX = imageWidth / screenWidth;
        int scaleY = imageHeight / screenHeight;
        if (scaleX > scaleY & scaleX >= 1) {//表示如果宽的缩放比例大于高的，并且scaleX>=1都为true
            scale = scaleX;
        }
        if (scaleY > scaleX & scaleY >= 1) {//表示如果高的缩放比例大于宽的，并且scaleY>=1都为true
            scale = scaleY;
        }
        //解析图片
        options.inJustDecodeBounds = false;
        //修改图片的缩放比例，如果scale=4说明图片缩小4倍，像数=1/16
        options.inSampleSize = scale;
        Bitmap bm = BitmapFactory.decodeFile("/sdcard/image_user_head.jpg", options);
        imageView.setImageBitmap(bm);
    }
    @Override
    public void finish(){
        super.finish();
        overridePendingTransition(R.anim.slide_in_right,R.anim.slide_out_right);
    }
}
