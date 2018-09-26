package com.android.king.albumpicker.view;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.PopupWindow;

import com.android.king.albumpicker.util.MediaManager;


/**
 * 弹出菜单
 *
 * @author king
 * @since 2018/01/05
 */
public class PopupWindowMenu extends PopupWindow {

    private ListView listView;
    private BaseAdapter baseAdapter;

    public PopupWindowMenu(Context context, final AdapterView.OnItemClickListener onItemClickListener) {
        super(context);
        listView = new ListView(context);
        this.setContentView(listView);
        this.setWidth(LayoutParams.MATCH_PARENT);
        this.setHeight(LayoutParams.WRAP_CONTENT);
        this.setFocusable(false);
        this.setTouchable(true);
        this.setOutsideTouchable(true);
        ColorDrawable dw = new ColorDrawable(0xffffffff);
        listView.setBackgroundDrawable(dw);
        this.setBackgroundDrawable(null);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                MediaManager mediaManager = MediaManager.getInstance();
                if (position == mediaManager.getSelectIndex()) {
                    hide();
                    return;
                }
                mediaManager.getSelectDirectory().setSelected(false);
                mediaManager.getDirectoryList().get(position).setSelected(true);
                mediaManager.setSelectIndex(position);
                baseAdapter.notifyDataSetChanged();
                if (onItemClickListener != null)
                    onItemClickListener.onItemClick(parent, view, position, id);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        hide();
                    }
                }, 200);
            }
        });
        this.setTouchInterceptor(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_OUTSIDE) {
                    hide();
                    return true;
                }
                return false;
            }
        });
    }

    public void setAdapter(BaseAdapter baseAdapter) {
        listView.setAdapter(baseAdapter);
        this.baseAdapter = baseAdapter;
    }

    public BaseAdapter getAdapter() {
        return baseAdapter;
    }

    @Override
    public void showAtLocation(View parent, int gravity, int x, int y) {
        TranslateAnimation showAnim = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0.0f,
                Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF
                , 1.0f, Animation.RELATIVE_TO_SELF, 0.0f);
        showAnim.setDuration(200);
        getContentView().startAnimation(showAnim);
        super.showAtLocation(parent, gravity, x, y);
    }


    /**
     * 隐藏菜单
     */
    public void hide() {
        TranslateAnimation hideAmin = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0.0f,
                Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF
                , 0.0f, Animation.RELATIVE_TO_SELF, 1.0f);
        hideAmin.setDuration(200);
        getContentView().startAnimation(hideAmin);
        getContentView().postDelayed(new Runnable() {
            @Override
            public void run() {
                dismiss();
            }
        }, 200);
    }
}
