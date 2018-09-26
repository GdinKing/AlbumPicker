package com.android.king.albumpicker.util;

import android.content.Context;
import android.widget.ImageView;

/***
 * 图片加载器接口
 * @since 2018-09-26
 * @author king
 */
public interface ImageLoader {

    public void showImage(Context context, String path, ImageView imageView);

}
