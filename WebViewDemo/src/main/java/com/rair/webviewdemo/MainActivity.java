package com.rair.webviewdemo;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.webkit.JsResult;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class MainActivity extends AppCompatActivity {

    private WebView mWebView;
    private WebSettings mWebViewSettings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mWebView = (WebView) findViewById(R.id.webview);
        mWebViewSettings = mWebView.getSettings();

        initmWebView();

        mWebView.loadUrl("http://www.baidu.com");

        // 加载本地应用中的html网页
        // http:// https:// file:// ftp://
        // /android_asset - android应用中assets目录
        /*loadUrl()*/
        //mWebView.loadUrl("file:///android_asset/test.html");

        /*loadData()*/
        //String html_text = "<p align='center'>段落内容</p>";
        //mWebView.loadData(html_text,"text/html; charset=utf-8",null);

        mWebView.addJavascriptInterface(new JavaScriptObject(this,mWebView),"androidClient");
//        mWebView.addJavascriptInterface(new Object() {  //也可以这样直接给对象
//            @JavascriptInterface
//            public void externalUrl(String url) {
//                Intent intent = new Intent(Intent.ACTION_VIEW,Uri.parse(url));
//                startActivity(intent);
//            }
//        },"androidClient");
//        mWebView.loadUrl("javascript:javacalljs(var,var)");  //加载JavaScript中的函数,无返回值
//        如果是有返回值的:如果你的兼容最低是19的版本可以直接用这个方法
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
//            mWebView.evaluateJavascript("javascript:sum(3,8)", new ValueCallback<String>() {
//                @Override
//                public void onReceiveValue(String value) {
//                    //返回的值在里面  -->value
//                }
//            });
//        }
    }

    private void initmWebView() {
        //设置支持JS
        mWebViewSettings.setJavaScriptEnabled(true);
        // 设置支持本地存储
        mWebViewSettings.setDatabaseEnabled(true);
        //取得缓存路径
        String path = getApplicationContext().getDir("cache", Context.MODE_PRIVATE).getPath();
        //设置路径
        mWebViewSettings.setDatabasePath(path);
        //设置支持DomStorage
        mWebViewSettings.setDomStorageEnabled(true);
        //设置存储模式
        mWebViewSettings.setCacheMode(WebSettings.LOAD_DEFAULT);
        //设置适应屏幕
        mWebViewSettings.setUseWideViewPort(true);
        mWebViewSettings.setLoadWithOverviewMode(true);
        mWebViewSettings.setSupportZoom(true);
        mWebViewSettings.setBuiltInZoomControls(true);
        mWebViewSettings.setDisplayZoomControls(false);
        //设置缓存
        mWebViewSettings.setAppCacheEnabled(true);
        mWebView.requestFocus();
        //下面三个各种监听
//        mWebView.setDownloadListener(dl);
        mWebView.setWebViewClient(new WebViewClient() {
            //系统默认会通过手机浏览器打开网页，为了能够直接通过WebView显示网页，则必须设置
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                // 默认返回：return super.shouldOverrideUrlLoading(view, url); 这个返回的方法会调用父类方法，也就是跳转至手机浏览器，平时写webview一般都在方法里面写 webView.loadUrl(url);  然后把这个返回值改成下面的false。
                //返回: return true;  webview处理url是根据程序来执行的。
                //返回: return false; webview处理url是在webview内部执行。
                view.loadUrl(url);
                return true;
            }

            // 网页加载初始化
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {

            }

            // 网页加载结束
            @Override
            public void onPageFinished(WebView view, String url) {

            }

            // 加载网页出现错误
            @Override
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {

            }
        });

        mWebView.setWebChromeClient(new WebChromeClient() {
            //通知应用程序当前网页加载的进度。
            @Override
            // newProgress 加载进度
            public void onProgressChanged(WebView view, int newProgress) {
                Log.d("webview", "progress: " + newProgress);
            }

            @Override
            public void onRequestFocus(WebView view) {
                Log.d("webview", "onRequestFocus()");
            }

            // 当关闭窗口时回调
            @Override
            public void onCloseWindow(WebView window) {
                super.onCloseWindow(window);
            }

            // webview中显示提示框回调 --处理网页alert弹窗
            @Override
            public boolean onJsAlert(
                    WebView view, String url, String message, JsResult result) {
                return super.onJsAlert(view, url, message, result);
            }

            // webview中显示对话框回调 --  一般这个并不常用(处理网页Confirm弹窗)
            @Override
            public boolean onJsConfirm(
                    WebView view, String url, String message, JsResult result) {
                return super.onJsConfirm(view, url, message, result);
            }

            @Override
            public boolean onJsBeforeUnload(
                    WebView view, String url, String message, JsResult result) {
                return super.onJsBeforeUnload(view, url, message, result);
            }

            // 文件选择框打开选择文件后回调
            @Override
            public boolean onShowFileChooser(WebView webView, ValueCallback<Uri[]> filePathCallback,
                                             FileChooserParams fileChooserParams) {
                return super.onShowFileChooser(webView, filePathCallback, fileChooserParams);
            }
        });
    }

    /**
     * 刷新
     *
     * @param view
     */
    public void doReload(View view) {
        mWebView.reload();
    }

    /**
     * 后退
     *
     * @param view
     */
    public void doBack(View view) {
        if (mWebView.canGoBack()) {
            mWebView.goBack();
        }
    }

    /**
     * 前进
     *
     * @param view
     */
    public void doForward(View view) {
        if (mWebView.canGoForward()) {
            //mWebView.goBackOrForward();
            mWebView.goForward();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        mWebView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mWebView.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mWebView.clearCache(true);
        mWebView.clearHistory();
        mWebView.clearFormData();
        mWebView.destroy();
    }
}

