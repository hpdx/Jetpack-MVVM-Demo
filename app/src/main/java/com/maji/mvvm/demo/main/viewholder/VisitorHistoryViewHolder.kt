package com.maji.mvvm.demo.main.viewholder

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import com.maji.mvvm.demo.R
import com.maji.mvvm.demo.base.BaseViewHolder
import com.maji.mvvm.demo.base.listener.OnItemClickListener
import com.maji.mvvm.demo.main.model.ApiInfo

/**
 *
 * <p>
 * Created by android_ls on 2020/12/26 15:27.
 *
 * @author android_ls
 * @version 1.0
 */
class VisitorHistoryViewHolder(
    layoutInflater: LayoutInflater, resId: Int, parent: ViewGroup,
    onItemClickListener: OnItemClickListener<ApiInfo>? = null
) : BaseViewHolder<ApiInfo>(layoutInflater, resId, parent, onItemClickListener) {

    private val tvName: TextView = itemView.findViewById(R.id.tv_name)
    private val tvTime: TextView = itemView.findViewById(R.id.tv_time)

    override fun bind(cellModel: ApiInfo, position: Int) {
        super.bind(cellModel, position)
        tvName.text = cellModel.url
        tvTime.text = cellModel.time
    }

}