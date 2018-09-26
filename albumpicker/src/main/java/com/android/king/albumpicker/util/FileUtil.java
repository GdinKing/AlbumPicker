package com.android.king.albumpicker.util;

import android.content.Context;
import android.text.TextUtils;

import java.io.File;

/***
 * 文件操作工具类
 *
 * @since 2018-09-25
 * @author king
 */
public class FileUtil {
    /**
     * 获取文件扩展名
     *
     * @param fileName
     * @return
     */
    public static String getSuffix(String fileName) {
        if (TextUtils.isEmpty(fileName)) {
            return "";
        }
        String suffix = "";
        if (!fileName.contains(".")) {
            suffix = "";
        } else {
            suffix = fileName.substring(fileName.lastIndexOf(".") + 1, fileName.length()).toLowerCase();
        }
        return suffix;

    }

    /**
     * 获取图片大小
     *
     * @param imgPath 图片路径
     * @return 图片大小,单位：b
     */
    public static long getImageSize(String imgPath) {
        File file = new File(imgPath);
        if (file.exists()) {
            return file.length();
        }
        return 0;
    }
    /**
     * 获取裁剪后的图片保存路径
     */
    public static File getCropSavePath(Context context) {
        File cropCacheFolder = new File(context.getExternalCacheDir() + "/image/crop/");
        if (!cropCacheFolder.exists()) {
            cropCacheFolder.mkdirs();
        }
        return cropCacheFolder;
    }

    /**
     * 获取旋转后的图片保存路径
     */
    public static File getRotateSavePath(Context context) {
        File cropCacheFolder = new File(context.getExternalCacheDir() + "/image/rotate/");
        if (!cropCacheFolder.exists()) {
            cropCacheFolder.mkdirs();
        }
        return cropCacheFolder;
    }

}
