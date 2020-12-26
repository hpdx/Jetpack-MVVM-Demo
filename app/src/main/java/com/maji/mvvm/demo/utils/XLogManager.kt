package com.maji.mvvm.demo.utils

import com.elvishew.xlog.LogConfiguration
import com.elvishew.xlog.LogLevel
import com.elvishew.xlog.XLog
import com.elvishew.xlog.flattener.ClassicFlattener
import com.elvishew.xlog.printer.AndroidPrinter
import com.elvishew.xlog.printer.file.FilePrinter
import com.elvishew.xlog.printer.file.clean.FileLastModifiedCleanStrategy
import com.elvishew.xlog.printer.file.naming.DateFileNameGenerator
import com.maji.mvvm.demo.BuildConfig

/**
 *
 * <p>
 * Created by android_ls on 2020/12/25 17:41.
 *
 * @author android_ls
 * @version 1.0
 */
object XLogManager {

    private const val TAG = "XLog"
    private const val MAX_TIME = 1000 * 60 * 60 * 24 * 7L // 日志保留7天

    fun init() {
        val config = LogConfiguration.Builder()
            .logLevel(if (BuildConfig.DEBUG) LogLevel.ALL else LogLevel.NONE) // 指定日志级别，低于该级别的日志将不会被打印，默认为 LogLevel.ALL
            .tag(TAG)
            .t() // 允许打印线程信息，默认禁止
            .st(1) // 允许打印深度为2的调用栈信息，默认禁止
            .build()

        val androidPrinter = object : AndroidPrinter() {
            override fun println(logLevel: Int, tag: String?, msg: String?) {
                if (BuildConfig.DEBUG) {
                    // 仅Debug模式打印控制台日志
                    super.println(logLevel, tag, msg)
                }
            }
        }

        val filePrinter = FilePrinter
            .Builder(FileUtils.getLogDirPath())
            .flattener(ClassicFlattener())
            .fileNameGenerator(DateFileNameGenerator())
            .cleanStrategy(FileLastModifiedCleanStrategy(MAX_TIME))
            .build()

        XLog.init(
            config,
            androidPrinter,
            filePrinter
        )
    }

}