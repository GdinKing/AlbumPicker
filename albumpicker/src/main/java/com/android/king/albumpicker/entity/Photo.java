package com.android.king.albumpicker.entity;

import java.io.Serializable;

/**
 * 图片/视频 实体
 *
 * @author king
 * @since 2018/01/09
 */
public class Photo implements Serializable {

    private int id;
    private String path;
    private String mimeType;
    private long duration;
    private int width, height;
    private long size;
    private long addDate;

    /**
     * 是否使用原图
     */
    private boolean fullImage;

    public Photo(int id, String path, String mimeType, int width, int height, long size) {
        this.id = id;
        this.path = path;
        this.mimeType = mimeType;
        this.width = width;
        this.height = height;
        this.size = size;
    }


    public Photo() {
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Photo)) return false;

        Photo photo = (Photo) o;

        return id == photo.id;
    }

    public long getAddDate() {
        return addDate;
    }

    public void setAddDate(long addDate) {
        this.addDate = addDate;
    }

    public boolean isFullImage() {
        return fullImage;
    }

    public void setFullImage(boolean fullImage) {
        this.fullImage = fullImage;
    }

    public String getMimeType() {
        return mimeType;
    }

    public void setMimeType(String mimeType) {
        this.mimeType = mimeType;
    }

    @Override
    public int hashCode() {
        return id;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

}
