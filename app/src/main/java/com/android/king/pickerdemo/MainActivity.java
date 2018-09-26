package com.android.king.pickerdemo;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.android.king.albumpicker.AlbumPicker;
import com.android.king.albumpicker.ui.AlbumPickerActivity;
import com.android.king.albumpicker.util.AlbumConstant;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements RadioGroup.OnCheckedChangeListener {
    private static final String TAG = MainActivity.class.getCanonicalName();


    private RadioGroup rgType;
    private RadioGroup rgMode;
    private EditText etMax;
    private TextView tvResult;
    private int selectType = AlbumConstant.TYPE_ALL;
    private int selectMode = AlbumConstant.MODE_MULTI;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        rgType = findViewById(R.id.rg_type);
        rgMode = findViewById(R.id.rg_mode);
        etMax = findViewById(R.id.et_max);
        tvResult = findViewById(R.id.tv_result);
        rgMode.setOnCheckedChangeListener(this);
        rgType.setOnCheckedChangeListener(this);
    }

    public void selectImage(View view) {
        int max = Integer.valueOf(etMax.getText().toString());

        Intent intent = AlbumPicker.getInstance(this)
                .setImageLoader(new GlideLoader())  //设置图片加载器
                .setMax(max) //最大选择数
                .setSelectType(selectType)//选择类型 TYPE_ALL:图片和视频 TYPE_IMAGE:图片 TYPE_VIDEO:视频
                .setMode(selectMode) //选择模式 MODE_MULTI：多选 MODE_SINGLE：单选
                .setWidthLimit(1080)  //压缩宽度限定，为原图时此设置无效
                .setHeightLimit(1920) //压缩高度限定，为原图时此设置无效
                .getIntent();
        startActivityForResult(intent, 100);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK && requestCode == 100 && data != null) {
            try {
                ArrayList<String> pathList = data.getStringArrayListExtra(AlbumConstant.RESULT_KEY_PATH_LIST);
                for (String path : pathList) {
                    Log.i(TAG, path);
                }
                tvResult.setText(pathList.toString());
            } catch (Exception e) {
            }
        }
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        switch (checkedId) {
            case R.id.rb_type_all:
                selectType = AlbumConstant.TYPE_ALL;
                break;
            case R.id.rb_type_image:
                selectType = AlbumConstant.TYPE_IMAGE;
                break;
            case R.id.rb_type_video:
                selectType = AlbumConstant.TYPE_VIDEO;
                break;
            case R.id.rb_mode_multi:
                selectMode = AlbumConstant.MODE_MULTI;
                break;
            case R.id.rb_mode_single:
                selectMode = AlbumConstant.MODE_SINGLE;
                break;
        }
    }
}
