package com.maji.mvvm.demo.repository

import retrofit2.http.GET
import retrofit2.Call

/**
 *
 * <p>
 * Created by android_ls on 2020/12/25 21:13.
 *
 * @author android_ls
 * @version 1.0
 */
interface IGithubApiService {

    @GET("https://api.github.com")
    fun getOpenApiList(): Call<MutableMap<String, String>>

}