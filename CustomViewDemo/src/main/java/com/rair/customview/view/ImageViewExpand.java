package com.rair.customview.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.ImageView;

import com.rair.customview.R;

public class ImageViewExpand extends ImageView {

    private Bitmap bitmap;
    //每次刷新比上一次多显示图片的比例
    private float step = 0.01f;
    //已经显示图片的比例
    private float currentScale = 0.0f;
    //显示图片的区域
    private RectF dst;
    private Rect src;
    private int width;
    private int height;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 1)
                invalidate();
        }
    };

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        currentScale = 1;   //当触摸的时候马上进行一个显示完整图片
        return super.onTouchEvent(event);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        //因为这里默认wrap_content时候也是match_parent，没有特别处理，因此这一步可以不必要写，只是出于习惯做法
//        width = MeasureSpec.getSize(widthMeasureSpec);
//        height = MeasureSpec.getSize(heightMeasureSpec);
//        setMeasuredDimension(width, height);  //--->如果没什么特殊要求不要写这段代码,在ScrollView中会出现问题
    }

    public void initView() {
        BitmapFactory.Options opts = new BitmapFactory.Options();
        opts.inSampleSize = 2;
        //这里是获取定制的drawable
        Drawable drawable = getDrawable();
        if (drawable==null) {
            drawable = getResources().getDrawable(R.mipmap.flower);
        }
        //将drawable压缩成bitmap
        bitmap = drawableToBitmap(drawable);

        dst = new RectF();
        dst.left = 0;
        dst.top = 0;
        dst.bottom = bitmap.getHeight();
        src = new Rect();
        src.left = 0;
        src.top = 0;
        src.bottom = bitmap.getHeight();
    }
    public ImageViewExpand(Context context) {
        super(context, null);
        initView();
    }
    public ImageViewExpand(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView();
    }


    /**
     * 默认从左到右显示
     */
    @Override
    protected void onDraw(Canvas canvas) {

        //将bitmap等比例压缩
        bitmap = zoomBitmap(bitmap, getWidth(), getHeight());   //这个应该移动到initView方法里,onDraw会执行多次

        currentScale = currentScale + step > 1 ? 1 : currentScale + step;
        dst.right = bitmap.getWidth() * currentScale;
        src.right = (int) (bitmap.getWidth() * currentScale);

        //这是对高度进行了下拉式显示
        dst.bottom = bitmap.getHeight() * currentScale;
        src.bottom = (int) (bitmap.getHeight()*currentScale);
        /*
         * drawBitmap(Bitmap bitmap, Rect src, RectF dst, Paint paint)；
         * Rect src: 是对图片进行裁截，若是空null则显示整个图片
         * RectF dst：是图片在Canvas画布中显示的区域，
         * 大于src则把src的裁截区放大，
         * 小于src则把src的裁截区缩小。
         * 当想要让图片以画卷方式展现的话，主要是设置src大小，这边是默认从左到右显示，所以每次只要修改src中right的大小就好
         */
        canvas.drawBitmap(bitmap, src, dst, null);
        if (currentScale >= 1) {
            //当显示完图片，重置
            currentScale = 0 - step;
        }else {
            //不想让整个页面处于频繁刷新的状态，这里延迟了50ms来刷新该页面
            handler.sendEmptyMessageDelayed(1, 50);
        }
    }

    public static Bitmap drawableToBitmap(Drawable drawable) {


        Bitmap bitmap = Bitmap.createBitmap(

                drawable.getIntrinsicWidth(),

                drawable.getIntrinsicHeight(),

                drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888

                        : Bitmap.Config.RGB_565);

        Canvas canvas = new Canvas(bitmap);

        //canvas.setBitmap(bitmap);

        drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());

        drawable.draw(canvas);

        return bitmap;

    }

    public static Bitmap zoomBitmap(Bitmap bitmap, int w, int h) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        Matrix matrix = new Matrix();
        float scaleWidht = ((float) w / width);
        float scaleHeight = ((float) h / height);
        matrix.postScale(scaleWidht, scaleHeight);
        Bitmap newbmp = Bitmap.createBitmap(bitmap, 0, 0, width, height, matrix, true);
        return newbmp;
    }
}