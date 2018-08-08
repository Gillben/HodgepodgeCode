package com.gillben.hodgepodgecode

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import com.gillben.hodgepodgecode.adpter.RecyclerViewItemDivider
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
        val mAdapter = SlidePageRecyclerViewAdapter(this)
        val linearManager = LinearLayoutManager(this)
        val recyclerViewItemDivider = RecyclerViewItemDivider(this, 10)
        mRecyclerView.layoutManager = linearManager
        mRecyclerView.addItemDecoration(recyclerViewItemDivider)
        mRecyclerView.adapter = mAdapter
        mAdapter.notifyData(list)
    }

    private fun initData(): List<String> {
        var count = 0
        val data: ArrayList<String> = arrayListOf()
        while (count <= 30) {
            data.add("正文内容$count")
            count++
        }
        return data
    }


}