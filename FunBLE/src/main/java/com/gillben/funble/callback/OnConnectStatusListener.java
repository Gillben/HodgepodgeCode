package com.gillben.funble.callback;

/**
 * BLE设备连接状态回调
 * Created by postech on 2017/11/29.
 */

public interface OnConnectStatusListener {
    void connecting();
    void connected();
    void disconnected();
    void connectFailed(String errorReason);
}
