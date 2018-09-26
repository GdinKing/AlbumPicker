package com.android.king.albumpicker;

import android.content.Context;
import android.content.Intent;

import com.android.king.albumpicker.ui.AlbumPickerActivity;
import com.android.king.albumpicker.util.ImageLoader;

/***
 * 图片选择器帮助类
 * @since 2018-09-26
 * @author king
 */
public class AlbumPicker {
    private static AlbumPicker mInstance;
    private Context context;
    private int type;
    private int max;
    private int mode;
    private int width;
    private int height;
    private ImageLoader imageLoader;

    private AlbumPicker(Context context) {
        this.context = context;
    }

    public static AlbumPicker getInstance(Context context) {
        if (mInstance == null) {
            synchronized (AlbumPicker.class) {
                if (mInstance == null) {
                    mInstance = new AlbumPicker(context);
                }
            }
        }
        return mInstance;
    }


    /**
     * 设置图片加载器
     *
     * @param imageLoader
     * @return
     */
    public AlbumPicker setImageLoader(ImageLoader imageLoader) {
        this.imageLoader = imageLoader;
        return this;
    }

    public ImageLoader getImageLoader() {
        return imageLoader;
    }

    /**
     * 选择类型
     *
     * @param type
     */
    public AlbumPicker setSelectType(int type) {
        this.type = type;
        return this;
    }

    /**
     * 最大选择数
     *
     * @param max
     */
    public AlbumPicker setMax(int max) {
        this.max = max;
        return this;
    }

    /**
     * 单选/多选 模式
     *
     * @param mode
     */
    public AlbumPicker setMode(int mode) {
        this.mode = mode;
        return this;
    }

    /**
     * 压缩宽度像素限定
     *
     * @param width
     */
    public AlbumPicker setWidthLimit(int width) {
        this.width = width;
        return this;
    }

    /**
     * 压缩高度像素限定
     *
     * @param height
     */
    public AlbumPicker setHeightLimit(int height) {
        this.height = height;
        return this;
    }

    public Intent getIntent() {
        Intent intent = new Intent(context, AlbumPickerActivity.class);
        intent.putExtra(AlbumPickerActivity.KEY_SELECT_TYPE, type);//选择类型 TYPE_ALL:图片和视频 TYPE_IMAGE:图片 TYPE_VIDEO:视频
        intent.putExtra(AlbumPickerActivity.KEY_MAX_SELECT, max); //最大选择数
        intent.putExtra(AlbumPickerActivity.KEY_SELECT_MODE, mode);//MODE_MULTI：多选模式  MODE_SINGLE：单选模式
        intent.putExtra(AlbumPickerActivity.KEY_REQ_WIDTH, width);//压缩宽度限定，默认720
        intent.putExtra(AlbumPickerActivity.KEY_REQ_HEIGHT, height);//压缩高度限定，默认1280
        return intent;
    }

}
