package com.maji.mvvm.demo.main.viewmodel

import androidx.lifecycle.MutableLiveData
import com.maji.mvvm.demo.base.BaseViewModel
import com.maji.mvvm.demo.dao.MJAppDatabase
import com.maji.mvvm.demo.main.model.ApiInfo

/**
 *
 * <p>
 * Created by android_ls on 2020/12/27 18:46.
 *
 * @author android_ls
 * @version 1.0
 */
class VisitorHistoryViewModel : BaseViewModel() {

    private val mVisitorRecordLiveData = MutableLiveData<MutableList<ApiInfo>>()

    fun getVisitorRecordLiveData(): MutableLiveData<MutableList<ApiInfo>> {
        return mVisitorRecordLiveData
    }

    fun getVisitorRecordList() {
        launchOnIO {
            val apiInfoDao = MJAppDatabase.getDatabase().apiInfoDao()
            val resultData = apiInfoDao.loadApiList()
            mVisitorRecordLiveData.postValue(resultData)
        }
    }

}