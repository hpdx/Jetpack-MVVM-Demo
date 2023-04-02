package com.maji.mvvm.demo.design.adapter

import android.content.Context
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.maji.mvvm.demo.R
import com.maji.mvvm.demo.base.BaseAdapter
import com.maji.mvvm.demo.base.listener.OnItemClickListener
import com.maji.mvvm.demo.design.viewholder.OpenApiViewHolder
import com.maji.mvvm.demo.design.model.ItemInfo

/**
 *
 * <p>
 * Created by android_ls on 2020/12/26 15:24.
 *
 * @author android_ls
 * @version 1.0
 */
class OpenApiListAdapter(
    context: Context,
    data: MutableList<ItemInfo>,
    onItemClickListener: OnItemClickListener<ItemInfo>? = null
) : BaseAdapter<ItemInfo>(context, data, onItemClickListener) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return OpenApiViewHolder(mLayoutInflater, R.layout.item_open_api_layout, parent)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val itemInfo = data[position]
        if (holder is OpenApiViewHolder) {
            holder.bind(itemInfo, position)
        }
    }

}