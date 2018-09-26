package com.android.king.albumpicker.adapter;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.widget.AppCompatCheckBox;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.android.king.albumpicker.R;
import com.android.king.albumpicker.entity.Photo;
import com.android.king.albumpicker.util.AlbumConstant;
import com.android.king.albumpicker.util.ImageUtil;
import com.android.king.albumpicker.util.MediaManager;
import com.android.king.albumpicker.view.SquareImageView;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * 图片列表适配器
 *
 * @author King
 * @since 2017/5/24
 */

public class AlbumAdapter extends BaseAdapter {

    private List<Photo> mDataList = new ArrayList<Photo>();
    protected Context mContext;
    protected LayoutInflater mInflater;
    /**
     * 选择类型
     */
    private int selectType = AlbumConstant.TYPE_ALL;
    /**
     * 选择模式  多选/单选
     */
    private int mode = AlbumConstant.MODE_MULTI;


    public AlbumAdapter(Context context, List<Photo> datas, int type, int mode) {
        this.mContext = context;
        this.mDataList = datas;
        this.mInflater = LayoutInflater.from(context);
        this.selectType = type;
        this.mode = mode;
    }

    public void setMode(int mode) {
        this.mode = mode;
        notifyDataSetChanged();
    }

    public int getMode() {
        return mode;
    }

    @Override
    public int getCount() {
        if (mDataList == null) {
            return 0;
        }
        return mDataList.size();
    }

    @Override
    public Object getItem(int position) {
        if (mDataList == null) {
            return null;
        }
        return mDataList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.album_item_photo, parent, false);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        if (mode == AlbumConstant.MODE_MULTI) {
            holder.cbSelect.setVisibility(View.VISIBLE);
        } else {
            holder.cbSelect.setVisibility(View.GONE);
        }
        final Photo photo = mDataList.get(position);
        String url = "file:///" + photo.getPath();
        ImageUtil.showImage(mContext, url, holder.ivPhoto);
        final boolean isChecked = MediaManager.getInstance().existPhoto(photo.getId());
        holder.cbSelect.setChecked(isChecked);
        holder.ivPhoto.justSetShowShade(isChecked);
        if (photo.getMimeType().contains("video")) {
            holder.tvTime.setVisibility(View.VISIBLE);
            holder.tvTime.setText(convertDuration(photo.getDuration()));
        } else {
            holder.tvTime.setVisibility(View.GONE);
        }
        holder.cbSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isChecked) {
                    boolean result = MediaManager.getInstance().addMedia(photo.getId(), photo);
                    if (!result) {
                        String tips = "";
                        if (selectType == AlbumConstant.TYPE_IMAGE) {
                            tips = String.format(mContext.getResources().getString(R.string.select_max_image), MediaManager.getInstance().getMaxMediaSum());
                        } else if (selectType == AlbumConstant.TYPE_VIDEO) {
                            tips = String.format(mContext.getResources().getString(R.string.select_max_video), MediaManager.getInstance().getMaxMediaSum());
                        } else {
                            tips = String.format(mContext.getResources().getString(R.string.select_max_sum), MediaManager.getInstance().getMaxMediaSum());
                        }
                        Toast.makeText(mContext, tips, Toast.LENGTH_SHORT).show();
                    }
                } else {
                    MediaManager.getInstance().removeMedia(photo.getId());
                }
                notifyDataSetChanged();

            }
        });
        return convertView;
    }

    /**
     * 刷新数据
     *
     * @param dataList
     */
    public void refreshData(List<Photo> dataList) {
        if (dataList != null) {
            this.mDataList = dataList;
            notifyDataSetChanged();
        }

    }

    /**
     * 转换视频时间显示格式
     *
     * @param duration
     * @return
     */
    private String convertDuration(long duration) {
        StringBuilder durationString = new StringBuilder();
        int second = (int) (duration / 1000);
        int min = second / 60;
        int hour = min / 60;
        if (hour > 0) {
            durationString.append(hour + ":");
        }
        durationString.append(min + ":");
        durationString.append(new DecimalFormat("00").format(second));
        return durationString.toString();
    }


    private class ViewHolder {
        SquareImageView ivPhoto;
        AppCompatCheckBox cbSelect;
        TextView tvTime;

        public ViewHolder(View converView) {
            ivPhoto = converView.findViewById(R.id.item_iv_photo);
            cbSelect = converView.findViewById(R.id.item_cb_select);
            tvTime = converView.findViewById(R.id.item_tv_video_time);
            ivPhoto.setShade(new ColorDrawable(Color.parseColor("#92000000")));
        }
    }
}
