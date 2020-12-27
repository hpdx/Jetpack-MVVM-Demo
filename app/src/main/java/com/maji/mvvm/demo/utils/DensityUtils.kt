package com.maji.mvvm.demo.utils

import android.content.Context
import android.util.TypedValue

/**
 *
 * <p>
 * Created by android_ls on 2020/12/27 15:05.
 *
 * @author android_ls
 * @version 1.0
 */
object DensityUtils {

    fun getDisplayHeight(context: Context): Int {
        return context.resources.displayMetrics.heightPixels
    }

    fun getDisplayWidth(context: Context): Int {
        return context.resources.displayMetrics.widthPixels
    }

    fun dpToPx(context: Context, dip: Float): Int {
        val px = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP, dip,
            context.resources.displayMetrics
        )
        return px.toInt()
    }

}