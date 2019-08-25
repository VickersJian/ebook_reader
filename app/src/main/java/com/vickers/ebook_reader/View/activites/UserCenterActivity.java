/* Created by Vickers Jian on 2019/07 */
package com.vickers.ebook_reader.View.activites;

import android.app.Activity;

import android.content.Intent;

import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;

import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.vickers.ebook_reader.Base.mBaseActivity;
import com.vickers.ebook_reader.MyApplication;
import com.vickers.ebook_reader.R;
import com.vickers.mylibrary.impl.BaseActivity.AppActivityManager;

/**
 * 用户中心界面
 */
public class UserCenterActivity extends mBaseActivity {

//    private static final String TAG = "user_head_path";
    private Button btn_logout;
//    ImageView imageView;


    @Override
    protected void onActivityCreate() {
        super.onActivityCreate();
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right);
        setContentView(R.layout.activity_user_center);

    }

    @Override
    protected void bindView() {
//        imageView = (ImageView) findViewById(R.id.image_user_head);
        btn_logout = (Button) findViewById(R.id.logout);

    }

    @Override
    protected void bindEvent() {
        btn_logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MyApplication.setUser(null);
                startActivity(new Intent(UserCenterActivity.this,UserLoginActivity.class));
                AppActivityManager.getInstance().finishActivity(BookshelfActivity.class,UserCenterActivity.class);
            }
        });
//        btn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
//                intent.setType("image/*");//选择图片
////                intent.setType("*/*");//任意类型
////                intent.setType("audio/*"); //选择音频
////                intent.setType("video/*"); //选择视频 （mp4 3gp 是android支持的视频格式）
////                intent.setType("video/*image/*");//同时选择视频和图片
//                intent.addCategory(Intent.CATEGORY_OPENABLE);
//                try {
//                    startActivityForResult(Intent.createChooser(intent, "选择文件"), FILE_SELECT_CODE);
//                } catch (android.content.ActivityNotFoundException ex) {
//                    Toast.makeText(UserCenterActivity.this, "亲，木有文件管理器啊-_-!!", Toast.LENGTH_SHORT).show();
//                }
//            }
//        });
    }
//
//    @Override
//    public void onActivityResult(int requestCode, int resultCode, Intent data) {
//        String path_image_user_head;
//        // TODO Auto-generated method stub
//        if (resultCode != Activity.RESULT_OK) {
//            Log.e(TAG, "onActivityResult() error, resultCode: " + resultCode);
//            super.onActivityResult(requestCode, resultCode, data);
//            return;
//        }
//        if (requestCode == FILE_SELECT_CODE) {
//            Uri uri = data.getData();
//            path_image_user_head = GetRealPathFromUriUtils.getRealPathFromUri(UserCenterActivity.this, uri);
//
////      imageView.setImageResource(R.drawable.image_user_head);
//
//            BitmapFactory.Options options = new BitmapFactory.Options();
//            BitmapFactory.decodeFile(path_image_user_head, options);
//            //不去真的解析图片，只是获取图片的头部信息，宽高
//            options.inJustDecodeBounds = true;
//            //得到图片的真实宽高
//            int imageHeight = options.outHeight;
//            int imageWidth = options.outWidth;
//            //得到屏幕的宽高
//            WindowManager wm = (WindowManager) getSystemService(WINDOW_SERVICE);
//            int screenHeight = wm.getDefaultDisplay().getHeight();
//            int screenWidth = wm.getDefaultDisplay().getWidth();
//            //得到缩放比例
//            int scale = 1;
//            int scaleX = imageWidth / screenWidth;
//            int scaleY = imageHeight / screenHeight;
//            if (scaleX > scaleY & scaleX >= 1) {//表示如果宽的缩放比例大于高的，并且scaleX>=1都为true
//                scale = scaleX;
//            }
//            if (scaleY > scaleX & scaleY >= 1) {//表示如果高的缩放比例大于宽的，并且scaleY>=1都为true
//                scale = scaleY;
//            }
//            //解析图片
//            options.inJustDecodeBounds = false;
//            //修改图片的缩放比例，如果scale=4说明图片缩小4倍，像数=1/16
//            options.inSampleSize = scale;
//            Bitmap bm = BitmapFactory.decodeFile(path_image_user_head, options);
//            imageView.setImageBitmap(bm);
//            Log.i(TAG, "------->" + uri.getPath());
//        }
//        super.onActivityResult(requestCode, resultCode, data);
//    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right);
    }
}

























