package com.maji.mvvm.demo.design.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import com.maji.mvvm.demo.R
import com.maji.mvvm.demo.base.listener.OnItemClickListener
import com.maji.mvvm.demo.design.viewholder.OpenApiViewHolder
import com.maji.mvvm.demo.design.model.ItemInfo

/**
 * https://developer.android.google.cn/topic/libraries/architecture/paging/v3-paged-data?hl=zh-cn
 * <p>
 * Created by android_ls on 2020/12/26 15:24.
 *
 * @author android_ls
 * @version 1.0
 */
class OpenApiListAdapter2(
    diffCallback: DiffUtil.ItemCallback<ItemInfo>,
    onItemClickListener: OnItemClickListener<ItemInfo>? = null
) : PagingDataAdapter<ItemInfo, OpenApiViewHolder>(diffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OpenApiViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return OpenApiViewHolder(layoutInflater, R.layout.item_open_api_layout, parent)
    }

    override fun onBindViewHolder(holder: OpenApiViewHolder, position: Int) {
        val itemInfo = getItem(position)
        itemInfo?.let {
            holder.bind(itemInfo, position)
        }
    }

}

object UserComparator : DiffUtil.ItemCallback<ItemInfo>() {

    override fun areItemsTheSame(oldItem: ItemInfo, newItem: ItemInfo): Boolean {
        // Id is unique.
        return oldItem.url == newItem.url
    }

    override fun areContentsTheSame(oldItem: ItemInfo, newItem: ItemInfo): Boolean {
        return oldItem.url == newItem.url && oldItem.name == newItem.name
    }

}