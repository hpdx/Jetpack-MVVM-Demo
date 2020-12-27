package com.maji.mvvm.demo.base.model

import okhttp3.Response
import retrofit2.HttpException

/**
 * Sealed class of HTTP result
 * https://github.com/gildor/kotlin-coroutines-retrofit
 */
@Suppress("unused")
sealed class HttpResult<out T : Any> {
    /**
     * Successful result of request without errors
     */
    class Success<out T : Any>(
        val value: T,
        override val response: Response
    ) : HttpResult<T>(), ResponseResult {
        override fun toString() = "Result.Ok{value=$value, response=$response}"
    }

    /**
     * HTTP error
     */
    class Error(
        override val exception: HttpException,
        override val response: Response
    ) : HttpResult<Nothing>(), ErrorResult, ResponseResult {
        override fun toString() = "Result.Error{exception=$exception}"
    }

    /**
     * Network exception occurred talking to the server or when an unexpected
     * exception occurred creating the request or processing the response
     */
    class Exception(
        override val exception: Throwable
    ) : HttpResult<Nothing>(), ErrorResult {
        override fun toString() = "Result.Exception{$exception}"
    }

}

/**
 * Interface for [Result] classes with [okhttp3.Response]: [Result.Ok] and [Result.Error]
 */
interface ResponseResult {
    val response: Response
}

/**
 * Interface for [Result] classes that contains [Throwable]: [Result.Error] and [Result.Exception]
 */
interface ErrorResult {
    val exception: Throwable
}

/**
 * Returns [Result.Ok.value] or `null`
 */
fun <T : Any> HttpResult<T>.getOrNull(): T? = (this as? HttpResult.Success)?.value

/**
 * Returns [Result.Ok.value] or [default]
 */
fun <T : Any> HttpResult<T>.getOrDefault(default: T): T = getOrNull() ?: default

/**
 * Returns [Result.Ok.value] or throw [throwable] or [ErrorResult.exception]
 */
fun <T : Any> HttpResult<T>.getOrThrow(throwable: Throwable? = null): T {
    return when (this) {
        is HttpResult.Success -> value
        is HttpResult.Error -> throw throwable ?: exception
        is HttpResult.Exception -> throw throwable ?: exception
    }
}
