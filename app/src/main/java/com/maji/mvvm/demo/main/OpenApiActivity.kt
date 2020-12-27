package com.maji.mvvm.demo.main

import android.content.Intent
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.elvishew.xlog.XLog
import com.maji.mvvm.demo.R
import com.maji.mvvm.demo.base.BaseActivity
import com.maji.mvvm.demo.dispatcher.TaskPeriod
import com.maji.mvvm.demo.dispatcher.TaskScheduler
import com.maji.mvvm.demo.main.adapter.OpenApiListAdapter
import com.maji.mvvm.demo.main.model.ItemInfo
import com.maji.mvvm.demo.main.viewmodel.OpenApiViewModel


/**
 * Github OpenApi List UI
 * <p>
 * Created by android_ls on 2020/12/25 21:33.
 *
 * @author android_ls
 * @version 1.0
 */
class OpenApiActivity : BaseActivity<OpenApiViewModel>() {

    private var mRecyclerView: RecyclerView? = null

    private val mOpenApiData = mutableListOf<ItemInfo>()
    private lateinit var mAdapter: OpenApiListAdapter

    private val startDelayMillisecond: Long = 5000L
    private val periodMillisecond: Long = 5000L
    private lateinit var mTaskPeriod: TaskPeriod

    override fun getContentLayoutRes(): Int {
        return R.layout.activity_main
    }

    override fun createViewModel(): OpenApiViewModel {
        return getViewModel<OpenApiViewModel>()
    }

    override fun setActionbar() {
        super.setActionbar()
        setActionBarBack(false)
        setActionBarTitle(getString(R.string.open_api_title))
        setActionBarRightText(getString(R.string.open_api_right_title), View.OnClickListener {
            val intent = Intent(this@OpenApiActivity, VisitorHistoryActivity::class.java)
            startActivity(intent)
        })
    }

    override fun setupViews() {
        super.setupViews()
        mRecyclerView = findViewById(R.id.recycler_view)
        mAdapter = OpenApiListAdapter(this@OpenApiActivity, mOpenApiData)
        mRecyclerView?.apply {
            layoutManager = LinearLayoutManager(this@OpenApiActivity, RecyclerView.VERTICAL, false)
            adapter = mAdapter
        }
    }

    override fun loadData() {
        XLog.i("-->5秒后开始执行")
        showLoading()
        // 5秒后执行，之后每隔5秒执行一次
        mTaskPeriod = TaskPeriod(startDelayMillisecond, periodMillisecond,
            true, object : TaskPeriod.OnTaskCallback {
                override fun run() {
                    XLog.i("-->getOpenApiList()")
                    mViewModel?.getOpenApiList()
                }
            }
        )
        TaskScheduler.start(mTaskPeriod)
    }

    override fun observeLiveData() {
        mViewModel.apply {
            getOpenApiLiveData().observe(this@OpenApiActivity, { result ->
                mAdapter.updateAdapterData(result)
                showContent()
            })

            getErrorMsgLiveData().observe(this@OpenApiActivity, { errorMsg ->
                showError(errorMsg)
            })
        }
    }

    override fun hasStatusLayout(): Boolean {
        return true
    }

    override fun onDestroy() {
        XLog.i("-->onDestroy()")
        TaskScheduler.stop(mTaskPeriod)
        super.onDestroy()
    }

}