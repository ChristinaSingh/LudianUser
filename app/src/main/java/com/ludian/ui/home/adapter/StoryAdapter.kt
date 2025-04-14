package com.ludian.ui.home.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.ludian.R
import com.ludian.databinding.ItemStoryBinding
import com.ludian.models.PropertyModel
import com.ludian.ui.booking.PropertyDetailAct


class StoryAdapter (
    private val mContext: Context,
    var arrayList: ArrayList<PropertyModel.Property>?,
    val listener:OnStoryListener
) : RecyclerView.Adapter<StoryAdapter.MyViewHolder>() {



    class MyViewHolder(var binding: ItemStoryBinding) :
        RecyclerView.ViewHolder(binding.root)


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding: ItemStoryBinding = DataBindingUtil.inflate(
            LayoutInflater.from(mContext), R.layout.item_story, parent, false
        )
        return MyViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return arrayList!!.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {

         if(arrayList!![position].image_urls.isNotEmpty()) {
             holder.binding.tvName.text = arrayList!![position].title
             Glide.with(mContext)
                 .load(arrayList!![position].image_urls[0])
                 .diskCacheStrategy(DiskCacheStrategy.ALL)
                 .placeholder(com.denzcoskun.imageslider.R.drawable.default_loading)
                 .error(com.denzcoskun.imageslider.R.drawable.default_error)
                 .centerInside()
                 .into(holder.binding.ivImg)
         }

        holder.itemView.setOnClickListener {
            mContext.startActivity(
                Intent(mContext, PropertyDetailAct::class.java)
                .putExtra("propertyData",arrayList!![position])
                .putExtra("type",""))
        }
    }


    interface OnStoryListener {
        fun onStory(model: PropertyModel.Property, position: Int, type: String)

    }


    fun notifyAdapter(propertyList: ArrayList<PropertyModel.Property>) {
        arrayList = propertyList
        notifyDataSetChanged()
    }


}