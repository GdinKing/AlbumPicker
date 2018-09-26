package com.android.king.albumpicker.util;


import com.android.king.albumpicker.entity.Photo;
import com.android.king.albumpicker.entity.PhotoDirectory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 选中图片缓存管理类
 *
 * @author king
 * @since 2018/01/10
 */
public class MediaManager {
    private static final MediaManager ourInstance = new MediaManager();

    public static MediaManager getInstance() {
        return ourInstance;
    }

    private MediaManager() {
    }

    /**
     * 保存选中的文件
     */
    private Map<Integer, Photo> checkPhotos;
    /**
     * 状态监听，区分开不同监听
     */
    private List<OnCheckChangeListener> onCheckChangeListeners;
    /**
     * 目录列表
     */
    private List<PhotoDirectory> directoryList;
    /**
     * 最大选择数
     */
    private int maxMediaSum;
    /**
     * 选中的目录的位置
     */
    private int selectIndex;

    /**
     * 是否使用原图
     */
    private boolean isOriginal;

    public void init() {
        checkPhotos = new HashMap<Integer, Photo>();
        onCheckChangeListeners = new ArrayList<OnCheckChangeListener>();
        directoryList = new ArrayList<PhotoDirectory>();
        maxMediaSum = Integer.MAX_VALUE;
        selectIndex = 0;
    }

    /**
     * 清除选中
     */
    public void clear() {
        if (checkPhotos != null) {
            checkPhotos.clear();
        }
        if (directoryList != null) {
            directoryList.clear();
        }
    }

    public int getMaxMediaSum() {
        return maxMediaSum;
    }

    public void setMaxMediaSum(int maxMediaSum) {
        this.maxMediaSum = maxMediaSum;
    }

    public void addOnCheckChangeListener(OnCheckChangeListener onCheckChangeListener) {
        if (onCheckChangeListener != null) {
            onCheckChangeListeners.add(onCheckChangeListener);
        }
    }

    public void removeOnCheckChangeListener(OnCheckChangeListener onCheckChangeListener) {
        if (onCheckChangeListener != null && onCheckChangeListeners != null) {
            if (onCheckChangeListeners.contains(onCheckChangeListener)) {
                onCheckChangeListeners.remove(onCheckChangeListener);
            }
        }
    }

    /**
     * 添加图片
     * 超过最大允许的数量就不能再添加了
     *
     * @param id
     * @param photo
     * @return
     */
    public boolean addMedia(int id, Photo photo) {
        if (checkPhotos.size() >= maxMediaSum) {
            return false;
        }
        if (!checkPhotos.containsKey(id)) {
            checkPhotos.put(id, photo);
            notifyDataChange(id);
        }
        return true;
    }

    /**
     * 移除图片
     *
     * @param id
     */
    public void removeMedia(int id) {
        if (checkPhotos.containsKey(id)) {
            checkPhotos.remove(id);
            notifyDataChange(id);
        }
    }


    public int getSelectIndex() {
        return selectIndex;
    }

    public void setSelectIndex(int selectIndex) {
        this.selectIndex = selectIndex;
    }

    /**
     * 已选的图片是否存在
     *
     * @param id
     * @return
     */
    public boolean existPhoto(int id) {
        if (checkPhotos == null) {
            return false;
        }
        return checkPhotos.containsKey(id);
    }

    public Map<Integer, Photo> getCheckPhotos() {
        return checkPhotos;
    }

    public List<PhotoDirectory> getDirectoryList() {
        return directoryList;
    }

    public void setDirectoryList(List<PhotoDirectory> directoryList) {
        this.directoryList = directoryList;
    }

    public PhotoDirectory getSelectDirectory() {
        return directoryList.get(selectIndex);
    }

    /**
     * 选中状态改变，统一更新
     *
     * @param id
     */
    private void notifyDataChange(int id) {
        for (OnCheckChangeListener listener : onCheckChangeListeners) {
            listener.onCheckedChanged(checkPhotos, id);
        }
    }

    public interface OnCheckChangeListener {
        void onCheckedChanged(Map<Integer, Photo> checkStaus, int changedId);
    }

    /**
     * 获取选中图片的路径集合
     *
     * @return
     */
    public ArrayList<String> getSelected() {
        ArrayList<String> pathList = new ArrayList<>(checkPhotos.size());
        for (Integer integer : checkPhotos.keySet()) {
            Photo photo = checkPhotos.get(integer);
            pathList.add(photo.getPath());
        }
        return pathList;
    }

    public boolean isOriginal() {
        return isOriginal;
    }

    public void setOriginal(boolean original) {
        isOriginal = original;
    }
}
