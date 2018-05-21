package com.rair.bluetoothdemo.client;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.os.ParcelUuid;
import android.util.Log;

import java.io.IOException;
import java.util.UUID;

public class ConnThread extends Thread {
    private BluetoothDevice device;
    private UUID uuid;
    private BluetoothSocket socket;

    public ConnThread(BluetoothDevice device) {
//        try {
//            BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();
//            Method getUuidsMethod = null;
//            getUuidsMethod = BluetoothAdapter.class.getDeclaredMethod("getUuids", null);
//            ParcelUuid[] uuids = (ParcelUuid[]) getUuidsMethod.invoke(adapter, null);
//            Log.d("qqq", "UUIDs:  " + uuids.length);
//            for (ParcelUuid uuid : uuids) {
//                Log.d("qqq", "UUID: " + uuid.getUuid().toString());
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//            Log.d("qqq", "UUID: 获取失败");
//        }


        this.device = device;
        ParcelUuid[] uuids = device.getUuids();
        if (uuids == null || uuids.length <= 0) {
            Log.d("qqq", "uuids为空了");
            return;
        }
        for (ParcelUuid id : uuids) {
            Log.e("qqq", id.getUuid() + "   id");
        }
        this.uuid = device.getUuids()[0].getUuid();
        Log.d("qqq", "ConnThread: "+uuid);
        try {
            socket =
                    device.createRfcommSocketToServiceRecord(this.uuid);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {

        try {
            //java.io.IOException: Service discovery failed
            // 连接蓝牙服务端
            socket.connect();

            // 发送内容
            socket.getOutputStream().write(new String("Hello").getBytes());

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }


    }
}
