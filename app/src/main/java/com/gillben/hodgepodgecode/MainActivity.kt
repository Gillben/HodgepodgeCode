package com.gillben.hodgepodgecode

import android.app.Activity
import android.content.Intent
import android.support.v7.app.AlertDialog
import android.util.Log
import android.view.View
import kotlinx.android.synthetic.main.activity_main.*
import kotlin.reflect.KClass

class MainActivity : BaseActivity(), View.OnClickListener {

    private val logtext = "MainActivity"

    override fun getLayoutId(): Int {
        return R.layout.activity_main
    }

    override fun initView() {
        jumpSlidePage.setOnClickListener(this)
        jumpWeb.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.jumpSlidePage ->   openDialog()  //startDirectActivity(SlidePageActivity::class)
            R.id.jumpWeb -> startDirectActivity(WebViewActivity::class)
        }
    }

    private fun <T : Activity> startDirectActivity(activity: KClass<T>) {
        val intent = Intent(this, activity.java)
        startActivity(intent)
    }


    override fun onStart() {
        super.onStart()
        Log.e(logtext,"onStart")
    }

    override fun onPause() {
        super.onPause()
        Log.e(logtext,"onPause")
    }

    override fun onStop() {
        super.onStop()
        Log.e(logtext,"onStop")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.e(logtext,"onDestroy")
    }

    private fun openDialog(){
        val mDialog = AlertDialog.Builder(this)
                .setTitle("dialog")
                .create()
        mDialog.show()


    }
}
