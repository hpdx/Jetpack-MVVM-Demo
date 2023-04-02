package com.maji.mvvm.demo.design

import androidx.activity.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.elvishew.xlog.XLog
import com.maji.mvvm.demo.R
import com.maji.mvvm.demo.base.BaseActivity
import com.maji.mvvm.demo.databinding.ActivityVisitorHistoryBinding
import com.maji.mvvm.demo.design.adapter.VisitorHistoryListAdapter
import com.maji.mvvm.demo.design.viewmodel.VisitorHistoryViewModel
import com.maji.mvvm.demo.design.model.ApiInfo

/**
 * OpenApi调用历史记录
 * TODO 分页暂未实现
 * <p>
 * Created by android_ls on 2020/12/27 18:44.
 *
 * @author android_ls
 * @version 1.0
 */
class VisitorHistoryActivity : BaseActivity() {

    private val mVisitorHistoryData = mutableListOf<ApiInfo>()

    private lateinit var mAdapter: VisitorHistoryListAdapter
    private lateinit var mLayout: ActivityVisitorHistoryBinding

    private val viewModel by viewModels<VisitorHistoryViewModel>()

    override fun setActionbar() {
        super.setActionbar()
        setActionBarBack(true)
        setActionBarTitle(getString(R.string.visitor_history_title))
    }

    override fun getContentLayoutId(): Int = 0
    override fun getContentLayoutView() =
        ActivityVisitorHistoryBinding.inflate(layoutInflater).let {
            mLayout = it
            it.root
        }

    override fun loadData() {
        showLoading()
        viewModel.getVisitorRecordList()
    }

    override fun observeLiveData() {
        viewModel.getVisitorRecordLiveData().observe(this) { data ->
            XLog.i("result.size = ${data.size}")
            if (data.size > 0) {
                mAdapter.updateAdapterData(data)
                hideEmpty()
            } else {
                // 空白页处理
                showEmpty("暂无访问历史记录")
            }
            showContent()
        }
    }

    override fun hasStatusLayout(): Boolean {
        return true
    }

    override fun setupViews() {
        super.setupViews()
        mAdapter = VisitorHistoryListAdapter(this@VisitorHistoryActivity, mVisitorHistoryData)
        mLayout.recyclerView.apply {
            layoutManager = LinearLayoutManager(
                this@VisitorHistoryActivity,
                RecyclerView.VERTICAL, false
            )
            adapter = mAdapter
        }
    }

}