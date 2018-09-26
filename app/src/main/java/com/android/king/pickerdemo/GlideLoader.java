package com.android.king.pickerdemo;

import android.content.Context;
import android.widget.ImageView;

import com.android.king.albumpicker.util.ImageLoader;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

/***
 * 名称：
 * 描述：
 * 最近修改时间：2018年09月26日 16:17分
 * @since 2018-09-26
 * @author king
 */
public class GlideLoader implements ImageLoader {

    @Override
    public void showImage(Context context, String path, ImageView imageView) {

        RequestOptions options = new RequestOptions();
        options.placeholder(R.drawable.album_ic_placeholder);
        options.fitCenter();

        if (path.endsWith(".gif")) { //显示gif图

            Glide.with(context).asGif().load(path)
                    .apply(options)
                    .into(imageView);
        } else {
            Glide.with(context).load(path)
                    .apply(options)
                    .into(imageView);
        }
    }
}
