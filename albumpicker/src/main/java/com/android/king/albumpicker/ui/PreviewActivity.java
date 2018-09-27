package com.android.king.albumpicker.ui;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.support.v4.content.FileProvider;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.AppCompatCheckBox;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.Toast;

import com.android.king.albumpicker.R;
import com.android.king.albumpicker.entity.Photo;
import com.android.king.albumpicker.entity.SelectedImage;
import com.android.king.albumpicker.util.AlbumConstant;
import com.android.king.albumpicker.util.ImageUtil;
import com.android.king.albumpicker.util.MediaManager;
import com.android.king.albumpicker.view.GalleryView;
import com.android.king.albumpicker.view.TouchImageView;

import java.io.File;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Queue;


/**
 * 多张图片预览
 *
 * @author king
 * @since 2018/01/10
 */
public class PreviewActivity extends BaseActivity implements MediaManager.OnCheckChangeListener, View.OnClickListener {

    public static final String KEY_SELECT_INDEX = "index";
    public static final String KEY_CURRENT_DIR = "dir";
    public static final String KEY_SELECT_TYPE = "type";

    private CheckBox rbOriginal;
    private AppCompatCheckBox cbSelect;
    private ViewPager viewPager;
    private ImageAdapter imageAdapter;
    private View bottomBar, actionBar;

    private List<Photo> selectPhotoList;

    private GalleryView galleryView;
    private int dirIndex;
    private int selectType;

    @Override
    protected int getLayout() {
        return R.layout.album_activity_preview;
    }

