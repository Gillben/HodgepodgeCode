package com.gillben.hodgepodgecode

import android.annotation.SuppressLint
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() ,View.OnClickListener{


    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initView()
    }

    private fun initView() {
        jumpSlidePage.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when(v!!.id){
            R.id.jumpSlidePage -> startActivity()
        }
    }

    private fun startActivity(){
        val intent = Intent(this, SlidePageActivity::class.java)
        startActivity(intent)
    }
}
