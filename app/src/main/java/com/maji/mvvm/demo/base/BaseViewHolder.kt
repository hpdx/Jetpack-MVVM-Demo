package com.maji.mvvm.demo.base

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.maji.mvvm.demo.base.listener.OnItemClickListener

/**
 *
 * <p>
 * Created by android_ls on 2020/12/26 15:07.
 *
 * @author android_ls
 * @version 1.0
 */
open class BaseViewHolder<M>(
    layoutInflater: LayoutInflater, resId: Int, parent: ViewGroup,
    var onItemClickListener: OnItemClickListener<M>? = null
) : RecyclerView.ViewHolder(layoutInflater.inflate(resId, parent, false)) {

    private var mCellModel: M? = null

    open fun bind(cellModel: M, position: Int) {
        mCellModel = cellModel
    }

}