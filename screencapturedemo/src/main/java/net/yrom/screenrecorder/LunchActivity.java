package net.yrom.screenrecorder;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import net.yrom.screenrecorder.screencarturedome.MainScreenCartureActivity;

public class LunchActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lunch);
        findViewById(R.id.tv1).setOnClickListener(onClick->{startActivity(new Intent(LunchActivity.this, MainScreenCartureActivity.class));finish();});
//        findViewById(R.id.tv1).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                startActivity(new Intent(LunchActivity.this, MainScreenCartureActivity.class));
//            }
//        });
        findViewById(R.id.tv2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LunchActivity.this, MainActivity.class));
                finish();
            }
        });
    }
}
