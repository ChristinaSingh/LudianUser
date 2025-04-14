package com.ludian.ui.profile.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.ludian.R
import com.ludian.databinding.ItemFaqBinding
import com.ludian.models.FaqModel
import com.ludian.models.ReviewModel


class FaqAdapter (
    private val mContext: Context,
                          var arrayList: ArrayList<FaqModel.Data>?
) : RecyclerView.Adapter<FaqAdapter.MyViewHolder>() {



    class MyViewHolder(var binding: ItemFaqBinding) :
        RecyclerView.ViewHolder(binding.root)


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding: ItemFaqBinding = DataBindingUtil.inflate(
            LayoutInflater.from(mContext), R.layout.item_faq, parent, false
        )
        return MyViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return arrayList!!.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
         holder.binding.tvName.text = arrayList!![position].qus

    }

    fun notifyAdapter(arrayFaqList: ArrayList<FaqModel.Data>) {
        arrayList = arrayFaqList
        notifyDataSetChanged()
    }
}