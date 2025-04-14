package com.ludian.ui.home.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.ludian.R
import com.ludian.databinding.ItemTrendingDestinationBinding


class TrendingDestinationAdapter (
    private val mContext: Context/*,
                          var arrayList: ArrayList<String>?,*/
) : RecyclerView.Adapter<TrendingDestinationAdapter.MyViewHolder>() {



    class MyViewHolder(var binding: ItemTrendingDestinationBinding) :
        RecyclerView.ViewHolder(binding.root)


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding: ItemTrendingDestinationBinding = DataBindingUtil.inflate(
            LayoutInflater.from(mContext), R.layout.item_trending_destination, parent, false
        )
        return MyViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return 5
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        // holder.binding.chk.text = arrayList!!.get(position)
        holder.itemView.setOnClickListener {
        }
    }
}