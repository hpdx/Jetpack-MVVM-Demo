package com.maji.mvvm.demo.utils

import android.view.Gravity
import android.widget.Toast
import com.maji.mvvm.demo.MJApp
import com.maji.mvvm.demo.appContext

/**
 *
 * <p>
 * Created by android_ls on 2020/12/25 18:02.
 *
 * @author android_ls
 * @version 1.0
 */
object ToastUtils {

    fun showToast(message: String?) {
        val toast = Toast.makeText(appContext, message, Toast.LENGTH_SHORT)
        toast.setGravity(Gravity.CENTER, 0, 0)
        toast.show()
    }

    fun showToast(resId: Int) {
        val toast: Toast = Toast.makeText(appContext, appContext.getString(resId), Toast.LENGTH_SHORT)
        toast.setGravity(Gravity.CENTER, 0, 0)
        toast.show()
    }

}