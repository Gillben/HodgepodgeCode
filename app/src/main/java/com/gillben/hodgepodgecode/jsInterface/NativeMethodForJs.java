package com.gillben.hodgepodgecode.jsInterface;

import android.webkit.JavascriptInterface;

public class NativeMethodForJs {

    @JavascriptInterface
    public void invokeJavaMethodOnJS(){
        System.out.println("在JS中成功调用该方法");
    }
}
