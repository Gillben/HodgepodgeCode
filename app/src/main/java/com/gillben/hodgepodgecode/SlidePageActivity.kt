package com.gillben.hodgepodgecode

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.widget.Toast
import com.gillben.hodgepodgecode.R.id.recyclerView
import com.gillben.hodgepodgecode.adpter.RecyclerViewItemDivider
import com.gillben.hodgepodgecode.adpter.SlidePageRecyclerViewAdapter

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
        val recyclerViewItemDivider = RecyclerViewItemDivider(this, 10)
        recyclerView.addItemDecoration(recyclerViewItemDivider)
        recyclerView.layoutManager = linearManager
        recyclerView.adapter = adapter
        adapter.setRecyclerItemCallback { view, position ->
            Toast.makeText(this, " $position", Toast.LENGTH_SHORT).show()
        }
        adapter.notifyData(list)
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