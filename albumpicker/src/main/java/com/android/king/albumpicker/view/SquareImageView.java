package com.android.king.albumpicker.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.AppCompatImageView;
import android.util.AttributeSet;

import com.android.king.albumpicker.R;


/**
 * 带阴影遮罩效果和边框的ImageView
 *
 * @author king
 * @since 2018/01/10
 */
public class SquareImageView extends AppCompatImageView {
    private Drawable shade;
    private boolean showShade = false;
    private boolean showBorder = false;
    private int borderColor = R.color.album_green;
    private float borderWidth = 12f;

    public SquareImageView(Context context) {
        super(context);
    }

    public SquareImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public SquareImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (showShade && shade != null) {
            shade.setBounds(0, 0, getWidth(), getHeight());
            shade.draw(canvas);
        }
        if (showBorder) {
            // 画边框
            Rect rec = canvas.getClipBounds();
            rec.bottom--;
            rec.right--;
            Paint paint = new Paint();
            paint.setColor(getResources().getColor(borderColor));
            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeWidth(borderWidth);
            canvas.drawRect(rec, paint);
        }
    }

    public boolean isShowShade() {
        return showShade;
    }

    public Drawable getShade() {
        return shade;
    }

    public void setShade(Drawable shade) {
        this.shade = shade;
    }

    public void setShowShade(boolean showShade) {
        this.showShade = showShade;
        invalidate();
    }

    public void setBorderColor(int color) {
        this.borderColor = color;
        invalidate();
    }

    public void setBorderWidth(float width) {
        this.borderWidth = width;
        invalidate();
    }

    public void setShowBorder(boolean isShow) {
        this.showBorder = isShow;
        invalidate();
    }

    public void justSetShowShade(boolean showShade) {
        this.showShade = showShade;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, widthMeasureSpec);
    }
}
