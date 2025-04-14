package com.ludian.ui.home.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.ludian.R
import com.ludian.databinding.ItemPopularHomeBinding
import com.ludian.databinding.ItemSearchDetailBinding


class SearchListAdapter (
    private val mContext: Context/*,
                          var arrayList: ArrayList<String>?,*/
) : RecyclerView.Adapter<SearchListAdapter.MyViewHolder>() {



    class MyViewHolder(var binding: ItemSearchDetailBinding) :
        RecyclerView.ViewHolder(binding.root)


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding: ItemSearchDetailBinding = DataBindingUtil.inflate(
            LayoutInflater.from(mContext), R.layout.item_search_detail, parent, false
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