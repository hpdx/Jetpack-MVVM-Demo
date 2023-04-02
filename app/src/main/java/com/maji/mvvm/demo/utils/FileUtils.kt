package com.maji.mvvm.demo.utils

import android.os.Environment
import com.maji.mvvm.demo.MJApp
import com.maji.mvvm.demo.appContext
import java.io.File

/**
 *
 * <p>
 * Created by android_ls on 2020/12/25 17:44.
 *
 * @author android_ls
 * @version 1.0
 */
object FileUtils {

    /**
     * 应用程序相关文件存放的根目录
     */
    private const val ROOT_DIR_NAME = "MAJIA"

    /**
     * LOG文件存放的目录
     */
    private const val LOG_PATH = "/log"

    private fun getRootDir(): String {
        if (Environment.MEDIA_MOUNTED == Environment.getExternalStorageState()
            || !Environment.isExternalStorageRemovable()
        ) {
            // 外部存储可用
            val cacheFile: File? = appContext.getExternalFilesDir(ROOT_DIR_NAME)
            cacheFile?.let {
                if (it.exists()) {
                    return it.path
                }
            }
        }

        // 外部存储不可用
        return appContext.filesDir.path
    }

    /**
     * 获取Log文件存储目录的路径
     */
    fun getLogDirPath(): String {
        val filesDirPath = getRootDir() + LOG_PATH
        val logDir = File(filesDirPath)
        if (!logDir.exists()) {
            logDir.mkdirs()
        }
        return logDir.absolutePath
    }

}