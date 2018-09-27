# 安卓仿微信图片选择器

一款参考微信的图片/视频选择器

### 功能

- 支持选择图片/视频
- 支持单选/多选、预览
- 支持图片压缩
- 自定义图片加载器，Glide、Picasso、ImageLoader随你喜欢

### 待完善

- 支持拍摄照片/视频
- 图片裁剪、旋转

### 示例图
<div>
<img src="/guide/picker.jpg" width="250" height="445" style="float:left"/>     <img src="/guide/preview.jpg" width="250" height="445"/>
</div>

### 引入

```groovy
   implementation 'com.king.ui:albumpicker:1.0.0'
```

### 用法

1.创建一个图片加载器类,实现com.android.king.albumpicker.util.ImageLoader接口

```java
public class GlideLoader implements ImageLoader {

    @Override
    public void showImage(Context context, String path, ImageView imageView) {

        RequestOptions options = new RequestOptions();
        options.placeholder(R.drawable.album_ic_placeholder);
        options.fitCenter();
        Glide.with(context).load(path) //Glide
                    .apply(options)
                    .into(imageView);
      
    }
}
```

2.配置参数

```java
 Intent intent = AlbumPicker.getInstance(this)
                .setImageLoader(new GlideLoader())  //设置图片加载器
                .setMax(max) //最大选择数
                .setSelectType(AlbumConstant.TYPE_IMAGE) //选择类型 TYPE_ALL:图片和视频 TYPE_IMAGE:图片 TYPE_VIDEO:视频
                .setMode(AlbumConstant.MODE_MULTI) //选择模式 MODE_MULTI：多选 MODE_SINGLE：单选
                .setWidthLimit(1080)  //压缩宽度限定，为原图时此设置无效，默认720
                .setHeightLimit(1920) //压缩高度限定，为原图时此设置无效，默认1280
                .getIntent();
 startActivityForResult(intent, 100);
```

3.获取结果

```java
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK && requestCode == 100 && data != null) {
           ArrayList<String> pathList = data.getStringArrayListExtra(AlbumConstant.RESULT_KEY_PATH_LIST);
           for (String path : pathList) {
              Log.i(TAG, path);
           }
        }
    }
```

如果大家在使用过程中发现bug，欢迎在[issues](https://github.com/GdinKing/AlbumPicker/issues)给我留言！

### 更新日志

#### 2018-09-27 v1.0.0

 发布1.0.0版本


