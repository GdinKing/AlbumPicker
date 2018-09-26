package com.android.king.albumpicker.util;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.database.MergeCursor;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.text.TextUtils;

import com.android.king.albumpicker.R;
import com.android.king.albumpicker.entity.Photo;
import com.android.king.albumpicker.entity.PhotoDirectory;
import com.android.king.albumpicker.ui.AlbumPickerActivity;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import static android.provider.BaseColumns._ID;
import static android.provider.MediaStore.Images.ImageColumns.BUCKET_DISPLAY_NAME;
import static android.provider.MediaStore.Images.ImageColumns.BUCKET_ID;
import static android.provider.MediaStore.MediaColumns.DATA;
import static android.provider.MediaStore.MediaColumns.DATE_ADDED;
import static android.provider.MediaStore.MediaColumns.HEIGHT;
import static android.provider.MediaStore.MediaColumns.MIME_TYPE;
import static android.provider.MediaStore.MediaColumns.SIZE;
import static android.provider.MediaStore.MediaColumns.WIDTH;

/**
 * 加载手机中图片/视频
 *
 * @author king
 * @since 2018/01/10
 */
public class MediaStoreHelper {

    public final static int INDEX_ALL_PHOTOS = 0;

    /**
     * 加载图片/视频的异步线程
     */
    public static class FetchMediaThread extends Thread {
        WeakReference<Context> contextWeakReference;
        Handler callbackHandler;
        int type;

        public FetchMediaThread(Context context, int type, Handler handler) {
            this.contextWeakReference = new WeakReference<>(context);
            this.callbackHandler = handler;
            this.type = type;
        }

        @Override
        public void run() {
            if (contextWeakReference.get() == null) {
                return;
            }
            ContentResolver contentResolver = contextWeakReference.get().getContentResolver();
            ArrayList<Cursor> cursorArrayList = new ArrayList<>();
            Cursor videoCursor = contentResolver.query(MediaStore.Video.Media.EXTERNAL_CONTENT_URI
                    , new String[]{MediaStore.Video.Media._ID,
                            MediaStore.Video.Media.DATA,
                            MediaStore.Video.Media.BUCKET_ID,
                            MediaStore.Video.Media.BUCKET_DISPLAY_NAME,
                            MediaStore.Video.Media.DATE_ADDED,
                            MediaStore.Video.Media.SIZE,
                            MediaStore.Video.Media.WIDTH,
                            MediaStore.Video.Media.HEIGHT,
                            MediaStore.Video.VideoColumns.DURATION,
                            MediaStore.Video.Media.MIME_TYPE}
                    , MIME_TYPE + "=? or " + MIME_TYPE + "=? or " + MIME_TYPE + "=? or " + MIME_TYPE + "=? "
                    , new String[]{"video/mpeg", "video/mp4", "video/3gp", "video/avi"}
                    , MediaStore.Images.Media.DATE_ADDED + " DESC");
            if ((AlbumConstant.TYPE_VIDEO & type) == AlbumConstant.TYPE_VIDEO) {
                cursorArrayList.add(videoCursor);
            }


            Cursor imageCursor = contentResolver.query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                    , new String[]{MediaStore.Images.Media._ID,
                            MediaStore.Images.Media.DATA,
                            MediaStore.Images.Media.BUCKET_ID,
                            MediaStore.Images.Media.BUCKET_DISPLAY_NAME,
                            MediaStore.Images.Media.DATE_ADDED,
                            MediaStore.Images.Media.SIZE,
                            MediaStore.Video.Media.WIDTH,
                            MediaStore.Video.Media.HEIGHT,
                            MediaStore.Images.Media.MIME_TYPE}
                    , MIME_TYPE + "=? or " + MIME_TYPE + "=? or " + MIME_TYPE + "=? or " + MIME_TYPE + "=?"
                    , new String[]{"image/jpeg", "image/png", "image/jpg", "image/gif"}
                    , MediaStore.Images.Media.DATE_ADDED + " DESC");
            if ((AlbumConstant.TYPE_IMAGE & type) == AlbumConstant.TYPE_IMAGE) {
                cursorArrayList.add(imageCursor);
            }
            Cursor[] cursors = cursorArrayList.toArray(new Cursor[cursorArrayList.size()]);
            MergeCursor data = new MergeCursor(cursors);
            if (data == null) return;
            List<PhotoDirectory> directories = new ArrayList<>();
            if (contextWeakReference.get() == null)
                return;
            PhotoDirectory photoDirectoryAll = new PhotoDirectory();
            if (type == AlbumConstant.TYPE_ALL) {
                photoDirectoryAll.setName(contextWeakReference.get().getString(R.string.image_video));
            } else if (type == AlbumConstant.TYPE_IMAGE) {
                photoDirectoryAll.setName(contextWeakReference.get().getString(R.string.last_image));
            }
            photoDirectoryAll.setId(1);

