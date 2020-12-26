package com.maji.mvvm.demo.dispatcher

import android.os.Process
import java.util.concurrent.ThreadFactory
import java.util.concurrent.atomic.AtomicInteger

/**
 *
 * <p>
 * Created by android_ls on 2020/12/26 16:21.
 *
 * @author android_ls
 * @version 1.0
 */
object ThreadFactory {

    internal class BackgroundRunnable(private val runnable: Runnable) : Runnable {
        override fun run() {
            Process.setThreadPriority(Process.THREAD_PRIORITY_BACKGROUND)
            runnable.run()
        }
    }

    val SCHEDULER_THREAD_FACTORY = object : ThreadFactory {
        private val mCount = AtomicInteger(1)
        override fun newThread(r: Runnable): Thread {
            return Thread(
                BackgroundRunnable(r),
                "TaskScheduler scheduler #" + mCount.getAndIncrement()
            )
        }
    }

}