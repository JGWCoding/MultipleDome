package com.rair.customview.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

public class SimpleView extends View {

    private static final String TAG = "SimpleView";
    // 声明画笔
    private Paint paint;
    private Bitmap mBitmap;  //保存每次绘画的结果
    // 创建构造方法
    public SimpleView(Context context) {
        //super(context);
        // 调用当前类的其他的构造方法
        this(context, null);
    }

    /**
     * 如果在布局中添加自定义控件，则需要添加如下构造方法
     *
     * @param context      - 上下文
     * @param  set - 属性对象，它包含这当前控件所有的属性
     */
    public SimpleView(Context context, AttributeSet set) {
        super(context, set);
        initPaint();
    }

    /**
     * 初始化画笔
     */
    private void initPaint() {
        // 定义画笔
        paint = new Paint();
        // 设置画笔的字体大小
        paint.setTextSize(100);
        // 线条的样式 - 粗体 斜线
        paint.setTypeface(Typeface.DEFAULT_BOLD);
        paint.setStrokeWidth(10);
        // 设置画笔的颜色
        paint.setColor(Color.RED);
        // 设置画笔的样式
        // Paint.Style.STROKE - 边线样式 - 圆(空心圆)
        // Paint.Style.FILL - 填充样式 - 圆(实心圆)
        paint.setStyle(Paint.Style.STROKE);
        //paint.setStyle(Paint.Style.FILL);
        // 是否抗锯齿
        paint.setAntiAlias(true);
    }

    /**
     * 用于绘制
     * 当初始化View(在代码中添加自定义控件，或者在布局中添加加载时)，此方法会调用
     *
     * @param canvas - 画布
     */
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        Log.d(TAG, "onDraw: x = "+x+" y="+y+"move_x = "+move_x+" move_y="+move_y);
        int width = getWidth();
        int height = getHeight();

        if (mBitmap == null) {
            mBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        }

//先将结果画到Bitmap上
        Canvas tmpCanvas = new Canvas(mBitmap);
        tmpCanvas.drawLine(x, y, move_x, move_y, paint);

//再把Bitmap画到canvas上
        canvas.drawBitmap(mBitmap, 0, 0, paint);  //实现一个画板的功能
        /****************绘制内容***************/
        // 绘制线
//        canvas.drawLine(100, 100, 600, 300, paint);
//        // 绘制圆
//        canvas.drawCircle(500, 500, 200, paint);
//        // 绘制文字
//        canvas.drawText("绘制内容", 450, 750, paint);
//        // 绘制矩形
//        RectF rect = new RectF();
//        rect.left = 400;
//        rect.top = 950;
//        rect.right = 600;
//        rect.bottom = 1100;
//        //canvas.drawRect(rect,paint);
//        canvas.drawRect(400, 950, 600, 1100, paint);
    }
        float x, y,move_x, move_y;
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        x = move_x;
        y = move_y;
        move_x = event.getX();
        move_y = event.getY();
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                x = event.getX();
                y = event.getY();
                break;
            case MotionEvent.ACTION_MOVE:
            case MotionEvent.ACTION_UP:
                break;
        }
                postInvalidate(); //用于子线程调用onDraw   invalidate用于主线程更新
//        return super.onTouchEvent(event);
        return true;
    }
}
