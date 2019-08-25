/* Created by Vickers Jian on 2019/07 */
package com.vickers.ebook_reader.utils;

import android.os.Environment;
import android.util.Log;

import com.vickers.ebook_reader.MyApplication;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;
import java.util.Objects;

public class FileUtils {
    public static final String SUFFIX_EPUB = ".epub";

    /**
     * 判断是否挂载了SD卡
     *
     * @return boolean
     */
    public static boolean isSdCardExist() {
        return Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState());
    }

    /**
     * 获取Cache文件夹
     *
     * @return Cache文件夹路径
     */
    public static String getCachePath() {
        if (isSdCardExist()) {
            try {
                return Objects.requireNonNull(MyApplication.getInstance()
                        .getExternalCacheDir())
                        .getAbsolutePath();
            } catch (Exception ignored) {
            }
        }
        return MyApplication.getInstance()
                .getCacheDir()
                .getAbsolutePath();
    }

    /**
     * 创建文件
     *
     * @param path 需要创建的文件路径
     * @return boolean
     */
    public static boolean createFolderIfNotExists(String path) {
        File folder = new File(path);
        if (!folder.exists()) {
            return folder.mkdirs();
        }
        return false;
    }

    public static boolean isFileExist(String filePath){
        File file=new File(filePath);
        return file.exists();
    }

    /**
     * 得到图片字节流数组大小
     */
    public static byte[] readStream(InputStream inStream) throws Exception {
        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int len = 0;
        while ((len = inStream.read(buffer)) != -1) {
            outStream.write(buffer, 0, len);
        }
        outStream.close();
        inStream.close();
        return outStream.toByteArray();
    }
}
