package com.gillben.funble.callback;

import android.bluetooth.BluetoothDevice;

public interface OnBleScanResultCallback {
    void scanBleDevice(BluetoothDevice bleDevice);
    void scanError(String error);
}