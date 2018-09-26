package com.android.king.albumpicker.util;

import android.content.Context;
import android.widget.ImageView;

import com.android.king.albumpicker.AlbumPicker;


/***
 * 图片操作工具
 *
 * @since 2018/1/9
 * @author king
 */

public class ImageUtil {

    /**
     * 展示图片
     *
     * @param context
     * @param path
     * @param imageView
     */
    public static void showImage(Context context, String path, ImageView imageView) {

        ImageLoader loader = AlbumPicker.getInstance(context).getImageLoader();
        if(loader!=null){
            loader.showImage(context,path,imageView);
        }
    }
}
