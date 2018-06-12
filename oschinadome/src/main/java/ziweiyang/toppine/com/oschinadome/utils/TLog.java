package ziweiyang.toppine.com.oschinadome.utils;

import android.text.TextUtils;
import android.util.Log;

import java.util.Formatter;

import ziweiyang.toppine.com.oschinadome.BuildConfig;

//这个log只会在debug模式下进行输出
public class TLog {
    private static final String LOG_TAG = "OSChinaLog : ";
    private static boolean DEBUG = BuildConfig.DEBUG;

    private TLog() {
    }

    public static void error(String log) {
        if (DEBUG && !TextUtils.isEmpty(log)) Log.e(LOG_TAG, log);
    }

    public static void log(String log) {
        if (DEBUG && !TextUtils.isEmpty(log)) Log.i(LOG_TAG, log);
    }

    public static void log(String tag, String log) {
        if (DEBUG && !TextUtils.isEmpty(tag) && !TextUtils.isEmpty(log)) Log.i(tag, log);
    }

    public static void d(String tag, String log) {
        if (DEBUG && !TextUtils.isEmpty(tag) && !TextUtils.isEmpty(log)) Log.d(tag, log);
    }

    public static void i(String tag, String log) {
        if (DEBUG && !TextUtils.isEmpty(tag) && !TextUtils.isEmpty(log)) Log.i(tag, log);
    }

    public static void e(String tag, String log) {
        if (DEBUG && !TextUtils.isEmpty(tag) && !TextUtils.isEmpty(log)) Log.e(tag, log);
    }

    public static void e(String log) {

        StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
        String fileName = stackTrace[5].getFileName();
         fileName = fileName.substring(0, fileName.indexOf("."));
        String logTag = new Formatter()//严格按（FileName:LineNuber）的格式来写 才可以定位
                .format(LOG_TAG + "Thread: %s, %s`(%s:%d)",
                        Thread.currentThread().getName(),
                        stackTrace[5].getMethodName(),
                        fileName,
                        stackTrace[5].getLineNumber())
                .toString();
        if (TextUtils.isEmpty(log)) {
            log = "null";
        }
        if (DEBUG && !TextUtils.isEmpty(log)) Log.e(logTag, log);
    }
}
