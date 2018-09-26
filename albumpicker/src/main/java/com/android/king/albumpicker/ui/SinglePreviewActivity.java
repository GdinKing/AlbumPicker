package com.android.king.albumpicker.ui;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.support.v4.content.FileProvider;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.king.albumpicker.R;
import com.android.king.albumpicker.entity.Photo;
import com.android.king.albumpicker.util.ImageUtil;
import com.android.king.albumpicker.util.MediaManager;
import com.android.king.albumpicker.view.TouchImageView;

import java.io.File;


/**
 * 单个图片预览
 *
 * @author king
 * @since 2018/03/02
 */
public class SinglePreviewActivity extends BaseActivity implements View.OnClickListener {

    public static final String KEY_SELECT_PHOTO = "photo";

    private TouchImageView ivShow;
    private Photo selectPhoto;
    private ImageView ivVideo;

    @Override
    protected int getLayout() {
        return R.layout.album_activity_single_preview;
    }

    @Override
    protected void initView() {
        tvRight.setOnClickListener(this);
        ivBack.setOnClickListener(this);
        ivShow = findViewById(R.id.iv_show);
        ivVideo = findViewById(R.id.iv_play);
        ivVideo.setOnClickListener(this);
    }

    @Override
    protected void initData() {
        selectPhoto = (Photo) getIntent().getSerializableExtra(KEY_SELECT_PHOTO);
        if (selectPhoto == null) {
            Toast.makeText(this, "图片出错", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        if (selectPhoto.getMimeType().contains("video")) {
            ivVideo.setVisibility(View.VISIBLE);
        } else {
            ivVideo.setVisibility(View.GONE);
        }
        ImageUtil.showImage(this, selectPhoto.getPath(), ivShow);
    }


    @Override
    public void onClick(View view) {
        int i = view.getId();
        if (i == R.id.iv_back) {  //返回
            finish();

        } else if (i == R.id.tv_right) { //确定
            MediaManager.getInstance().addMedia(selectPhoto.getId(), selectPhoto);
            setResult(RESULT_OK);
            finish();

        } else if (i == R.id.iv_play) {  //播放视频
            ivVideo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        String path = selectPhoto.getPath();
                        if (path.startsWith("file:///")) {
                            path = path.substring(7);
                        }
                        Intent intent = new Intent(Intent.ACTION_VIEW);
                        Uri uri = null;
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                            intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                            uri = FileProvider.getUriForFile(SinglePreviewActivity.this, getApplicationContext().getPackageName() + ".provider", new File(path));
                        } else {
                            uri = Uri.fromFile(new File(path));
                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        }
                        intent.setDataAndType(uri, "video/*");
                        startActivity(intent);
                    } catch (Exception e) {
                        Toast.makeText(SinglePreviewActivity.this, "视频播放出错", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

}
