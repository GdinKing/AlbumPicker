package com.android.king.albumpicker.ui;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.PermissionChecker;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewStub;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.GridView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.android.king.albumpicker.R;
import com.android.king.albumpicker.adapter.AlbumAdapter;
import com.android.king.albumpicker.adapter.PopupDirectoryListAdapter;
import com.android.king.albumpicker.entity.Photo;
import com.android.king.albumpicker.entity.PhotoDirectory;
import com.android.king.albumpicker.util.AlbumConstant;
import com.android.king.albumpicker.util.CompressHelper;
import com.android.king.albumpicker.util.DisplayUtil;
import com.android.king.albumpicker.util.FileUtil;
import com.android.king.albumpicker.util.MediaManager;
import com.android.king.albumpicker.util.MediaStoreHelper;
import com.android.king.albumpicker.view.PopupWindowMenu;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 图片选择器
 *
 * @author king
 * @since 2017/12/01
 */
public class AlbumPickerActivity extends BaseActivity implements View.OnClickListener, AdapterView.OnItemClickListener, MediaManager.OnCheckChangeListener {

    public static final int REQUEST_GET_PHOTO = 0x1001;
    public static final int REQUEST_COMPRESS_IMAGE = 0x1002;
    public static final int REQUEST_PREVIEW = 0x1003;

    public static final String KEY_MAX_SELECT = "max";
    public static final String KEY_SELECT_TYPE = "type";
    public static final String KEY_SELECT_MODE = "mode";
    public static final String KEY_REQ_WIDTH = "width";
    public static final String KEY_REQ_HEIGHT = "height";


    private CheckBox rbOriginal;
    private GridView albumGridView;

    private TextView tvDirectory;
    private TextView tvPreview;

    private AlbumAdapter albumAdapter;

    //选择类型
    private int type = AlbumConstant.TYPE_ALL;
    //选择模式
    private int mode = AlbumConstant.MODE_MULTI;

    private PopupWindowMenu menuWindow;

    private int reqWidth;
    private int reqHeight;
    private boolean isPermissionOk;

    @Override
    protected int getLayout() {
        return R.layout.album_activity_picker;
    }

    @Override
    protected void initView() {
        tvDirectory = findViewById(R.id.tv_directory);
        tvPreview = findViewById(R.id.tv_preview);
        albumGridView = findViewById(R.id.gv_album);
        tvDirectory.setOnClickListener(this);
        tvPreview.setOnClickListener(this);
        tvRight.setOnClickListener(this);
        ivBack.setOnClickListener(this);
        rbOriginal = findViewById(R.id.rb_original);

        rbOriginal.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                MediaManager.getInstance().setOriginal(isChecked);
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        rbOriginal.setChecked(MediaManager.getInstance().isOriginal());
        if (isPermissionOk) {
            MediaStoreHelper.getPhotoData(this, type, mHandler);
        }
    }

    @Override
    protected void initData() {

        Intent intent = getIntent();
        int maxMedia = intent.getIntExtra(KEY_MAX_SELECT, AlbumConstant.DEFAULT_MAX_SELECT);
        type = intent.getIntExtra(KEY_SELECT_TYPE, AlbumConstant.TYPE_ALL);
        mode = intent.getIntExtra(KEY_SELECT_MODE, AlbumConstant.MODE_MULTI);
        reqWidth = intent.getIntExtra(KEY_REQ_WIDTH, 720);
        reqHeight = intent.getIntExtra(KEY_REQ_HEIGHT, 1280);
        if (type == AlbumConstant.TYPE_VIDEO) {
            rbOriginal.setVisibility(View.GONE);
        }
        MediaManager.getInstance().init();
        MediaManager.getInstance().setMaxMediaSum(maxMedia);
        if (type == AlbumConstant.TYPE_IMAGE) {
            tvDirectory.setText(getString(R.string.last_image));
            tvLeft.setText(getString(R.string.last_image));
        } else if (type == AlbumConstant.TYPE_VIDEO) {
            tvDirectory.setText(getString(R.string.videos));
            tvLeft.setText(getString(R.string.videos));
        } else {
            tvDirectory.setText(getString(R.string.image_video));
            tvLeft.setText(getString(R.string.image_video));
        }
        tvRight.setEnabled(false);

        if (PermissionChecker.checkSelfPermission(this,
                Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

            isPermissionOk = false;
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 100);
        } else {
            isPermissionOk = true;
        }
    }

