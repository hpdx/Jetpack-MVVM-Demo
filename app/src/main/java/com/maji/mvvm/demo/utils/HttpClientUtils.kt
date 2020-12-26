package com.maji.mvvm.demo.utils

import com.elvishew.xlog.XLog
import okhttp3.ConnectionSpec
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import java.util.concurrent.TimeUnit

/**
 *
 * <p>
 * Created by android_ls on 2020/12/26 11:18.
 *
 * @author android_ls
 * @version 1.0
 */
object HttpClientUtils {

    private const val TAG = "OkHttp"
    private const val DEFAULT_TIMEOUT: Long = 15
    private const val READ_TIMEOUT: Long = 60
    private val logger = XLog.tag(TAG).nt().nst()

    fun getOkHttpClient(block: OkHttpClient.Builder.() -> Unit = {}): OkHttpClient {
        return OkHttpClient().newBuilder().apply {
            addInterceptor(getHttpLoggingInterceptor())
            connectTimeout(DEFAULT_TIMEOUT, TimeUnit.SECONDS)
            readTimeout(READ_TIMEOUT, TimeUnit.SECONDS)
            writeTimeout(DEFAULT_TIMEOUT, TimeUnit.SECONDS)
            connectionSpecs(listOf(ConnectionSpec.MODERN_TLS, ConnectionSpec.COMPATIBLE_TLS))
            retryOnConnectionFailure(true)
            block()
        }.build()
    }

    private fun getHttpLoggingInterceptor() = HttpLoggingInterceptor(
        object : HttpLoggingInterceptor.Logger {
            override fun log(message: String) {
                logger.d(message)
            }
        }
    ).apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

}