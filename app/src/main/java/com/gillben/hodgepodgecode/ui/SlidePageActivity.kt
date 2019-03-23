package com.gillben.hodgepodgecode.ui

import android.support.v7.widget.LinearLayoutManager
import com.gillben.hodgepodgecode.R
import com.gillben.hodgepodgecode.adpter.RecyclerViewItemDivider
import com.gillben.hodgepodgecode.adpter.SlidePageRecyclerViewAdapter
import kotlinx.android.synthetic.main.activity_slide_page.*
import java.util.*

class SlidePageActivity : BaseActivity() {


    private lateinit var list: List<String>

    override fun getLayoutId(): Int {
        return R.layout.activity_slide_page
    }

    override fun initView() {
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
        val data: ArrayList<String> = arrayListOf()
        for (count in 0 until 30) {
            data.add("正文内容$count")
        }
        return data
    }


}