    /**
     * 加载图片
     */
    private void loadData() {
        if (MediaManager.getInstance().getDirectoryList().isEmpty() || MediaManager.getInstance().getSelectDirectory().getPhotos().isEmpty()) {
            Toast.makeText(this, "暂无图片和视频", Toast.LENGTH_SHORT).show();
            ViewStub emptyStub = findViewById(R.id.stub_empty);
            emptyStub.inflate();
            return;
        }
        List<Photo> photoList = MediaManager.getInstance().getSelectDirectory().getPhotos();
        if (albumAdapter == null) {
            albumAdapter = new AlbumAdapter(this, photoList, type, mode);
            albumGridView.setAdapter(albumAdapter);
            albumGridView.setOnItemClickListener(this);
            MediaManager.getInstance().addOnCheckChangeListener(this);
        } else {
            albumAdapter.refreshData(photoList);
        }
    }

    /**
     * 弹出目录
     */
    private void showDirectory() {
        if (menuWindow == null) {
            menuWindow = new PopupWindowMenu(this, new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    loadData();
                    tvDirectory.setText(MediaManager.getInstance().getDirectoryList().get(position).getName());
                    tvLeft.setText(MediaManager.getInstance().getDirectoryList().get(position).getName());
                }
            });

            PopupDirectoryListAdapter popupDirectoryListAdapter = new PopupDirectoryListAdapter(this, MediaManager.getInstance().getDirectoryList());
            menuWindow.setAdapter(popupDirectoryListAdapter);
            int windowHeight = 3 * getResources().getDimensionPixelOffset(R.dimen.album_directory_item_height) + DisplayUtil.dip2px(this, 10);
            menuWindow.setHeight(windowHeight);
        }
        int barHeight = getResources().getDimensionPixelOffset(R.dimen.album_toolbar_height);
        if (!menuWindow.isShowing()) {
            menuWindow.showAtLocation(findViewById(R.id.bottom), Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, barHeight);
        }

    }

    @Override
    public void onClick(View view) {
        int i1 = view.getId();
        if (i1 == R.id.iv_back) {  //返回
            setResult(RESULT_CANCELED);
            finish();

        } else if (i1 == R.id.tv_directory) { //弹出目录
            showDirectory();

        } else if (i1 == R.id.tv_preview) { //预览
            Intent intent = new Intent(this, PreviewActivity.class);
            intent.putExtra(PreviewActivity.KEY_SELECT_TYPE, type);
            startActivityForResult(intent, REQUEST_PREVIEW);

        } else if (i1 == R.id.tv_right) {//确定
            if (!MediaManager.getInstance().isOriginal()) {
                compressImage();
            } else {
                Intent i = new Intent();
                i.putExtra(AlbumConstant.RESULT_KEY_PATH_LIST, MediaManager.getInstance().getSelected());
                setResult(RESULT_OK, i);
                finish();
            }
        }
    }

    /**
     * 压缩图片
     */
    private void compressImage() {
        showProgress("处理中");
        new Thread() {
            @Override
            public void run() {
                List<String> pathList = MediaManager.getInstance().getSelected();
                List<String> result = new ArrayList<String>();
                CompressHelper helper = new CompressHelper();
                for (String path : pathList) {
                    if (path.endsWith(".jpg") || path.endsWith(".png") || path.endsWith(".jpeg")) {
                        File f = helper.compressImage(AlbumPickerActivity.this, path, reqWidth, reqHeight);
                        if (f != null) {
                            result.add(f.getAbsolutePath());
                        } else {
                            result.add(path);
                        }
                    } else {
                        result.add(path);
                    }
                }
                mHandler.obtainMessage(REQUEST_COMPRESS_IMAGE, result).sendToTarget();
            }
        }.start();
    }

    /**
     * 图片/视频加载完毕的回调
     */
    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {

            if (msg.what == REQUEST_GET_PHOTO) {
                List<PhotoDirectory> dirs = (List<PhotoDirectory>) msg.obj;
                if (dirs != null && dirs.size() > 0) {
                    dirs.get(0).setSelected(true);
                }
                MediaManager.getInstance().setDirectoryList(dirs);
                loadData();
            } else if (msg.what == REQUEST_COMPRESS_IMAGE) {
                hideProgress();
                ArrayList<String> resultList = (ArrayList<String>) msg.obj;
                Intent i = new Intent();
                i.putExtra(AlbumConstant.RESULT_KEY_PATH_LIST, resultList);
                setResult(RESULT_OK, i);
                finish();
            }
        }
    };

    /**
     * 跳转至预览界面
     *
     * @param adapterView
     * @param view
     * @param position
     * @param l
     */
    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
        if (albumAdapter.getMode() == AlbumConstant.MODE_SINGLE) {  //单选模式
            Photo photo = (Photo) albumAdapter.getItem(position);
            Intent intent = new Intent(this, SinglePreviewActivity.class);
            intent.putExtra(SinglePreviewActivity.KEY_SELECT_PHOTO, photo);
            startActivityForResult(intent, REQUEST_PREVIEW);
        } else {  //多选模式
            Intent intent = new Intent(this, PreviewActivity.class);
            intent.putExtra(PreviewActivity.KEY_SELECT_INDEX, position);
            intent.putExtra(PreviewActivity.KEY_CURRENT_DIR, MediaManager.getInstance().getSelectIndex());
            intent.putExtra(PreviewActivity.KEY_SELECT_TYPE, type);
            startActivityForResult(intent, REQUEST_PREVIEW);
        }

    }

    /**
     * 选择图片的监听
     *
     * @param checks
     * @param changedId
     */
    @Override
    public void onCheckedChanged(Map<Integer, Photo> checks, int changedId) {
        final int checkSize = checks.size();
        tvRight.setEnabled(checkSize != 0);
        tvRight.setText(checkSize == 0 ? getString(R.string.btn_text_ok) : String.format(getString(R.string.btn_text_ok_count), checkSize, MediaManager.getInstance().getMaxMediaSum()));

        tvPreview.setEnabled(checkSize != 0);
        int color = checkSize != 0 ? Color.WHITE : ContextCompat.getColor(this, R.color.album_gray);
        tvPreview.setTextColor(color);
        tvPreview.setText(checkSize == 0 ? getString(R.string.preview) : String.format(getString(R.string.preview_count), checkSize));
    }

    /**
     * 选择后的结果回调
     *
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == REQUEST_PREVIEW) {
            if (!MediaManager.getInstance().isOriginal()) {
                compressImage();
            } else {
                Intent i = new Intent();
                i.putExtra(AlbumConstant.RESULT_KEY_PATH_LIST, MediaManager.getInstance().getSelected());
                setResult(RESULT_OK, i);
                finish();
            }
        }
    }

    @Override
    protected void onDestroy() {
        //移除监听
        MediaManager.getInstance().removeOnCheckChangeListener(this);
        MediaManager.getInstance().clear();
        if (mHandler != null) {
            mHandler.removeCallbacksAndMessages(null);
        }
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        if (menuWindow != null && menuWindow.isShowing()) {
            menuWindow.hide();
            return;
        }
        setResult(RESULT_CANCELED);
        finish();
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == 100) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {//获得了权限
                isPermissionOk = true;
                MediaStoreHelper.getPhotoData(this, type, mHandler);
            } else {
                finish();
            }
        }
    }


}
