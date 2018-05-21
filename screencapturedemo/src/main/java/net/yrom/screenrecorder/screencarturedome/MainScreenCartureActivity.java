package net.yrom.screenrecorder.screencarturedome;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.projection.MediaProjectionManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.FragmentActivity;

import net.yrom.screenrecorder.R;

public class MainScreenCartureActivity extends FragmentActivity {


  public static final int REQUEST_MEDIA_PROJECTION = 18;
  public boolean flag_alert_window = true;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main_dome);
    requestCapturePermission(); //请求权限成功 ---> 开启FloatWindowsService服务 --->启动悬浮窗
//    Toast.makeText(this, "thank you", Toast.LENGTH_SHORT).show();
  }


  public void requestCapturePermission() {

    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {

      return;
    }
    if (Build.VERSION.SDK_INT >= 23) {
      if (!Settings.canDrawOverlays(getApplicationContext())) {
        Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                Uri.parse("package:" + getPackageName()));
        startActivityForResult(intent,2);
//        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.SYSTEM_ALERT_WINDOW}, 2);
      }
      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && this.checkSelfPermission(
              Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
        this.requestPermissions(new String[]{
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
        }, 3);
      }
    }
    MediaProjectionManager mediaProjectionManager = (MediaProjectionManager)
        getSystemService(Context.MEDIA_PROJECTION_SERVICE);
    startActivityForResult(mediaProjectionManager.createScreenCaptureIntent(),
        REQUEST_MEDIA_PROJECTION);
  }

  @Override
  protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    switch (requestCode) {
      case REQUEST_MEDIA_PROJECTION:

        if (resultCode == RESULT_OK && data != null&& flag_alert_window) {
          FloatWindowsService.setResultData(data);
          startService(new Intent(getApplicationContext(), FloatWindowsService.class));
        }
        break;
      case 2:
        if (resultCode==RESULT_CANCELED) {
//          Toast.makeText(this,"",Toast.LENGTH_LONG).show();
          flag_alert_window = false;
        }
        break;
      case 3:
        if (resultCode==RESULT_CANCELED) {
//          Toast.makeText(this,"",Toast.LENGTH_LONG).show();
          flag_alert_window = false;
        }
        break;
    }

  }

}
