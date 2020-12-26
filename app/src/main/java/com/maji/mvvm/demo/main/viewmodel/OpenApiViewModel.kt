package com.maji.mvvm.demo.main.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.elvishew.xlog.XLog
import com.google.gson.Gson
import com.maji.mvvm.demo.dao.MJAppDatabase
import com.maji.mvvm.demo.dispatcher.DispatcherTask
import com.maji.mvvm.demo.service.IGithubApiService
import com.maji.mvvm.demo.main.model.ApiInfo
import com.maji.mvvm.demo.main.model.ItemInfo
import com.maji.mvvm.demo.utils.DateUtils
import com.maji.mvvm.demo.utils.ServiceCreator
import retrofit2.Callback
import retrofit2.Response


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
        val service = ServiceCreator.create(IGithubApiService::class.java)
        service.getOpenApiList().enqueue(object : Callback<MutableMap<String, String>> {
            override fun onFailure(call: retrofit2.Call<MutableMap<String, String>>, t: Throwable) {
                XLog.i("-->onFailure message:" + t.message)

            }

            override fun onResponse(
                call: retrofit2.Call<MutableMap<String, String>>,
                response: Response<MutableMap<String, String>>
            ) {
                // retrofit2 回调的结果是在主线程中
                if (response.isSuccessful) {
                    val resultData = response.body()
                    resultData?.let { map ->
                        if (map.isNotEmpty()) {
                            val apiList = mutableListOf<ItemInfo>()
                            for ((name, url) in map) {
                                apiList.add(ItemInfo(name, url))
                            }

                            XLog.i("-->apiList.size = ${apiList.size}")
                            mApiLiveData.postValue(apiList)

                            // 将数据持久化到DB中
                            saveToDB(map)
                        }
                    }
                } else {
                    // rate limit exceeded
                    XLog.i("-->onResponse message:" + response.message())

                }
            }
        })
    }

    private fun saveToDB(mapData: MutableMap<String, String>) {
        DispatcherTask.runOnDiskIO(Runnable {
            val gson = Gson()
            val jsonData = gson.toJson(mapData)

            val apiUrl = "https://api.github.com/"
            val apiInfoDao = MJAppDatabase.getDatabase().apiInfoDao()
            val apiInfo = ApiInfo(apiUrl, jsonData, DateUtils.getCurrentDate())
            val saveResult = apiInfoDao.save(apiInfo)
            XLog.i("saveResult = $saveResult")
        })
    }

}