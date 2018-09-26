package com.android.king.albumpicker.ui;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.king.albumpicker.R;


/***
 * Activity基类
 *
 * @since 2018/1/9
 * @author king
 */

public abstract class BaseActivity extends AppCompatActivity {

    protected ImageView ivBack;
    protected TextView tvLeft;
    protected TextView tvTitle;
    protected TextView tvRight;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayout());
        initTitleBar();
        initView();
        initData();
    }

    /**
     * 初始化标题栏
     */
    private void initTitleBar() {
        tvTitle = findViewById(R.id.tv_title);
        tvLeft = findViewById(R.id.tv_left);
        ivBack = findViewById(R.id.iv_back);
        tvRight = findViewById(R.id.tv_right);
    }

    protected void showProgress(String s) {
        if (progressDialog == null) {
            progressDialog = new ProgressDialog(this);
            progressDialog.setMessage(s);
            progressDialog.setCancelable(false);
            progressDialog.setCanceledOnTouchOutside(false);
        }
        if (!progressDialog.isShowing()) {
            progressDialog.show();
        }

    }

    protected void hideProgress() {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }

    /**
     * 获取布局
     *
     * @return
     */
    protected abstract int getLayout();

    /**
     * 初始化视图
     */
    protected abstract void initView();

    /**
     * 初始化数据
     */
    protected abstract void initData();

}
