package com.maji.mvvm.demo

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import com.maji.mvvm.demo.utils.XLogManager

/**
 *
 * <p>
 * Created by android_ls on 2020/12/25 17:36.
 *
 * @author android_ls
 * @version 1.0
 */
class MJApp : Application() {

    override fun onCreate() {
        super.onCreate()
        appContext = this
        XLogManager.init()
    }

}

lateinit var appContext: MJApp