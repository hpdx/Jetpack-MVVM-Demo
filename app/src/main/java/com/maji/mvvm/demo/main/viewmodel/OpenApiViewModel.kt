package com.maji.mvvm.demo.main.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.elvishew.xlog.XLog
import com.google.gson.Gson
import com.maji.mvvm.demo.dao.MJAppDatabase
import com.maji.mvvm.demo.service.IGithubApiService
import com.maji.mvvm.demo.main.model.ApiInfo
import com.maji.mvvm.demo.main.model.ItemInfo
import com.maji.mvvm.demo.utils.DateUtils
import com.maji.mvvm.demo.utils.ServiceCreator
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


/**
 *
 * <p>
 * Created by android_ls on 2020/12/25 21:33.
 *
 * @author android_ls
 * @version 1.0
 */
class OpenApiViewModel : ViewModel() {

    val mApiLiveData = MutableLiveData<MutableList<ItemInfo>>()

    fun getOpenApiList() {
        GlobalScope.launch(Dispatchers.Main) {
            withContext(Dispatchers.IO) {
                val service = ServiceCreator.create<IGithubApiService>()
                val resultData = service.getOpenApiList().await()
                XLog.i("-->resultData.size = ${resultData.size}")
                if (resultData.isNotEmpty()) {
                    val apiList = mutableListOf<ItemInfo>()
                    for ((name, url) in resultData) {
                        apiList.add(ItemInfo(name, url))
                    }

                    XLog.i("-->apiList.size = ${apiList.size}")
                    mApiLiveData.postValue(apiList)

                    // 将数据持久化到DB中
                    saveToDB(resultData)
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

}