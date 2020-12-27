package com.maji.mvvm.demo.main

import android.content.Intent
import android.os.CountDownTimer
import android.view.View
import android.widget.FrameLayout
import android.widget.TextView
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

    private var flCountDown: FrameLayout? = null
    private var tvCountDown: TextView? = null
    private var mRecyclerView: RecyclerView? = null

    private val mOpenApiData = mutableListOf<ItemInfo>()
    private lateinit var mAdapter: OpenApiListAdapter

    private val mStartDelayMillisecond: Long = 0L
    private val mPeriodMillisecond: Long = 5000L
    private lateinit var mTaskPeriod: TaskPeriod

    private var mCountDownTimer: CountDownTimer? = null

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
        flCountDown = findViewById(R.id.fl_count_down)
        tvCountDown = findViewById(R.id.tv_count_down)
        mRecyclerView = findViewById(R.id.recycler_view)

        mAdapter = OpenApiListAdapter(this@OpenApiActivity, mOpenApiData)
        mRecyclerView?.apply {
            layoutManager = LinearLayoutManager(this@OpenApiActivity, RecyclerView.VERTICAL, false)
            adapter = mAdapter
        }
    }

    override fun loadData() {
        showLoading()
        mViewModel.getLocalData()
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

            getLocalCacheLiveData().observe(this@OpenApiActivity, { result ->
                // 本地有缓存数据，显示最后一次的调用结果
                mAdapter.updateAdapterData(result)
                showContent()

                XLog.i("-->startTask()")
                // 开始每隔5秒都调用一次
                startTask()
            })

            getNoLocalCacheLiveData().observe(this@OpenApiActivity, { message ->
                showContentNoAnim()

                XLog.i("-->startCountDown()")
                // 初次启动App，本地无缓存数据，开始5秒倒计时
                startCountDown()
            })
        }
    }

    /**
     * 开始5秒倒计时
     */
    private fun startCountDown() {
        flCountDown?.visibility = View.VISIBLE
        tvCountDown?.text = "倒计时:5秒"
        mCountDownTimer = object : CountDownTimer(5000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                val value = millisUntilFinished / 1000 + 1
                tvCountDown?.text = "倒计时:${value}秒"
            }

            override fun onFinish() {
                XLog.i("-->onFinish()")
                flCountDown?.visibility = View.GONE

                showLoading()
                XLog.i("-->startTask()")
                // 开始每隔5秒都调用一次
                startTask()
            }
        }.start()
    }

    /**
     * 初次立即执行，之后每隔5秒执行一次
     */
    private fun startTask() {
        mTaskPeriod = TaskPeriod(mStartDelayMillisecond, mPeriodMillisecond,
            true, object : TaskPeriod.OnTaskCallback {
                override fun run() {
                    XLog.i("-->getOpenApiList()")
                    mViewModel?.getOpenApiList()
                }
            }
        )
        TaskScheduler.start(mTaskPeriod)
    }

    override fun hasStatusLayout(): Boolean {
        return true
    }

    override fun onDestroy() {
        XLog.i("-->onDestroy()")
        mCountDownTimer?.cancel()
        TaskScheduler.stop(mTaskPeriod)
        super.onDestroy()
    }

}