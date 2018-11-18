package com.gillben.funble;

import android.annotation.TargetApi;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.content.Context;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;


import com.gillben.funble.callback.OnBleScanResultCallback;
import com.gillben.funble.callback.OnConnectStatusListener;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


/**
 * BLE工具,检测是否支持BLE设备，搜索连接，断开
 */

public class BLEManager {

    private static final String TAG = "BLEManager";
    private static final UUID SERVICE_UUID = UUID.fromString("0000fff0-0000-1000-8000-00805f9b34fb");
    private static final UUID READ_NOTIFY_UUID = UUID.fromString("0000fff1-0000-1000-8000-00805f9b34fb");
    private static final UUID WRITE_UUID = UUID.fromString("0000fff2-0000-1000-8000-00805f9b34fb");
    private static final UUID DESCRIPTION_UUID = UUID.fromString("00002902-0000-1000-8000-00805f9b34fb");

    private Context mContext;
    private final Handler mHandler = new Handler(Looper.getMainLooper());
    private boolean isScanning = false;    //是否正在搜索蓝牙
    private List<BluetoothDevice> filterList = new ArrayList<>();
    private BluetoothAdapter mBlueAdapter;
    private BluetoothGatt mBluetoothGatt;
    private ScanCallback scanCallback;
    private BluetoothGattCharacteristic writeCharacteristic;

    private static final int SCAN_TIME_SECOND = 10000;
    private volatile boolean CONNECTED = false;
    private volatile boolean CONNECTING = false;
    private ReConnectionThread reConnectionThread;

    private OnBleScanResultCallback bleScanResultCallback;
    private OnConnectStatusListener connectStatusListener;


    private BLEManager() {

    }

    public static BLEManager getInstance() {
        return BluetoothInstance.BLE_MANAGER;
    }


    public void init(Context context) {
        this.mContext = context;
        BluetoothManager mBlueManager = (BluetoothManager) context.getSystemService(Context.BLUETOOTH_SERVICE);
        if (mBlueManager != null) {
            mBlueAdapter = mBlueManager.getAdapter();
        }
    }

    /**
     * 是否支持蓝牙
     *
     * @return true 表示支持
     */
    private boolean isSupportBluetooth() {
        return mBlueAdapter != null;
    }


    /**
     * 开启蓝牙
     *
     * @return true表示开启
     */
    public boolean enable() {
        return mBlueAdapter != null && mBlueAdapter.enable();
    }

    /**
     * 关闭蓝牙
     *
     * @return true表示关闭
     */
    public boolean disable() {

        return mBlueAdapter != null && mBlueAdapter.isEnabled() && mBlueAdapter.disable();
    }


