package com.android.king.albumpicker.util;

import android.content.Context;

import java.io.File;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/***
 * 缓存控制
 *
 * @since 2018/2/8
 * @author king
 */

public class CacheUtil {
    /**
     * 视频保存路径
     *
     * @return
     */
    public static String generateVideoFile(Context context) {
        String childDir = "tmp" + File.separator + "videos";
        String fileName = "VID_" + System.currentTimeMillis() + ".mp4";

        File file = new File(context.getExternalCacheDir().getParentFile(), childDir);
        if (!file.exists()) {
            file.mkdirs();
        }
        return file.getAbsolutePath() + File.separator + fileName;
    }

    /**
     * 获取某个目录下所有文件的大小
     *
     * @param filePath
     * @return
     */
    public static long getFileSize(String filePath) {
        long size = 0;
        File file = new File(filePath);
        if (file.exists() && file.isDirectory()) {
            for (File f : file.listFiles()) {
                size += f.length();
            }
        }
        return size;
    }


    /**
     * 清理缓存
     *
     * @param filePath  缓存路径
     * @param cacheSize 允许缓存的大小
     */
    public static void clearCache(String filePath, long cacheSize) {
        try {
            File file = new File(filePath);
            if (file != null && file.exists()) {
                File[] files = file.listFiles();
                if (files != null) {
                    List<File> fileList = Arrays.asList(files);
                    Collections.sort(fileList, new Comparator<File>() {
                        @Override
                        public int compare(File file1, File file2) {
                            if (file1.lastModified() < file2.lastModified()) {
                                return -1;
                            } else {
                                return 1;
                            }
                        }
                    });

                    for (File f : fileList) {
                        long size = getFileSize(filePath);
                        if (size > cacheSize && f.exists() && f.isFile()) {
                            f.delete();
                        }
                    }
                }

            }
        } catch (Exception e) {

        }
    }

}
