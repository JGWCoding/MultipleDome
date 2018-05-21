package com.rair.customview.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import com.rair.customview.R;


/**
 * Created by Administrator on 2018/3/12.
 */

public abstract class BaseView extends View {
    //坐标轴画笔
    private Paint mXYAxisPaint;
    private Paint mGraphTitlePaint;
    private Paint mAxisNamePaint;
    public Paint mPaint;    //画笔


    private String mGrapthTitle;    //主题title
    private String mXAxisName;  //x轴坐标名
    private String mYAxisName;  //y轴坐标名
    private int mAxisTextColor;     //坐标轴上text颜色
    private float mAxisTextSize;    //坐标轴上text大小

    //X坐标轴最大刻度线离x轴的距离
    public float arrowAndAxisValueX = 50;
    //X坐标轴刻度线数量
    public int axisDivideSizeX = 7;
    //X坐标轴最大刻度线离x轴的距离
    public float arrowAndAxisValueY = 50;
    //Y坐标轴刻度线数量
    public int axisDivideSizeY = 7;
    //视图宽度
    public int width;
    //视图高度
    public int height;
    //坐标原点位置
    public final int originX = 100;
    public  int originY ;
    //柱状图数据
    public float columnInfo[][];


    public BaseView(Context context) {
        this(context,null);
    }

    public BaseView(Context context, AttributeSet attrs) {
        this(context, attrs,-1);
    }

    public BaseView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.MukeGraphStyle);
        mGrapthTitle = typedArray.getString(R.styleable.MukeGraphStyle_graphTitle);
        mXAxisName = typedArray.getString(R.styleable.MukeGraphStyle_xAxisName);
        mYAxisName = typedArray.getString(R.styleable.MukeGraphStyle_yAxisName);
        mAxisTextColor = typedArray.getColor(R.styleable.MukeGraphStyle_axisTextColor,context.getResources().getColor(android.R.color.black));
        mAxisTextSize = typedArray.getDimension(R.styleable.MukeGraphStyle_axisTextSize,12);

        if(typedArray != null){
            typedArray.recycle();
        }

        initPaint(context);

    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        originY = getHeight() - 300;
        width = getWidth() - originX - 100;
        height = originY - 300;
        Log.e("qqq","Width="+width+"height="+height + "w"+w+"h"+h);
        super.onSizeChanged(w, h, oldw, oldh);
    }

    private void initPaint(Context context) {

        if(mPaint == null){
            mPaint = new Paint();
            mPaint.setAntiAlias(true);  //抗锯齿  --- 平滑
            mPaint.setDither(true);     //防抖动
            mPaint.setTextSize(20);
        }
    }


    public void setGrapthTitle(String grapthTitle) {
        mGrapthTitle = grapthTitle;
    }

    public void setXAxisName(String XAxisName) {
        mXAxisName = XAxisName;
    }

    public void setYAxisName(String YAxisName) {
        mYAxisName = YAxisName;
    }

    public void setAxisTextColor(int axisTextColor) {
        mAxisTextColor = axisTextColor;
    }

    public void setAxisTextSize(float axisTextSize) {
        mAxisTextSize = axisTextSize;
    }

    /**
     * 手动设置X轴最大值及等份数
     * @param dividedXSize
     */
    public void setXAndYAxisValue(int dividedXSize,int dividedYSize) {
        this.axisDivideSizeX = dividedXSize;
        this.axisDivideSizeY = dividedYSize;
    }

    /**
     * 传入柱状图数据
     * @param columnInfo
     */
    public void setColumnInfo(float[][] columnInfo) {
        this.columnInfo = columnInfo;
    }


    @Override
    protected void onDraw(Canvas canvas) {
        drawAxisX(canvas, mPaint);  //画X轴
        drawAxisY(canvas, mPaint);  //画Y轴
        drawAxisScaleMarkX(canvas, mPaint);   //画x轴的刻度线
        drawAxisScaleMarkY(canvas, mPaint);     //画y轴的刻度线
        drawAxisArrowsX(canvas, mPaint);    //画x轴的箭头
        drawAxisArrowsY(canvas, mPaint);    //画y轴的箭头
        drawAxisScaleMarkValueX(canvas, mPaint);    //画x轴刻度线上的值
        drawAxisScaleMarkValueY(canvas, mPaint);    //画y轴刻度线上的值
        drawColumn(canvas, mPaint); //画条形图
        drawTitle(canvas, mPaint);  //画标题
    }


    protected abstract void drawAxisScaleMarkValueY(Canvas canvas, Paint paint);

    protected abstract void drawAxisScaleMarkValueX(Canvas canvas, Paint paint);

    //画标题
    private void drawTitle(Canvas canvas, Paint paint) {
        //画标题
        if (mGrapthTitle != null) {
            //设置画笔绘制文字的属性
            mPaint.setColor(mAxisTextColor);
            mPaint.setTextSize(mAxisTextSize);
            mPaint.setFakeBoldText(true);
            canvas.drawText(mGrapthTitle, (getWidth()/2)-(paint.measureText(mGrapthTitle)/2), originY + 100, paint);
        }
    }

    //开始 画中间的矩形
    protected abstract void drawColumn(Canvas canvas, Paint paint);


    private void drawAxisArrowsY(Canvas canvas, Paint paint) {
        //画三角形（Y轴箭头）
        Path mPathX = new Path();
        mPathX.moveTo(originX, originY - height - 30 -arrowAndAxisValueY);//起始点
        mPathX.lineTo(originX - 10, originY - height-arrowAndAxisValueY);//下一点
        mPathX.lineTo(originX + 10, originY - height-arrowAndAxisValueY);//下一点
        mPathX.close();
        canvas.drawPath(mPathX, paint);
        paint.setTextSize(20);
        canvas.drawText(mYAxisName,originX-50,originY-height-30-arrowAndAxisValueY,paint);
    }

    /**
     * X轴上的箭头
     * @param canvas
     * @param paint
     */
    private void drawAxisArrowsX(Canvas canvas, Paint paint) {
        //画三角形（X轴箭头）
        Path mPathX = new Path();
        mPathX.moveTo(originX + width+arrowAndAxisValueX + 30, originY);//起始点
        mPathX.lineTo(originX + width+arrowAndAxisValueX, originY - 10);//下一点
        mPathX.lineTo(originX + width+arrowAndAxisValueX, originY + 10);//下一点
        mPathX.close();
        canvas.drawPath(mPathX, paint);
        paint.setTextSize(20);
        canvas.drawText(mXAxisName,originX+width+arrowAndAxisValueX/2,originY+30,paint);
    }

    /**
     * Y轴上的标记
     * @param canvas
     * @param paint
     */
    protected abstract void drawAxisScaleMarkY(Canvas canvas, Paint paint);

    /**
     * X轴上的标记
     * @param canvas
     * @param paint
     */
    protected abstract void drawAxisScaleMarkX(Canvas canvas, Paint paint);

    protected abstract void drawAxisY(Canvas canvas, Paint paint);

    protected abstract void drawAxisX(Canvas canvas, Paint paint);
}