    @Override
    protected void initView() {
        actionBar = findViewById(R.id.actionbar);
        bottomBar = findViewById(R.id.bottom);
        tvRight.setOnClickListener(this);
        ivBack.setOnClickListener(this);
        cbSelect = findViewById(R.id.cb_pre);
        rbOriginal = findViewById(R.id.rb_original);
        viewPager = findViewById(R.id.view_pager);
        galleryView = findViewById(R.id.layout_gallery);
        rbOriginal.setChecked(MediaManager.getInstance().isOriginal());
        rbOriginal.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                MediaManager.getInstance().setOriginal(isChecked);
            }
        });
    }

    @Override
    protected void initData() {
        int index = getIntent().getIntExtra(KEY_SELECT_INDEX, 0);
        dirIndex = getIntent().getIntExtra(KEY_CURRENT_DIR, -1);
        selectType = getIntent().getIntExtra(KEY_SELECT_TYPE, AlbumConstant.TYPE_ALL);
        if (selectType == AlbumConstant.TYPE_VIDEO) {
            rbOriginal.setVisibility(View.GONE);
        }
        if (dirIndex == -1) {//从预览按钮点击进入
            selectPhotoList = new ArrayList<>();
            for (Integer integer : MediaManager.getInstance().getCheckPhotos().keySet()) {
                Photo photo = MediaManager.getInstance().getCheckPhotos().get(integer);
                selectPhotoList.add(photo);
            }
            initGallery();
        } else {//从图片点击进入
            selectPhotoList = MediaManager.getInstance().getDirectoryList().get(dirIndex).getPhotos();
            initGallery();
        }
        cbSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Photo photo = imageAdapter.getItem(viewPager.getCurrentItem());
                boolean isChecked = cbSelect.isChecked();
                if (isChecked) {
                    boolean result = MediaManager.getInstance().addMedia(photo.getId(), photo);
                    if (!result) {
                        cbSelect.setChecked(false);
                        Toast.makeText(getBaseContext(), String.format(getString(R.string.select_max_sum), MediaManager.getInstance().getMaxMediaSum()), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    MediaManager.getInstance().removeMedia(photo.getId());
                }
                initGallery();
                galleryView.toggleSelect(photo.getId(), isChecked);
            }
        });
        tvTitle.setText((index + 1) + "/" + selectPhotoList.size());
        imageAdapter = new ImageAdapter(this, selectPhotoList);
        viewPager.setAdapter(imageAdapter);
        viewPager.setCurrentItem(index);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset,
                                       int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                updateUi(position);
                TouchImageView lastImageView = viewPager.findViewById(R.id.iv_touch);
                if (lastImageView != null && lastImageView.isZoomed()) {
                    lastImageView.resetZoom();
                }

            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
        MediaManager.getInstance().addOnCheckChangeListener(this);
        onCheckedChanged(MediaManager.getInstance().getCheckPhotos(), 0);
        updateUi(viewPager.getCurrentItem());
    }

    /**
     * 初始化Gallery
     */
    private void initGallery() {
        if (selectPhotoList == null || selectPhotoList.size() == 0) {
            return;
        }
        if (MediaManager.getInstance().getCheckPhotos().isEmpty()) {
            return;
        }
        if (dirIndex != -1) {
            galleryView.removeAllImage();
        }
        galleryView.setVisibility(View.VISIBLE);
        for (int i = 0; i < selectPhotoList.size(); i++) {
            Photo photo = selectPhotoList.get(i);
            if (MediaManager.getInstance().existPhoto(photo.getId())) {
                galleryView.addImage(new SelectedImage(i, photo));
            }
        }
        galleryView.setImageClickListener(new GalleryView.OnImageClickListener() {
            @Override
            public void onImageClicked(SelectedImage image) {
                viewPager.setCurrentItem(image.getIndex(), false);
            }
        });
    }

    /**
     * 更新界面展示内容
     *
     * @param position
     */
    private void updateUi(int position) {
        tvTitle.setText((position + 1) + "/" + imageAdapter.getCount());
        Photo photo = imageAdapter.getItem(position);
        cbSelect.setChecked(MediaManager.getInstance().existPhoto(photo.getId()));
        galleryView.toggleSelect(photo.getId(), cbSelect.isChecked());
        galleryView.setImageBorder(photo.getId());
    }

    /**
     * 切换全屏显示图片
     */
    public void switchOverlay() {
        if (actionBar.getVisibility() == View.VISIBLE) {
            actionBar.setVisibility(View.GONE);
            bottomBar.setVisibility(View.GONE);
        } else {
            actionBar.setVisibility(View.VISIBLE);
            bottomBar.setVisibility(View.VISIBLE);
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        imageAdapter.clearCache();
        MediaManager.getInstance().removeOnCheckChangeListener(this);
    }

    @Override
    public void onCheckedChanged(Map<Integer, Photo> checkStatus, int changedId) {
        tvRight.setEnabled(!checkStatus.isEmpty());
        tvRight.setText(checkStatus.isEmpty() ? getString(R.string.btn_text_ok) : String.format(getString(R.string.btn_text_ok_count), checkStatus.size(), MediaManager.getInstance().getMaxMediaSum()));
    }

    @Override
    public void onClick(View view) {
        int i = view.getId();
        if (i == R.id.iv_back) {
            finish();

        } else if (i == R.id.tv_right) {
            setResult(RESULT_OK);
            finish();

        }
    }

    /**
     * ViewHolder
     */
    public class PagerHolder {
        public View itemView;
        public TouchImageView touchImageView;
        public ImageView ivVideoPlay;
        public int viewType;
        private String url = "";

        public PagerHolder(View itemView) {
            this.itemView = itemView;
            touchImageView = itemView.findViewById(R.id.iv_touch);
            ivVideoPlay = itemView.findViewById(R.id.iv_play);
            touchImageView.setMaxZoom(6);
            touchImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    switchOverlay();
                }
            });

            if (ivVideoPlay != null) {
                ivVideoPlay.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        try {
                            String path = url;
                            if (url.startsWith("file:///")) {
                                path = url.substring(7);
                            }
                            Intent intent = new Intent(Intent.ACTION_VIEW);
                            Uri uri = null;
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                                intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                                uri = FileProvider.getUriForFile(PreviewActivity.this, getApplicationContext().getPackageName() + ".provider", new File(path));
                            } else {
                                uri = Uri.fromFile(new File(path));
                                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            }
                            intent.setDataAndType(uri, "video/*");
                            startActivity(intent);
                        } catch (Exception e) {
                            Toast.makeText(PreviewActivity.this, "视频播放出错", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        }

        public void bindData(Photo photo) {
            url = "file:///" + photo.getPath();
            ImageUtil.showImage(PreviewActivity.this, url, touchImageView);
        }
    }

    /**
     * 预览的ViewPager适配器
     */
    public class ImageAdapter extends PagerAdapter {
        List<Photo> images;
        LayoutInflater layoutInflater;

        public ImageAdapter(Context context, List<Photo> images) {
            this.images = images;
            layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }


        public PagerHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            switch (viewType) {
                case 1:
                    PagerHolder pagerHolder = new PagerHolder(layoutInflater.inflate(R.layout.album_item_preview_video, parent, false));
                    pagerHolder.viewType = viewType;
                    return pagerHolder;
                case 2:
                    pagerHolder = new PagerHolder(layoutInflater.inflate(R.layout.album_item_preview_image, parent, false));
                    pagerHolder.viewType = viewType;
                    return pagerHolder;
            }
            return null;
        }

        public void onBindViewHolder(PagerHolder holder, int position) {
            holder.bindData(images.get(position));
        }

        public int getItemViewType(int position) {
            return getItem(position).getMimeType().contains("video") ? 1 : 2;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
            PagerHolder pagerHolder = (PagerHolder) ((View) object).getTag(R.id.view_pager);
            if (pagerHolder != null) caches.get(getItemViewType(position)).offer(pagerHolder);
        }

        public Photo getItem(int position) {
            if (images == null) {
                return null;
            }
            return images.get(position);
        }

        @Override
        public int getCount() {
            if (images == null) {
                return 0;
            }
            return images.size();
        }

        SparseArray<Queue<PagerHolder>> caches = new SparseArray<>();

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            int viewType = getItemViewType(position);
            Queue<PagerHolder> pagerHolderList = caches.get(viewType);
            if (pagerHolderList == null) {
                pagerHolderList = new ArrayDeque<>();
                caches.put(viewType, pagerHolderList);
            }
            PagerHolder pagerHolder = pagerHolderList.poll();
            if (pagerHolder == null) {
                pagerHolder = onCreateViewHolder(container, viewType);
            }
            container.addView(pagerHolder.itemView);
            onBindViewHolder(pagerHolder, position);
            pagerHolder.itemView.setTag(R.id.view_pager, pagerHolder);
            return pagerHolder.itemView;
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        public void clearCache() {
            caches.clear();
        }
    }

}
