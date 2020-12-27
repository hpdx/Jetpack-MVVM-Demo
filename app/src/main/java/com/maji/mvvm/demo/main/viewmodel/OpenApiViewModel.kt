package com.maji.mvvm.demo.main.viewmodel

import androidx.lifecycle.MutableLiveData
import com.elvishew.xlog.XLog
import com.google.gson.Gson
import com.maji.mvvm.demo.base.BaseViewModel
import com.maji.mvvm.demo.base.model.HttpResult
import com.maji.mvvm.demo.base.model.getOrThrow
import com.maji.mvvm.demo.dao.MJAppDatabase
import com.maji.mvvm.demo.main.model.ApiInfo
import com.maji.mvvm.demo.main.model.ItemInfo
import com.maji.mvvm.demo.repository.GithubApiRepository
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
    private val mRepository = GithubApiRepository()

    fun getOpenApiLiveData(): MutableLiveData<MutableList<ItemInfo>> {
        return mOpenApiLiveData
    }

    fun getOpenApiList() {
        launchOnIO {
            val result: HttpResult<MutableMap<String, String>> = mRepository.getOpenApiList()
            try {
                val resultData = result.getOrThrow()
                XLog.i("-->resultData.size = ${resultData.size}")
                if (resultData.isNotEmpty()) {
                    val apiList = mutableListOf<ItemInfo>()
                    for ((name, url) in resultData) {
                        apiList.add(ItemInfo(name, url))
                    }

                    XLog.i("-->apiList.size = ${apiList.size}")
                    mOpenApiLiveData.postValue(apiList)

                    // 将数据持久化到DB中
                    saveToDB(resultData)
                }
            } catch (e: Exception) {
                XLog.e("-->Exception message: ${e.message}")

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