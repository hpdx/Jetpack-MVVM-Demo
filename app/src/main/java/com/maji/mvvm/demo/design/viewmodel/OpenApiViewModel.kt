package com.maji.mvvm.demo.design.viewmodel

import android.text.TextUtils
import androidx.lifecycle.MutableLiveData
import com.elvishew.xlog.XLog
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.maji.mvvm.demo.base.BaseViewModel
import com.maji.mvvm.demo.base.model.HttpResult
import com.maji.mvvm.demo.base.model.getOrThrow
import com.maji.mvvm.demo.repository.database.MJAppDatabase
import com.maji.mvvm.demo.design.model.ApiInfo
import com.maji.mvvm.demo.design.model.ItemInfo
import com.maji.mvvm.demo.repository.GithubApiRepo
import com.maji.mvvm.demo.utils.DateUtils

/**
 *
 * <p>
 * Created by android_ls on 2020/12/25 21:33.
 *
 * @author android_ls
 * @version 1.0
 */
class OpenApiViewModel : BaseViewModel() {

    private val mOpenApiLiveData = MutableLiveData<MutableList<ItemInfo>>()
    private val mErrorMsgLiveData = MutableLiveData<String>()

    private val mLocalCacheLiveData = MutableLiveData<MutableList<ItemInfo>>()
    private val mNoLocalCacheLiveData = MutableLiveData<String>()

    private val mRepository = GithubApiRepo()

    fun getOpenApiLiveData(): MutableLiveData<MutableList<ItemInfo>> {
        return mOpenApiLiveData
    }

    fun getErrorMsgLiveData(): MutableLiveData<String> {
        return mErrorMsgLiveData
    }

    fun getLocalCacheLiveData(): MutableLiveData<MutableList<ItemInfo>> {
        return mLocalCacheLiveData
    }

    fun getNoLocalCacheLiveData(): MutableLiveData<String> {
        return mNoLocalCacheLiveData
    }

    fun getLocalData() {
        launchOnIO {
            val apiInfoDao = MJAppDatabase.getDatabase().apiInfoDao()
            val apiInfo = apiInfoDao.getLastApiInfo()
            if (apiInfo != null) {
                val gson = Gson()
                val resultData: MutableMap<String, String> = gson.fromJson(
                    apiInfo.content,
                    object : TypeToken<MutableMap<String, String>>() {}.type
                )

                val apiList = mutableListOf<ItemInfo>()
                for ((name, url) in resultData) {
                    apiList.add(ItemInfo(name, url))
                }
                mLocalCacheLiveData.postValue(apiList)
            } else {
                mNoLocalCacheLiveData.postValue("初次启动App，本地无缓存数据")
            }
        }
    }

    fun getOpenApiList() {
        launchOnIO {
            val result: HttpResult<MutableMap<String, String>> = mRepository.getOpenApiList()
            try {
                val resultData = result.getOrThrow()
                XLog.d("-->resultData.size = ${resultData.size}")
                if (resultData.isNotEmpty()) {
                    val apiList = mutableListOf<ItemInfo>()
                    for ((name, url) in resultData) {
                        apiList.add(ItemInfo(name, url))
                    }

                    XLog.d("-->apiList.size = ${apiList.size}")
                    mOpenApiLiveData.postValue(apiList)

                    // 将数据持久化到DB中
                    saveToDB(resultData)
                } else {
                    // 无数据
                    XLog.i("-->Empty Data")
                }
            } catch (e: Exception) {
                XLog.e("-->Exception message: ${e.message}")
                if (!TextUtils.isEmpty(e.message)) {
                    mErrorMsgLiveData.postValue(e.message)
                }
            }
        }
    }

    private fun saveToDB(mapData: MutableMap<String, String>) {
        val gson = Gson()
        val jsonData = gson.toJson(mapData)

        val apiUrl = "https://api.github.com/"
        val apiInfoDao = MJAppDatabase.getDatabase().apiInfoDao()
        val apiInfo = ApiInfo(apiUrl, jsonData, DateUtils.getCurrentDate())
        val saveResult = apiInfoDao.save(apiInfo)
        XLog.i("saveResult = $saveResult")
    }

    override fun onCleared() {
        super.onCleared()
        XLog.i("-->onCleared()")
    }

}