package com.rair.customview;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private Button btnGroup;
    private Button btnSimple;
    private Button custom;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btnSimple = (Button) findViewById(R.id.btn_simple);
        btnGroup = (Button) findViewById(R.id.btn_group);
        custom = (Button) findViewById(R.id.custom);
        btnSimple.setOnClickListener(this);
        btnGroup.setOnClickListener(this);
        custom.setOnClickListener(this);

        findViewById(R.id.expand_image).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_simple:
                startActivity(new Intent(this, SimpleViewActivity.class));
                break;
            case R.id.btn_group:
                startActivity(new Intent(this, GroupViewActivity.class));
                break;
            case R.id.custom:
                startActivity(new Intent(this, CustomViewActivity.class));
                break;
            case R.id.expand_image:
                startActivity(new Intent(this, ImageExpandViewActivity.class));
                break;
        }
    }
}
