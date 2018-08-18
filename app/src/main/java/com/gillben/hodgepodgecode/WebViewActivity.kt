package com.gillben.hodgepodgecode

import android.annotation.SuppressLint
import android.support.v7.app.AlertDialog
import android.webkit.JsResult
import android.webkit.WebChromeClient
import android.webkit.WebView
import com.gillben.hodgepodgecode.jsInterface.NativeMethodForJs
import kotlinx.android.synthetic.main.activity_web_view.*

//示例通过WebView使得Android与JS进行交互
class WebViewActivity : BaseActivity(){


    override fun getLayoutId(): Int {
        return R.layout.activity_web_view
    }

    @SuppressLint("SetJavaScriptEnabled")
    override fun initView() {
        webView.loadUrl("file:///android_asset/HelloJavaScript.html")
        val webSettings = webView.settings
        webSettings.javaScriptEnabled = true
        webSettings.javaScriptCanOpenWindowsAutomatically = true

        webView.addJavascriptInterface(NativeMethodForJs(),"nativeMethodForJs")

        invokeJSMethod.setOnClickListener {
            webView.loadUrl("javascript:helloJS('This is JavaScript page')")
        }
        webView.webChromeClient = MyWebChromeClient()
    }

    private inner class MyWebChromeClient: WebChromeClient(){
        override fun onJsAlert(view: WebView?, url: String?, message: String?, result: JsResult?): Boolean {
            val builder: AlertDialog.Builder = AlertDialog.Builder(this@WebViewActivity)
            builder.setTitle("调用JS方法").setMessage(message)
                    .setPositiveButton("Ok") { dialog, which ->
                        result!!.confirm()
                    }
                    .create()
                    .show()
            return true
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        webView.destroy()
    }

}