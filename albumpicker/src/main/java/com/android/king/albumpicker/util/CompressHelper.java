package com.android.king.albumpicker.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;

/**
 * 图片压缩工具类
 *
 * @author king
 * @since 2018/3/29
 */

public class CompressHelper {

    public static final String FORMAT_JPG = "JPEG";
    public static final String FORMAT_PNG = "PNG";
    public static final String FORMAT_WEBP = "WEBP";



    /**
     * 压缩尺寸
     *
     * @param imgPath 源图路径
     * @param w  宽度限定
     * @param h  高度限定
     * @return 压缩后图片
     */
    public Bitmap compressBitmap(String imgPath, int w,int h) {
        BitmapFactory.Options opts = new BitmapFactory.Options();
        opts.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(imgPath, opts);
        opts.inSampleSize = calculateInSampleSize(opts, w, h);
        opts.inJustDecodeBounds = false;
        return BitmapFactory.decodeFile(imgPath, opts);
    }

    /**
     * 计算压缩比率
     */
    private int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;
        if (height > reqHeight || width > reqWidth) {
            final int heightRatio = Math.round((float) height / (float) reqHeight);
            final int widthRatio = Math.round((float) width / (float) reqWidth);
            inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
        }
        return inSampleSize;
    }

    /**
     * 旋转至正确的方向
     *
     * @param bitmap  源图
     * @param srcExif 图片参数
     * @return 旋转后的bitmap
     */
    public Bitmap rotatingImage(Bitmap bitmap, ExifInterface srcExif) {
        Matrix matrix = new Matrix();
        int angle = 0;
        int orientation = srcExif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
        switch (orientation) {
            case ExifInterface.ORIENTATION_ROTATE_90:
                angle = 90;
                break;
            case ExifInterface.ORIENTATION_ROTATE_180:
                angle = 180;
                break;
            case ExifInterface.ORIENTATION_ROTATE_270:
                angle = 270;
                break;
            default:
                break;
        }

        matrix.postRotate(angle);

        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
    }


    /**
     * 压缩尺寸
     *
     * @param srcFile 源文件路径
     * @param width   限定宽
     * @param height  限定高
     * @return 压缩后的图片文件
     */
    public File compressImage(Context context, String srcFile, int width, int height) {
        try {

            Bitmap tagBitmap = compressBitmap(srcFile, width, height);
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            tagBitmap = rotatingImage(tagBitmap, new ExifInterface(srcFile));
            String format = FORMAT_JPG;
            if (srcFile.toLowerCase().endsWith(".jpg") || srcFile.toLowerCase().endsWith(".jpeg")) {
                tagBitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
                tagBitmap.recycle();
                format = FORMAT_JPG;
            } else if (srcFile.toLowerCase().endsWith(".png")) {
                tagBitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
                tagBitmap.recycle();
                format = FORMAT_PNG;
            } else if (srcFile.toLowerCase().endsWith(".webp")) {
                tagBitmap.compress(Bitmap.CompressFormat.WEBP, 100, stream);
                tagBitmap.recycle();
                format = FORMAT_WEBP;
            }
            File saveFile = getCompressSavePath(context, format);
            FileOutputStream fos = new FileOutputStream(saveFile);
            fos.write(stream.toByteArray());
            fos.flush();
            fos.close();
            stream.close();
            return saveFile;
        } catch (Exception e) {
            return null;
        }
    }


    /**
     * 获取压缩图片保存路径
     *
     * @param format 图片格式
     * @return 图片保存文件
     */
    public File getCompressSavePath(Context context, String format) {

        String path = context.getExternalCacheDir().getAbsolutePath() + "/image/compress";
        String fileName = "";
        if (FORMAT_JPG.equals(format)) {
            fileName = System.currentTimeMillis() + ".jpg";
        } else if (FORMAT_PNG.equals(format)) {
            fileName = System.currentTimeMillis() + ".png";
        } else if (FORMAT_WEBP.equals(format)) {
            fileName = System.currentTimeMillis() + ".webp";
        }
        File parentFile = new File(path);
        if (!parentFile.exists()) {
            parentFile.mkdirs();
        }
        return new File(parentFile, fileName);
    }
}