    /**
     * 扫描BLE设备
     */
    public void ScanBleToggle(boolean enable, OnBleScanResultCallback resultCallback) {
        setBleScanResultCallback(resultCallback);

        if (!isSupportBluetooth() || !mBlueAdapter.isEnabled()) {
            if (bleScanResultCallback != null)
                bleScanResultCallback.scanError("BluetoothAdapter#isEnabled() = false");
            return;
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            final BluetoothLeScanner bluetoothLeScanner = mBlueAdapter.getBluetoothLeScanner();
            if (scanCallback == null) {
                scanCallback = createScanCallback();
            }
            if (enable) {
                if (!isScanning) {
                    mHandler.postDelayed(new Runnable() {
                        @TargetApi(Build.VERSION_CODES.LOLLIPOP)
                        @Override
                        public void run() {
                            cleanFilterListAndStopScan();
                            bluetoothLeScanner.stopScan(scanCallback);
                        }
                    }, SCAN_TIME_SECOND);
                    isScanning = true;
                    bluetoothLeScanner.startScan(scanCallback);
                }
            } else {
                cleanFilterListAndStopScan();
                bluetoothLeScanner.stopScan(scanCallback);
            }
        }
        //手机版本在4.3 -- 4.4的搜索方式
        else {
            if (enable) {
                if (!isScanning) {
                    mHandler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            cleanFilterListAndStopScan();
                            mBlueAdapter.stopLeScan(leScanCallback);
                        }
                    }, SCAN_TIME_SECOND);
                    isScanning = true;
                    mBlueAdapter.startLeScan(leScanCallback);
                }
            } else {
                cleanFilterListAndStopScan();
                mBlueAdapter.stopLeScan(leScanCallback);
            }
        }
    }

    //SDK版本 4.3 -- 5.0
    private BluetoothAdapter.LeScanCallback leScanCallback = new BluetoothAdapter.LeScanCallback() {
        @Override
        public void onLeScan(final BluetoothDevice device, int rssi, byte[] scanRecord) {
            filterBleDevice(device);
        }
    };


    //SDK版本 5.0以上
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private ScanCallback createScanCallback() {
        return scanCallback = new ScanCallback() {
            @Override
            public void onScanResult(int callbackType, ScanResult result) {
                BluetoothDevice device = result.getDevice();
                filterBleDevice(device);
            }

            @Override
            public void onScanFailed(int errorCode) {
                Log.e(TAG, "onScanFailed: " + errorCode);
                filterList.clear();
            }
        };
    }

    /**
     * 过滤设备
     *
     * @param device 设备
     */
    private void filterBleDevice(final BluetoothDevice device) {
        if (!filterList.contains(device)) {
            filterList.add(device);
            if (bleScanResultCallback != null) {
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        bleScanResultCallback.scanBleDevice(device);
                    }
                });
            }
        }
    }

    /**
     * 清空过滤容器，并改变搜索状态
     */
    private void cleanFilterListAndStopScan() {
        isScanning = false;
        if (filterList.size() > 0) {
            filterList.clear();
        }
    }


    /**
     * Connecting ble device
     *
     * @param macAddress BLE device mac address
     */
    public boolean connectBleDevice(String macAddress, boolean autoConnect) {
        if (CONNECTED || CONNECTING) {
            return true;
        }

        if (!isSupportBluetooth() || !mBlueAdapter.isEnabled() || !BluetoothAdapter.checkBluetoothAddress(macAddress)) {
            if (connectStatusListener != null) {
                connectStatusListener.connectFailed("mBlueAdapter：" + mBlueAdapter +
                        " -or- BluetoothAdapter#isEnabled()：" + mBlueAdapter.isEnabled() + " -or- Bluetooth address invalid");
            }
            return false;
        }

        BluetoothDevice mBluetoothDevice = mBlueAdapter.getRemoteDevice(macAddress);
        if (mBluetoothDevice == null) {
            if (connectStatusListener != null) {
                connectStatusListener.connectFailed("BluetoothDevice undefined");
            }
            return false;
        }

        if (mBluetoothGatt != null) {
            mBluetoothGatt.close();
            mBluetoothGatt.disconnect();
            mBluetoothGatt = null;
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            mBluetoothGatt = mBluetoothDevice.connectGatt(mContext,
                    autoConnect, bluetoothGattCallback, BluetoothDevice.TRANSPORT_LE);
        } else {
            mBluetoothGatt = mBluetoothDevice.connectGatt(mContext, autoConnect, bluetoothGattCallback);
        }
        isConnectedOrConnecting(true, false);
        if (connectStatusListener != null) {
            connectStatusListener.connecting();
        }

        //在5秒内没有连接 表示失败
        if (reConnectionThread == null) {
            reConnectionThread = new ReConnectionThread();
        }
        mHandler.postDelayed(reConnectionThread, 5000);

        return true;
    }

    /**
     * 重新连接 Runnable
     */
    private class ReConnectionThread implements Runnable {

        @Override
        public void run() {
            isConnectedOrConnecting(false, false);
            if (connectStatusListener != null) {
                connectStatusListener.connectFailed("Connection timeout");
            }
        }
    }

    /**
     * Disconnect BLE device
     */
    public void disconnectBleDevice() {
        if (mBluetoothGatt != null) {
            mBluetoothGatt.disconnect();
        }
    }


    /**
     * connectGatt 回调
     */
    private BluetoothGattCallback bluetoothGattCallback = new BluetoothGattCallback() {
        @Override
        public void onConnectionStateChange(final BluetoothGatt gatt, int status, int newState) {
            super.onConnectionStateChange(gatt, status, newState);
            if (status == 133) {
                gatt.close();
                return;
            }
            if (newState == BluetoothProfile.STATE_CONNECTED) {
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        //先移除超时连接
                        if (reConnectionThread != null) {
                            mHandler.removeCallbacks(reConnectionThread);
                            reConnectionThread = null;
                        }
                        isConnectedOrConnecting(false, true);
                        if (connectStatusListener != null) {
                            connectStatusListener.connected();
                        }
                        gatt.discoverServices();
                    }
                });
            } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        isConnectedOrConnecting(false, false);
                        if (connectStatusListener != null) {
                            connectStatusListener.disconnected();
                        }
                    }
                });
            }
        }

        @Override
        public void onServicesDiscovered(final BluetoothGatt gatt, int status) {
            super.onServicesDiscovered(gatt, status);
            if (status == BluetoothGatt.GATT_SUCCESS) {
                BluetoothGattService gattService = mBluetoothGatt.getService(SERVICE_UUID);
                final BluetoothGattCharacteristic readCharacteristic = gattService.getCharacteristic(READ_NOTIFY_UUID);
                writeCharacteristic = gattService.getCharacteristic(WRITE_UUID);
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        setNotifyReadDataFromDevice(gatt, readCharacteristic);
                    }
                });
            }
        }

        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
            analysisCharacteristic(characteristic);
        }
    };

    /**
     * 分析通知信息
     *
     * @param characteristic 特征内容
     */
    private void analysisCharacteristic(BluetoothGattCharacteristic characteristic) {
        String convertData = new String(characteristic.getValue());
        //TODO 接收数据处理
    }

    /**
     * 销毁资源
     */
    public void destroy() {
        ScanBleToggle(false, null);
        if (mBluetoothGatt != null) {
            mBluetoothGatt.disconnect();
        }
        if (mBluetoothGatt != null) {
            refreshBleDeviceCache(mBluetoothGatt);
        }
        if (mBluetoothGatt != null) {
            mBluetoothGatt.close();
            mBluetoothGatt = null;
        }
        filterList.clear();
        CONNECTED = false;
        removeBleScanResultCallback();
        removeOnConnectStatusListener();
        mHandler.removeCallbacksAndMessages(null);
    }


    private void refreshBleDeviceCache(BluetoothGatt bluetoothGatt) {
        try {
            Method refresh = BluetoothGatt.class.getMethod("refresh");
            refresh.invoke(bluetoothGatt);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void isConnectedOrConnecting(boolean connecting, boolean connected) {
        this.CONNECTING = connecting;
        this.CONNECTED = connected;
    }

    /**
     * 设置通知，读取数据的关键
     */
    private void setNotifyReadDataFromDevice(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
        if (gatt != null && characteristic != null) {
            BluetoothGattDescriptor descriptor = characteristic.getDescriptor(DESCRIPTION_UUID);
            descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
            gatt.writeDescriptor(descriptor);
            gatt.setCharacteristicNotification(characteristic, true);
        } else {
            Log.e(TAG, "setNotifyReadDataFromDevice: mBluetoothGatt is null or readCharacteristic is null");
        }
    }

    /**
     * 发送数据
     *
     * @param string 发送的内容
     */
    public void writeDataToBleDevice(String string) {
        if (CONNECTED) {
            if (mBluetoothGatt != null && writeCharacteristic != null && string != null && string.length() > 0) {
                writeCharacteristic.setValue(string.getBytes());
                mBluetoothGatt.writeCharacteristic(writeCharacteristic);
            } else {
                Log.e(TAG, "writeDataToBleDevice: mBluetoothGatt is null or writeCharacteristic is null or data is null");
            }
        } else {
            Log.e(TAG, "writeDataToBleDevice: No connection ble_device.");
        }
    }


    private void setBleScanResultCallback(OnBleScanResultCallback callback) {
        if (bleScanResultCallback != null) {
            bleScanResultCallback = null;
        }
        this.bleScanResultCallback = callback;
    }

    public void setOnConnectStatusListener(OnConnectStatusListener listener) {
        if (connectStatusListener != null) {
            connectStatusListener = null;
        }
        this.connectStatusListener = listener;
    }

    private void removeBleScanResultCallback() {
        if (bleScanResultCallback != null) {
            bleScanResultCallback = null;
        }
    }

    private void removeOnConnectStatusListener() {
        if (connectStatusListener != null) {
            connectStatusListener = null;
        }
    }


    private static class BluetoothInstance {
        private static final BLEManager BLE_MANAGER = new BLEManager();
    }

}
