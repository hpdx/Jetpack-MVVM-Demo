package com.maji.mvvm.demo.main

import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.maji.mvvm.demo.R
import com.maji.mvvm.demo.base.BaseActivity
import com.maji.mvvm.demo.main.adapter.VisitorHistoryListAdapter
import com.maji.mvvm.demo.main.viewmodel.VisitorHistoryViewModel
import com.maji.mvvm.demo.main.model.ApiInfo

/**
 * OpenApi调用历史记录
 * <p>
 * Created by android_ls on 2020/12/27 18:44.
 *
 * @author android_ls
 * @version 1.0
 */
class VisitorHistoryActivity : BaseActivity<VisitorHistoryViewModel>() {

    private var mRecyclerView: RecyclerView? = null

    private val mVisitorHistoryData = mutableListOf<ApiInfo>()
    private lateinit var mAdapter: VisitorHistoryListAdapter

    override fun setActionbar() {
        super.setActionbar()
        setActionBarBack(true)
        setActionBarTitle(getString(R.string.visitor_history_title))
    }

    override fun getContentLayoutRes(): Int {
       return R.layout.activity_visitor_history
    }

    override fun createViewModel(): VisitorHistoryViewModel {
        return getViewModel()
    }

    override fun loadData() {
        showLoading()
        mViewModel.getVisitorRecordList()
    }

    override fun observeLiveData() {
        mViewModel.getVisitorRecordLiveData().observe(this@VisitorHistoryActivity, { result ->
            mAdapter.updateAdapterData(result)
            showContent()
        })
    }

    override fun hasStatusLayout(): Boolean {
        return true
    }

    override fun setupViews() {
        super.setupViews()
        mRecyclerView = findViewById(R.id.recycler_view)
        mAdapter = VisitorHistoryListAdapter(this@VisitorHistoryActivity, mVisitorHistoryData)
        mRecyclerView?.apply {
            layoutManager = LinearLayoutManager(this@VisitorHistoryActivity, RecyclerView.VERTICAL, false)
            adapter = mAdapter
        }
    }

}