### AAC架构组件的使用示例


##### 使用到的知识点
1.Lifecycle、LiveData和ViewModel的基本使用

2.基于Retrofit + Coroutines的网络请求的使用

3.Room的基本使用

4.协程：是由Kotlin官方提供的多线程框架，它可以用看起来同步的方式写出异步执行的代码，降低了多任务并发的实现难度。

在Kotlin中协程是基于线程进行封装后的一套API，与线程池的功能类似。

`suspend` 关键字只是提到提醒作用，告诉函数的调用者我这是个耗时操作，只能在协程的作用域内调用。

`withContext(Dispatchers.IO){}`切到子线程执行任务，执行完成再切回来。

```
 GlobalScope.launch(Dispatchers.Main) {
     withContext(Dispatchers.IO) {
         // 在子线程执行
     }
     // 在主线程执行
 }
```

##### [推荐]基于Retrofit + Coroutines的网络请求

```
interface IGithubApiService {

    @GET("https://api.github.com")
    fun getOpenApiList(): Call<MutableMap<String, String>>

}
```

```
class GithubApiRepository : BaseRepository() {

    /**
     * 获取github open api列表数据
     */
    suspend fun getOpenApiList(): HttpResult<MutableMap<String, String>> {
        val service = ServiceCreator.create<IGithubApiService>()
        return service.getOpenApiList().awaitResult()
    }

}
```

```
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

}
```
想要了解更多实现的细节，请查阅完整的代码


##### 基于Retrofit的网络请求
```
 @GET("https://api.github.com")
 fun getOpenApiList(): Call<MutableMap<String, String>>

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

```


##### 基于Retrofit + Coroutines的网络请求
```
  @GET("https://api.github.com")
  fun getOpenApiList(): Deferred<MutableMap<String, String>>

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
```


### License
```
Copyright (C) 2020 android_ls@163.com

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.

```
