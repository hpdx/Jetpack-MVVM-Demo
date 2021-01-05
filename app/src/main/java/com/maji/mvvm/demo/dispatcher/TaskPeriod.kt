package com.maji.mvvm.demo.dispatcher

import java.util.concurrent.atomic.AtomicBoolean

/**
 *
 * <p>
 * Created by android_ls on 2020/12/26 16:23.
 *
 * @author android_ls
 * @version 1.0
 */
class TaskPeriod(
    val startDelayMillisecond: Long = 0,
    val periodMillisecond: Long = 0,
    val mainThread: Boolean = true,
    private val taskCallback: OnTaskCallback
) : Runnable {

    val canceled = AtomicBoolean(false)

    override fun run() {
        if (!canceled.get()) {
            taskCallback.run()
        }
    }

    interface OnTaskCallback {
        fun run()
    }

}