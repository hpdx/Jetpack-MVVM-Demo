package com.maji.mvvm.demo.base

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 *
 * <p>
 * Created by android_ls on 2020/12/27 12:06.
 *
 * @author android_ls
 * @version 1.0
 */
open class BaseViewModel : ViewModel() {

    fun <T> launchOnIO(block: suspend CoroutineScope.() -> T) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) { // async
                block()
            }
        }
    }

    fun <T> launchOnUI(block: suspend CoroutineScope.() -> T) {
        viewModelScope.launch {
            block()
        }
    }

}