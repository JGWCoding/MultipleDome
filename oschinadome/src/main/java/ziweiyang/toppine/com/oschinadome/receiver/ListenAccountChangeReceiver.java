package ziweiyang.toppine.com.oschinadome.receiver;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import ziweiyang.toppine.com.oschinadome.utils.Constants;
import ziweiyang.toppine.com.oschinadome.utils.TLog;


/**
 * 这个为动态注册的广播,监听账号变化的情况
 */

public class ListenAccountChangeReceiver extends BroadcastReceiver {
    public static final String TAG = ListenAccountChangeReceiver.class.getSimpleName();
    private Service service;

    private ListenAccountChangeReceiver(Service service) {
        this.service = service;
    }

    public static ListenAccountChangeReceiver start(Service service) {
        TLog.d(TAG, "start: " + service);
        ListenAccountChangeReceiver receiveBroadCast = new ListenAccountChangeReceiver(service);
        IntentFilter filter = new IntentFilter();
        filter.addAction(Constants.INTENT_ACTION_LOGOUT);
        service.registerReceiver(receiveBroadCast, filter);
        return receiveBroadCast;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        TLog.d(TAG, "onReceive: " + service);
        if (service != null)
            service.stopSelf();
    }

    public void destroy() {
        TLog.d(TAG, "destroy: " + service);
        if (service != null) {
            service.unregisterReceiver(this);
            service = null;
        }
    }
}
