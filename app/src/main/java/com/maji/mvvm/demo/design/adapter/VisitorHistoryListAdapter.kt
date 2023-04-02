package com.maji.mvvm.demo.design.adapter

import android.content.Context
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.maji.mvvm.demo.R
import com.maji.mvvm.demo.base.BaseAdapter
import com.maji.mvvm.demo.base.listener.OnItemClickListener
import com.maji.mvvm.demo.design.viewholder.VisitorHistoryViewHolder
import com.maji.mvvm.demo.design.model.ApiInfo

/**
 *
 * <p>
 * Created by android_ls on 2020/12/26 15:24.
 *
 * @author android_ls
 * @version 1.0
 */
class VisitorHistoryListAdapter(
    context: Context,
    data: MutableList<ApiInfo>,
    onItemClickListener: OnItemClickListener<ApiInfo>? = null
) : BaseAdapter<ApiInfo>(context, data, onItemClickListener) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return VisitorHistoryViewHolder(mLayoutInflater, R.layout.item_visitor_history_layout, parent)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val itemInfo = data[position]
        if (holder is VisitorHistoryViewHolder) {
            holder.bind(itemInfo, position)
        }
    }

}