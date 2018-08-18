package com.gillben.hodgepodgecode

import android.app.Activity
import android.content.Intent
import android.view.View
import kotlinx.android.synthetic.main.activity_main.*
import kotlin.reflect.KClass

class MainActivity : BaseActivity(), View.OnClickListener {

    override fun getLayoutId(): Int {
        return R.layout.activity_main
    }

    override fun initView() {
        jumpSlidePage.setOnClickListener(this)
        jumpWeb.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.jumpSlidePage -> startDirectActivity(SlidePageActivity::class)
            R.id.jumpWeb -> startDirectActivity(WebViewActivity::class)
        }
    }

    private fun <T : Activity> startDirectActivity(activity: KClass<T>) {
        val intent = Intent(this, activity.java)
        startActivity(intent)
    }
}
