package com.rair.customview.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;

/**
 * Created by Administrator on 2018/3/12.
 */

public class MukeChart extends BaseView {

    public MukeChart(Context context) {
        this(context,null);
    }

    public MukeChart(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    public MukeChart(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }



    @Override
    protected void drawAxisScaleMarkValueY(Canvas canvas, Paint paint) {
        float cellHeight = height / axisDivideSizeY;
        for (int i = 1; i < axisDivideSizeY+1; i++) {
            canvas.drawText(String.valueOf(i), originX - 30, originY - cellHeight * i , paint);
        }
    }

    @Override
    protected void drawAxisScaleMarkValueX(Canvas canvas, Paint paint) {
        //设置画笔绘制文字的属性
        paint.setColor(Color.GRAY);
        paint.setTextSize(20);
        paint.setFakeBoldText(true);

        float cellWidth = width / axisDivideSizeX;
        for (int i = 1; i < axisDivideSizeX +1; i++) {
            canvas.drawText(String.valueOf(i), cellWidth * i + originX - paint.measureText(i+"")/2 - cellWidth/2, originY + 30, paint);
        }
    }

    @Override
    protected void drawColumn(Canvas canvas, Paint paint) {
        if(columnInfo == null)
            return;
        float cellWidth = width / axisDivideSizeX;
        for (int i = 0; i < columnInfo.length; i++) {
            paint.setColor((int) columnInfo[i][1]);
            float leftTopY = originY - height * columnInfo[i][0] / axisDivideSizeY;
            //左上角x,y右下角x,y，画笔
            canvas.drawRect(originX + cellWidth * (i), leftTopY, originX + cellWidth * (i + 1), originY, mPaint);
            paint.setColor(Color.RED);
            canvas.drawText(columnInfo[i][0]+"",originX+cellWidth*(i+1/2f)-paint.measureText(columnInfo[i][0]+"")/2,leftTopY-15,paint);
        }
    }

    @Override
    protected void drawAxisScaleMarkY(Canvas canvas, Paint paint) {
        float cellHeight = height / axisDivideSizeY;
        for (int i = 0; i < axisDivideSizeY ; i++) {
            canvas.drawLine(originX, (originY - cellHeight * (i + 1)), originX + 10, (originY - cellHeight * (i + 1)), paint);
        }
    }

    @Override
    protected void drawAxisScaleMarkX(Canvas canvas, Paint paint) {
        float cellWidth = width / axisDivideSizeX;
        for (int i = 0; i < axisDivideSizeX ; i++) {
            canvas.drawLine(cellWidth * (i + 1) + originX, originY,
                    cellWidth * (i + 1) + originX, originY - 10, paint);
        }
    }

    @Override
    protected void drawAxisY(Canvas canvas, Paint paint) {
        //画竖轴(Y)
        canvas.drawLine(originX, originY, originX, originY - height- arrowAndAxisValueY, paint);//参数说明：起始点左边x,y，终点坐标x,y，画笔

    }

    @Override
    protected void drawAxisX(Canvas canvas, Paint paint) {
        paint.setColor(Color.BLACK);
        //设置画笔宽度
        paint.setStrokeWidth(5);
        //设置画笔抗锯齿
        paint.setAntiAlias(true);
        //画横轴(X)
        canvas.drawLine(originX, originY, originX + width+ arrowAndAxisValueX, originY, paint);

    }

}