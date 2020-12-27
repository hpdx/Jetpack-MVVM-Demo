package com.maji.mvvm.demo.base

import kotlinx.coroutines.CancellableContinuation
import kotlinx.coroutines.suspendCancellableCoroutine
import retrofit2.Call
import retrofit2.Callback
import retrofit2.HttpException
import retrofit2.Response
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import com.maji.mvvm.demo.base.model.HttpResult

/**
 * https://github.com/gildor/kotlin-coroutines-retrofit
 * <p>
 * Created by android_ls on 2020/12/27 13:40.
 *
 * @author android_ls
 * @version 1.0
 */
open class BaseRepository {

    /**
     * Suspend extension that allows suspend [Call] inside of a coroutine.
     *
     * @return Result for request or throw exception
     */
    suspend fun <T : Any> Call<T>.awaitResult(): HttpResult<T> {
        return suspendCancellableCoroutine { continuation ->
            enqueue(object : Callback<T> {
                override fun onResponse(call: Call<T>?, response: Response<T>) {
                    continuation.resumeWith(runCatching {
                        if (response.isSuccessful) {
                            val body = response.body()
                            if (body == null) {
                                HttpResult.Exception(NullPointerException("Response body is null"))
                            } else {
                                HttpResult.Success(body, response.raw())
                            }
                        } else {
                            HttpResult.Error(HttpException(response), response.raw())
                        }
                    })
                }

                override fun onFailure(call: Call<T>, t: Throwable) {
                    // Don't bother with resuming the continuation if it is already cancelled.
                    if (continuation.isCancelled) return
                    continuation.resume(HttpResult.Exception(t))
                }
            })

            registerOnCompletion(continuation)
        }
    }

    /**
     * Suspend extension that allows suspend [Call] inside coroutine.
     *
     * @return T for request or throw exception
     */
    suspend fun <T : Any> Call<T>.await(): T {
        return suspendCancellableCoroutine { continuation ->
            enqueue(object : Callback<T> {
                override fun onResponse(call: Call<T>?, response: Response<T?>) {
                    continuation.resumeWith(runCatching {
                        if (response.isSuccessful) {
                            response.body()
                                ?: throw NullPointerException("Response body is null: $response")
                        } else {
                            throw HttpException(response)
                        }
                    })
                }

                override fun onFailure(call: Call<T>, t: Throwable) {
                    // Don't bother with resuming the continuation if it is already cancelled.
                    if (continuation.isCancelled) return
                    continuation.resumeWithException(t)
                }
            })

            registerOnCompletion(continuation)
        }
    }

    /**
     * Suspend extension that allows suspend [Call] inside coroutine.
     *
     * @return Response for request or throw exception
     */
    suspend fun <T : Any?> Call<T>.awaitResponse(): Response<T> {
        return suspendCancellableCoroutine { continuation ->
            enqueue(object : Callback<T> {
                override fun onResponse(call: Call<T>?, response: Response<T>) {
                    continuation.resume(response)
                }

                override fun onFailure(call: Call<T>, t: Throwable) {
                    // Don't bother with resuming the continuation if it is already cancelled.
                    if (continuation.isCancelled) return
                    continuation.resumeWithException(t)
                }
            })

            registerOnCompletion(continuation)
        }
    }

    /**
     * Suspend extension that allows suspend [Call] inside coroutine.
     *
     * @return sealed class [Result] object that can be
     *         casted to [Result.Ok] (success) or [Result.Error] (HTTP error)
     *         and [Result.Exception] (other errors)
     */
    private fun Call<*>.registerOnCompletion(continuation: CancellableContinuation<*>) {
        continuation.invokeOnCancellation {
            try {
                cancel()
            } catch (ex: Throwable) {
                //Ignore cancel exception
            }
        }
    }

}