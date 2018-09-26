package com.android.king.albumpicker.entity;


/**
 * 记录选中的图片/视频
 *
 * @author king
 * @since 2018/03/02
 */
public class SelectedImage {

    private int index;
    private Photo photo;
    private boolean isSelected;

    public SelectedImage() {
    }

    public SelectedImage(int index, Photo photo) {
        this.index = index;
        this.photo = photo;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public Photo getPhoto() {
        return photo;
    }

    public void setPhoto(Photo photo) {
        this.photo = photo;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

}
