### AAC架构组件的使用示例

1、Lifecycle、LiveData和ViewModel的基本使用
2、基于Retrofit + Coroutines的网络请求的使用
3、Room的基本使用


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
