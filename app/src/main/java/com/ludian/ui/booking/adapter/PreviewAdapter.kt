package com.ludian.ui.booking.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.ludian.R
import com.ludian.databinding.ItemPreviewBinding


class PreviewAdapter (
    private val mContext: Context,
                          var arrayList: ArrayList<String>?,
) : RecyclerView.Adapter<PreviewAdapter.MyViewHolder>() {



    class MyViewHolder(var binding: ItemPreviewBinding) :
        RecyclerView.ViewHolder(binding.root)


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding: ItemPreviewBinding = DataBindingUtil.inflate(
            LayoutInflater.from(mContext), R.layout.item_preview, parent, false
        )
        return MyViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return arrayList!!.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {

        Glide.with(mContext)
            .load(arrayList!![position])
            .diskCacheStrategy(DiskCacheStrategy.ALL)
            .placeholder(com.denzcoskun.imageslider.R.drawable.default_loading)
            .error(com.denzcoskun.imageslider.R.drawable.default_error)
            .centerInside()
            .into(holder.binding.ivImg)


        holder.itemView.setOnClickListener {
        }
    }
}