package com.maji.mvvm.demo.design

import android.content.Intent
import android.os.CountDownTimer
import android.view.View
import androidx.activity.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.elvishew.xlog.XLog
import com.maji.mvvm.demo.R
import com.maji.mvvm.demo.base.BaseActivity
import com.maji.mvvm.demo.databinding.ActivityMainBinding
import com.maji.mvvm.demo.design.adapter.OpenApiListAdapter
import com.maji.mvvm.demo.design.model.ItemInfo
import com.maji.mvvm.demo.design.viewmodel.OpenApiViewModel
import com.maji.mvvm.demo.dispatcher.TaskPeriod
import com.maji.mvvm.demo.dispatcher.TaskScheduler


/**
 * Github OpenApi List UI
 * <p>
 * Created by android_ls on 2020/12/25 21:33.
 *
 * @author android_ls
 * @version 1.0
 */
class OpenApiActivity : BaseActivity() {

    private val mOpenApiData = mutableListOf<ItemInfo>()
    private lateinit var mAdapter: OpenApiListAdapter

    private val viewModel by viewModels<OpenApiViewModel>()

    private val mStartDelayMillisecond: Long = 0L
    private val mPeriodMillisecond: Long = 5000L
    private lateinit var mTaskPeriod: TaskPeriod

    private var mCountDownTimer: CountDownTimer? = null
    private var hasStartTask = false

    private lateinit var mLayout: ActivityMainBinding

    override fun getContentLayoutId(): Int = 0
    override fun getContentLayoutView() = ActivityMainBinding.inflate(layoutInflater).let {
        mLayout = it
        it.root
    }

    override fun setActionbar() {
        super.setActionbar()
        setActionBarBack(false)
        setActionBarTitle(getString(R.string.open_api_title))
        setActionBarRightText(getString(R.string.open_api_right_title)) {
            val intent = Intent(this@OpenApiActivity, VisitorHistoryActivity::class.java)
            startActivity(intent)
        }
    }

    override fun setupViews() {
        super.setupViews()
        mAdapter = OpenApiListAdapter(this@OpenApiActivity, mOpenApiData)
        mLayout.recyclerView.apply {
            layoutManager = LinearLayoutManager(this@OpenApiActivity, RecyclerView.VERTICAL, false)
            adapter = mAdapter
        }
    }

    override fun loadData() {
        showLoading()
        viewModel.getLocalData()
    }

    override fun observeLiveData() {
        viewModel.apply {
            getOpenApiLiveData().observe(this@OpenApiActivity) { result ->
                mAdapter.updateAdapterData(result)
                showContent()
            }

            getErrorMsgLiveData().observe(this@OpenApiActivity) { errorMsg ->
                XLog.i("-->showError")
                stopTask()
                showError(errorMsg)
            }

            getLocalCacheLiveData().observe(this@OpenApiActivity) { result ->
                // 本地有缓存数据，显示最后一次的调用结果
                mAdapter.updateAdapterData(result)
                showContent()

                XLog.i("-->startTask()")
                // 开始后每隔5秒都调用一次
                startTask()
            }

            getNoLocalCacheLiveData().observe(this@OpenApiActivity) {
                showContentNoAnim()

                XLog.i("-->startCountDown()")
                // 初次启动App，本地无缓存数据，开始5秒倒计时
                startCountDown()
            }
        }
    }

    /**
     * 开始5秒倒计时
     */
    private fun startCountDown() {
        mLayout.flCountDown.visibility = View.VISIBLE
        mLayout.tvCountDown.text = "倒计时:5秒"

        mCountDownTimer?.cancel()
        mCountDownTimer = object : CountDownTimer(5000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                val value = millisUntilFinished / 1000 + 1
                mLayout.tvCountDown.text = "倒计时:${value}秒"
            }

            override fun onFinish() {
                XLog.i("-->onFinish()")
                mLayout.flCountDown.visibility = View.GONE

                showLoading()
                XLog.i("-->startTask()")
                // 开始后每隔5秒都调用一次
                startTask()
            }
        }
        mCountDownTimer?.start()
    }

    /**
     * 初次立即执行，之后每隔5秒执行一次
     */
    private fun startTask() {
        mTaskPeriod = TaskPeriod(mStartDelayMillisecond, mPeriodMillisecond,
            true, object : TaskPeriod.OnTaskCallback {
                override fun run() {
                    XLog.d("-->getOpenApiList()")
                    viewModel.getOpenApiList()
                }
            }
        )
        hasStartTask = true
        TaskScheduler.start(mTaskPeriod)
    }

    private fun stopTask(){
        if (this::mTaskPeriod.isInitialized) {
            TaskScheduler.stop(mTaskPeriod)
            hasStartTask = false
        }
    }

    override fun hasStatusLayout(): Boolean {
        return true
    }

    override fun onResume() {
        super.onResume()
        if (this::mTaskPeriod.isInitialized && !hasStartTask) {
            TaskScheduler.start(mTaskPeriod)
        }
    }

    override fun onPause() {
        super.onPause()
        stopTask()
    }

    override fun onDestroy() {
        XLog.i("-->onDestroy()")
        mCountDownTimer?.cancel()
        TaskScheduler.stop(mTaskPeriod)
        hasStartTask = false
        super.onDestroy()
    }

}