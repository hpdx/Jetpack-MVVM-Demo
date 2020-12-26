package com.maji.mvvm.demo.service

import retrofit2.Call
import retrofit2.http.GET

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