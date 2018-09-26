package com.android.king.albumpicker.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.king.albumpicker.R;
import com.android.king.albumpicker.entity.PhotoDirectory;
import com.android.king.albumpicker.util.ImageUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * 相册目录列表适配器
 *
 * @author king
 * @since 2018/01/10
 */
public class PopupDirectoryListAdapter extends BaseAdapter {
    private List<PhotoDirectory> mDataList = new ArrayList<>();
    protected Context mContext;
    protected LayoutInflater mInflater;

    public PopupDirectoryListAdapter(Context context, List<PhotoDirectory> directories) {
        this.mContext = context;
        this.mDataList = directories;
        this.mInflater = LayoutInflater.from(context);
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
    public View getView(int position, View convertView, ViewGroup viewGroup) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.album_item_pop_directory, null);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.bindData(mDataList.get(position));

        return convertView;
    }


    private class ViewHolder {

        public ImageView ivCover;
        public TextView tvName;
        public TextView tvCount;
        public ImageView ivSelect;

        public ViewHolder(View rootView) {
            ivCover = rootView.findViewById(R.id.iv_dir_cover);
            tvName = rootView.findViewById(R.id.tv_dir_name);
            tvCount = rootView.findViewById(R.id.tv_dir_count);
            ivSelect = rootView.findViewById(R.id.iv_selected);
        }

        public void bindData(PhotoDirectory directory) {
            ImageUtil.showImage(ivCover.getContext(),"file:///" + directory.getCoverPath(),ivCover);
            tvName.setText(directory.getName());
            tvCount.setText(tvCount.getContext().getString(R.string.picker_image_count, directory.getPhotos().size()));
            ivSelect.setVisibility(directory.isSelected() ? View.VISIBLE : View.GONE);

        }

    }

}
