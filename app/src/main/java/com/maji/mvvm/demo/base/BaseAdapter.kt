package com.maji.mvvm.demo.base

import android.content.Context
import android.view.LayoutInflater
import androidx.recyclerview.widget.RecyclerView
import com.maji.mvvm.demo.base.listener.OnItemClickListener

/**
 *
 * <p>
 * Created by android_ls on 2020/12/26 14:53.
 *
 * @author android_ls
 * @version 1.0
 */
abstract class BaseAdapter<M>(
    context: Context,
    var data: MutableList<M>,
    var onItemClickListener: OnItemClickListener<M>? = null
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    var mLayoutInflater: LayoutInflater = LayoutInflater.from(context)

    fun updateAdapterData(data: MutableList<M>) {
        if (this.data.size > 0) {
            this.data.clear()
        }
        this.data.addAll(data)
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int {
        return data.size
    }

}