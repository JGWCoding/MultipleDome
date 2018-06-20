package ziweiyang.toppine.com.oschinadome.utils;

import android.content.Context;
import android.os.Looper;
import android.view.Gravity;
import android.widget.Toast;

public class SimplexToast {

    private static Toast mToast;
    private static long nextTimeMillis;
    private static int yOffset;

    private SimplexToast(Context context) {
    }

    public static Toast init(Context context) {
        if (context == null) {
            throw new IllegalArgumentException("Context should not be null!!!");
        }
        if (mToast == null) {
            mToast = Toast.makeText(context, null, Toast.LENGTH_SHORT);
            yOffset = mToast.getYOffset();
        }
        mToast.setDuration(Toast.LENGTH_SHORT);
        mToast.setGravity(Gravity.BOTTOM, 0, yOffset);
        mToast.setMargin(0, 0);
        return mToast;
    }

    public static void show(String content) {
        show(content, Toast.LENGTH_SHORT);
    }

    public static void show(String content, int duration) {
        show(null, content, Gravity.BOTTOM, duration);
    }

    public static void show(Context context, int rid) {
        show(context, context.getResources().getString(rid));
    }

    public static void show(Context context, String content) {
        show(context, content, Gravity.BOTTOM);
    }

    public static void show(Context context, String content, int gravity) {
        show(context, content, gravity, Toast.LENGTH_SHORT);
    }

    public static void show(final Context context, final String content, final int gravity, final int duration) {
//        Runnable r = new Runnable(){
//            @Override
//            public void run() {
//                long current = System.currentTimeMillis();
//                //if (current < nextTimeMillis) return;     //防止toast频繁,但不大需要
//                if (mToast == null) init(context.getApplicationContext());
//                mToast.setText(content);
//                mToast.setDuration(duration);
//                mToast.setGravity(gravity, 0, yOffset);
//                nextTimeMillis = current + (duration == Toast.LENGTH_LONG ? 3500 : 2000);
//                mToast.show();
//            }
//        };
        TLog.e("Toast content:"+content);
//        ThreadProxy.runMainThread(r);

        long current = System.currentTimeMillis();
        if (mToast == null) init(context.getApplicationContext());
        mToast.setText(content);
        mToast.setDuration(duration);
        mToast.setGravity(gravity, 0, yOffset);
        Looper.prepare();
        mToast.show();
        Looper.loop();
    }

}
