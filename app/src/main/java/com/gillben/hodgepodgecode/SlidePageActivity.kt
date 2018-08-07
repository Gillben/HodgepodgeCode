package com.gillben.hodgepodgecode

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.widget.Toast
import com.gillben.hodgepodgecode.adpter.SlidePageRecyclerViewAdapter
import kotlinx.android.synthetic.main.activity_slide_page.*

class SlidePageActivity : AppCompatActivity() {

    private lateinit var list: List<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_slide_page)
        initView()
    }

    private fun initView() {
        list = initData()
        val adapter = SlidePageRecyclerViewAdapter(this)
        val linearManager = LinearLayoutManager(this)
        recyclerView.layoutManager = linearManager
        recyclerView.adapter = adapter
        adapter.setRecyclerItemCallback { view, position ->
            Toast.makeText(this, " $position",Toast.LENGTH_SHORT).show()
        }
        adapter.notifyData(list)
    }

    private fun initData(): List<String> {
        var count = 30
        val data: ArrayList<String> = arrayListOf()
        while (count > 0) {
            data.add("正文内容$count")
            count--
        }
        return data
    }


}