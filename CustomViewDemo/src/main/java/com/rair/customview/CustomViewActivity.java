package com.rair.customview;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.rair.customview.view.MukeChart;

/**
 * Created by Administrator on 2018/3/12.
 */
public class CustomViewActivity  extends AppCompatActivity {

    private MukeChart mColumnView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_custom);

        mColumnView = (MukeChart) findViewById(R.id.columnView);

        float columnInfo[][] = new float[][]{
                {6.5f, Color.BLUE},
                {5, Color.GREEN},
                {4, Color.RED},
                {3, Color.BLUE},
                {5, Color.YELLOW},
                {4, Color.RED},
                {3, Color.BLUE},
                {5, Color.YELLOW},
                {4, Color.RED},
                {3, Color.BLUE},
                {5, Color.YELLOW},
                {4, Color.RED},
                {3, Color.BLUE},
                {5, Color.YELLOW},
                {3, Color.LTGRAY},
                {2, Color.BLUE}};
        mColumnView.setXAndYAxisValue(columnInfo.length,7);
        mColumnView.setColumnInfo(columnInfo);
    }
}