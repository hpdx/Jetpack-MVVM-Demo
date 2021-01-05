package com.maji.mvvm.demo.dispatcher

import java.util.concurrent.ScheduledThreadPoolExecutor
import java.util.concurrent.TimeUnit

/**
 *
 * <p>
 * Created by android_ls on 2020/12/26 16:25.
 *
 * @author android_ls
 * @version 1.0
 */
object TaskScheduler {

    /**
     * 主线程周期性执行任务，默认立刻执行
     * 之后间隔period执行，不需要时注意取消，每次执行时如果有相同的任务，默认会先取消
     *
     * @param task 任务对象
     */
    fun start(task: TaskPeriod) {
        task.canceled.compareAndSet(true, false)
        val scheduledExecutorService = ScheduledThreadPoolExecutor(
            1,
            ThreadFactory.SCHEDULER_THREAD_FACTORY
        )
        scheduledExecutorService.scheduleAtFixedRate({
            if (task.canceled.get()) {
                scheduledExecutorService.shutdownNow()
            } else {
                if (task.mainThread) {
                    DispatcherTask.runOnMainThread(task)
                } else {
                    task.run()
                }
            }
        }, task.startDelayMillisecond, task.periodMillisecond, TimeUnit.MILLISECONDS)
    }

    /**
     * 取消周期性任务
     * @param task 任务对象
     */
    fun stop(task: TaskPeriod) {
        task.canceled.compareAndSet(false, true)
    }

}