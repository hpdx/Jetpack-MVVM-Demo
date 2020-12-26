package com.maji.mvvm.demo.base.listener

import android.view.View

/**
 *
 * <p>
 * Created by android_ls on 2020/12/26 14:55.
 *
 * @author android_ls
 * @version 1.0
 */
interface OnItemClickListener<T> {
    fun onItemClick(view: View?, data: T, position: Int)
}