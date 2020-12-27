### Jetpack中架构组件的使用示例


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
