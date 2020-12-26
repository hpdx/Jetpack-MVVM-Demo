package com.maji.mvvm.demo.utils

import java.text.SimpleDateFormat
import java.util.*

/**
 *
 * <p>
 * Created by android_ls on 2020/12/26 17:03.
 *
 * @author android_ls
 * @version 1.0
 */
object DateUtils {

    /**
     * 获取格式化后的系统当前时间
     */
    fun getCurrentDate(): String {
        val sf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss.S", Locale.getDefault())
        return sf.format(Date(System.currentTimeMillis()))
    }

}