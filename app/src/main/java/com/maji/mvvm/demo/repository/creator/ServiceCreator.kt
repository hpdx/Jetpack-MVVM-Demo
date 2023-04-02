package com.maji.mvvm.demo.repository.creator

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

/**
 *
 * <p>
 * Created by android_ls on 2020/12/25 18:57.
 *
 * @author android_ls
 * @version 1.0
 */
object ServiceCreator {

    private const val BASE_URL = "https://api.github.com/"

    private val retrofit = Retrofit.Builder()
        .client(HttpClientCreator.getOkHttpClient())
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    fun <T> create(serviceClass: Class<T>): T = retrofit.create(serviceClass)

    inline fun <reified T> create(): T = create(T::class.java)

}