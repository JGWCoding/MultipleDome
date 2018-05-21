package com.rair.webviewdemo;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.KeyEvent;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;


/**
 * 该类用于js函数调用
 */
public class JavaScriptObject {
    public String phoneNumber;
    Activity mContext;
    WebView webView;

    public JavaScriptObject(Activity mContxt, WebView webView) {
        this.mContext = mContxt;
        this.webView = webView;
    }

    @JavascriptInterface //TODO 跳转地图  ----> 这个可以匹配多个地图应用
    public void useMap(String place) { //地点
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse("geo:0,0?q="+place)); //查询某个具体地点
        try{
            mContext.startActivity(intent);
        }catch (Exception e) {
        }
    }

    @JavascriptInterface //TODO whatsAPP跳转
    public void whatsAPP(String phoneNumber) {
//        if (APPUtils.isAppInstall(mContext, "com.whatsapp")) { //判断是否装了whatsAPP
//            try{
//                Uri uri = Uri.parse("smsto:" + phoneNumber);
//                Intent intent = new Intent(Intent.ACTION_SENDTO, uri);
//                ClipboardManager clipboard =
//                        (ClipboardManager) mContext.getSystemService(Context.CLIPBOARD_SERVICE);
//                clipboard.setText("你好,我想跟你溝通下"); //設置粘貼板的內容
//                intent.setPackage("com.whatsapp");
//                mContext.startActivity(intent);
//            }catch(Exception e){
//                Log.e("Exception", e.toString());
//            }
//        }
    }

    @JavascriptInterface   //必须加上这个注解,不然不能调用
    public void print(String data) {
        Log.e("=====", data);
    }

    @JavascriptInterface   //TODO 调用相当于清除上一个url, 在按返回键不会出bug(一直返回上一个url地址)
    public void goBack() {
        mContext.onKeyDown(KeyEvent.KEYCODE_BACK, new KeyEvent(0, 0));
    }

    @JavascriptInterface//TODO 用来调用打电话的功能
    public void call(String phoneNumber) {
//        Uri uri = Uri.parse("tel:" + phoneNumber);
//        Intent callIntent = new Intent(Intent.ACTION_CALL, uri);//直接打电话
//        if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
//            this.phoneNumber = phoneNumber; //在这里申请权限,记住下该号码以便申请成功打出去 --->弹出申请权限框
//            ActivityCompat.requestPermissions(mContext, new String[]{Manifest.permission.CALL_PHONE}, Constants.REQUEST_CALL_PHONE_CODE);
//        } else {
////            Intent dialntent = new Intent(Intent.ACTION_DIAL, uri); //跳到系统拨号处
////            mContext.startActivity(dialntent);
//            mContext.startActivity(callIntent);
//        }
    }
}