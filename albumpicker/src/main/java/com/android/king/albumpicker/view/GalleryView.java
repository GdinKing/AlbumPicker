package com.android.king.albumpicker.view;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.android.king.albumpicker.R;
import com.android.king.albumpicker.entity.SelectedImage;
import com.android.king.albumpicker.util.DisplayUtil;
import com.android.king.albumpicker.util.ImageUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * 已选图片展示器
 *
 * @author king
 * @since 2018/3/2
 */
public class GalleryView extends HorizontalScrollView {

    private Context mContext;
    private LinearLayout layoutContainer;
    private OnImageClickListener imageClickListener;
    private SquareImageView lastSelected;
    private List<SelectedImage> selectList;

    public GalleryView(Context context) {
        this(context, null);
    }

    public GalleryView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public GalleryView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mContext = context;
        init();
    }


    private void init() {
        layoutContainer = new LinearLayout(mContext);
        layoutContainer.setLayoutParams(new ViewGroup.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
        layoutContainer.setOrientation(LinearLayout.HORIZONTAL);
        layoutContainer.setGravity(Gravity.CENTER_VERTICAL);
        addView(layoutContainer);
        selectList = new ArrayList<SelectedImage>();
    }

    /**
     * 已选列表是否包含了Image
     *
     * @param image
     * @return
     */
    private boolean isContainsImage(SelectedImage image) {
        if (selectList == null) {
            return false;
        }
        boolean result = false;
        for (SelectedImage selectedImage : selectList) {
            if (selectedImage.getPhoto().getId() == image.getPhoto().getId()) {
                result = true;
                break;
            }
        }
        return result;
    }

    /**
     * 添加Image
     *
     * @param image
     */
    public void addImage(final SelectedImage image) {
        if (isContainsImage(image)) {
            return;
        }
        SquareImageView iv = new SquareImageView(mContext);
        float size = getResources().getDimension(R.dimen.album_gallery_image_size);
        int leftPadding = DisplayUtil.dip2px(mContext, 5);
        iv.setScaleType(ImageView.ScaleType.CENTER_CROP);
        ImageUtil.showImage(mContext, image.getPhoto().getPath(), iv);
        iv.setTag(R.id.layout_gallery, image);
        iv.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (imageClickListener != null) {
                    imageClickListener.onImageClicked(image);
                }
            }
        });
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams((int) size, (int) size);
        lp.setMargins(leftPadding, 0, leftPadding, 0);
        iv.setLayoutParams(lp);
        layoutContainer.addView(iv);
        selectList.add(image);
    }

    /**
     * 设置当前选中的Image边框
     *
     * @param photoId
     */
    public void setImageBorder(int photoId) {
        if (layoutContainer == null) {
            return;
        }
        for (int i = 0; i < layoutContainer.getChildCount(); i++) {
            SquareImageView iv = (SquareImageView) layoutContainer.getChildAt(i);
            SelectedImage image = (SelectedImage) iv.getTag(R.id.layout_gallery);

            if (image.getPhoto().getId() == photoId) {
                if (lastSelected != null) {
                    lastSelected.setShowBorder(false);
                }
                iv.setShowBorder(true);
                lastSelected = iv;
            } else {
                iv.setShowBorder(false);
            }
        }
    }

    /**
     * 选择/取消选择
     */
    public void toggleSelect(int photoId, boolean isSelected) {
        if (layoutContainer == null) {
            return;
        }
        for (int i = 0; i < layoutContainer.getChildCount(); i++) {
            SquareImageView iv = (SquareImageView) layoutContainer.getChildAt(i);
            SelectedImage image = (SelectedImage) iv.getTag(R.id.layout_gallery);
            if (image.getPhoto().getId() == photoId) {
                iv.setShade(new ColorDrawable(Color.parseColor("#92ffffff")));
                iv.setShowShade(!isSelected);
            }
        }

    }

    /**
     * 移除所有图片
     */
    public void removeAllImage() {
        if (layoutContainer == null) {
            return;
        }
        layoutContainer.removeAllViews();
        if (selectList != null) {
            selectList.clear();
        }
    }

    /**
     * GalleryView里面的图片点击事件
     */
    public interface OnImageClickListener {
        void onImageClicked(SelectedImage image);
    }

    public void setImageClickListener(OnImageClickListener imageClickListener) {
        this.imageClickListener = imageClickListener;
    }
}
