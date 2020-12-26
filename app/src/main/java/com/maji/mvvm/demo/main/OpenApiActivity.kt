package com.maji.mvvm.demo.main

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.elvishew.xlog.XLog
import com.maji.mvvm.demo.R
import com.maji.mvvm.demo.dispatcher.TaskPeriod
import com.maji.mvvm.demo.dispatcher.TaskScheduler
import com.maji.mvvm.demo.main.adapter.OpenApiListAdapter
import com.maji.mvvm.demo.main.viewmodel.OpenApiViewModel
import com.maji.mvvm.demo.main.model.ItemInfo


/**
 * Github OpenApi List UI
 * <p>
 * Created by android_ls on 2020/12/25 21:33.
 *
 * @author android_ls
 * @version 1.0
 */
class OpenApiActivity : AppCompatActivity() {

    private var mRecyclerView: RecyclerView? = null

    private val mOpenApiData = mutableListOf<ItemInfo>()
    private lateinit var mAdapter: OpenApiListAdapter
    private lateinit var viewModel: OpenApiViewModel

    private val startDelayMillisecond: Long = 5000L
    private val periodMillisecond: Long = 5000L
    private lateinit var mTaskPeriod: TaskPeriod

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mRecyclerView = findViewById(R.id.recycler_view)

        mAdapter = OpenApiListAdapter(this@OpenApiActivity, mOpenApiData)
        mRecyclerView?.apply {
            layoutManager = LinearLayoutManager(this@OpenApiActivity, RecyclerView.VERTICAL, false)
            adapter = mAdapter
        }

        viewModel = ViewModelProvider(this)[OpenApiViewModel::class.java]
        viewModel.mApiLiveData.observe(this, { result ->
            mAdapter.updateAdapterData(result)
        })

        XLog.i("-->5秒后开始执行")
        // 5秒后执行，之后每隔5秒执行一次
        mTaskPeriod = TaskPeriod(startDelayMillisecond, periodMillisecond,
            true, object : TaskPeriod.OnTaskCallback {
                override fun run() {
                    XLog.i("-->getOpenApiList()")
                    viewModel?.getOpenApiList()
                }
            }
        )
        TaskScheduler.start(mTaskPeriod)
    }

    override fun onDestroy() {
        XLog.i("-->onDestroy()")
        TaskScheduler.stop(mTaskPeriod)
        super.onDestroy()
    }

}