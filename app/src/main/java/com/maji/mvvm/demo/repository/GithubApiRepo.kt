package com.maji.mvvm.demo.repository

import com.maji.mvvm.demo.base.BaseRepository
import com.maji.mvvm.demo.base.model.HttpResult
import com.maji.mvvm.demo.repository.creator.ServiceCreator

/**
 *
 * <p>
 * Created by android_ls on 2020/12/27 11:49.
 *
 * @author android_ls
 * @version 1.0
 */
class GithubApiRepo : BaseRepository() {

    /**
     * 获取github open api列表数据
     */
    suspend fun getOpenApiList(): HttpResult<MutableMap<String, String>> {
        val service = ServiceCreator.create<IGithubApiService>()
        return service.getOpenApiList().awaitResult()
    }

    suspend fun getOpenApiList(pageNo: Int, pageSize: Int): HttpResult<MutableMap<String, String>> {
        val service = ServiceCreator.create<IGithubApiService>()
        return service.getOpenApiList().awaitResult()
    }

}