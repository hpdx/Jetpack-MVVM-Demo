package com.maji.mvvm.demo.utils

import androidx.lifecycle.MutableLiveData
import kotlin.collections.HashMap

/**
 * 基于LiveData的组件间通信
 * 应用场景：多个Activity之间进行传值、Callback，功能类似EventBus
 * <p>
 * Created by android_ls on 2021/1/8 21:55.
 *
 * @author android_ls
 * @version 1.0
 */
object LiveDataBus {

    private val liveDataMap = HashMap<Int, MutableLiveData<Any>>()

    @Synchronized
    fun <T> get(key: Int, type: Class<T>): MutableLiveData<T> {
        if (!liveDataMap.containsKey(key)) {
            liveDataMap[key] = MutableLiveData<Any>()
        }
        return liveDataMap[key] as MutableLiveData<T>
    }

}