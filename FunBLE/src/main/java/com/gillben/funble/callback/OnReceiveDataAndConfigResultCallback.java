package com.gillben.funble.callback;

/**
 * 接收数据和配置结果回调
 * Created by postech on 2017/12/12.
 */

public interface OnReceiveDataAndConfigResultCallback {
    void receiveData(String receiveData);
    void setupSucceed();
    void configTimeout();
}