            PhotoDirectory videoDirectoryAll = new PhotoDirectory();
            videoDirectoryAll.setName(contextWeakReference.get().getString(R.string.all_video));
            videoDirectoryAll.setId(2);
            while (data.moveToNext()) {

                int imageId = data.getInt(data.getColumnIndexOrThrow(_ID));
                int bucketId = data.getInt(data.getColumnIndexOrThrow(BUCKET_ID));
                String name = data.getString(data.getColumnIndexOrThrow(BUCKET_DISPLAY_NAME));
                String path = data.getString(data.getColumnIndexOrThrow(DATA));
                long size = data.getLong(data.getColumnIndexOrThrow(SIZE));
                String mimeType = data.getString(data.getColumnIndexOrThrow(MIME_TYPE));
                int width = data.getInt(data.getColumnIndexOrThrow(WIDTH));
                int height = data.getInt(data.getColumnIndexOrThrow(HEIGHT));
                long addDate = data.getInt(data.getColumnIndexOrThrow(DATE_ADDED));
                if (size < 1) continue;

                Photo photo = new Photo(imageId, path, mimeType, width, height, size);
                photo.setAddDate(addDate);
                if (!TextUtils.isEmpty(mimeType) && mimeType.contains("video")) {
                    long duration = data.getLong(data.getColumnIndexOrThrow(MediaStore.Video.VideoColumns.DURATION));
                    if (duration > 1000) {  //大于1秒的视频才显示
                        photo.setDuration(duration);
                        videoDirectoryAll.addPhoto(photo);
                    }
                }

                PhotoDirectory photoDirectory = null;
                for (PhotoDirectory dir :
                        directories) {
                    if (dir.getId() == bucketId) {
                        photoDirectory = dir;
                        break;
                    }
                }
                if (photoDirectory == null) {
                    photoDirectory = new PhotoDirectory();
                    photoDirectory.setId(bucketId);
                    photoDirectory.setName(name);
                    photoDirectory.setCoverPath(path);
                    photoDirectory.setDateAdded(data.getLong(data.getColumnIndexOrThrow(DATE_ADDED)));
                    directories.add(photoDirectory);
                }

                photoDirectory.addPhoto(photo);
                photoDirectoryAll.addPhoto(photo);
            }
            data.close();
            Collections.sort(photoDirectoryAll.getPhotos(), new Comparator<Photo>() {
                @Override
                public int compare(Photo lhs, Photo rhs) {
                    return lhs.getAddDate() >= rhs.getAddDate() ? -1 : 1;//按照添加时间进行降序排序
                }
            });
            if (photoDirectoryAll.getPhotoPaths().size() > 0) {
                photoDirectoryAll.setCoverPath(photoDirectoryAll.getPhotoPaths().get(0));
            }
            if ((AlbumConstant.TYPE_IMAGE & type) == AlbumConstant.TYPE_IMAGE)
                directories.add(INDEX_ALL_PHOTOS, photoDirectoryAll);
            if (!videoDirectoryAll.getPhotos().isEmpty() && (AlbumConstant.TYPE_VIDEO & type) == AlbumConstant.TYPE_VIDEO) {
                videoDirectoryAll.setCoverPath(videoDirectoryAll.getPhotoPaths().get(0));
                directories.add(INDEX_ALL_PHOTOS + 1, videoDirectoryAll);
            }
            if (callbackHandler != null) {  //加载完毕，传递结果
                Message msg = Message.obtain();
                msg.obj = directories;
                msg.what = AlbumPickerActivity.REQUEST_GET_PHOTO;
                msg.setTarget(callbackHandler);
                msg.sendToTarget();
            }
        }
    }


    /**
     * 开始加载
     *
     * @param context
     * @param type
     * @param handler
     */
    public static void getPhotoData(Context context, int type, final Handler handler) {
        new FetchMediaThread(context, type, handler).start();
    }


}
