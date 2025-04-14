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
import com.ludian.ui.booking.PropertyDetailAct
import com.ludian.databinding.ItemPopularHomeBinding
import com.ludian.models.PropertyModel


class PopularHomeAdapter (
    private val mContext: Context,
    var arrayList: ArrayList<PropertyModel.Property>?,
    val listener : OnBookmarkMostListener,
    var type:String
) : RecyclerView.Adapter<PopularHomeAdapter.MyViewHolder>() {



    class MyViewHolder(var binding: ItemPopularHomeBinding) :
        RecyclerView.ViewHolder(binding.root)


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding: ItemPopularHomeBinding = DataBindingUtil.inflate(
            LayoutInflater.from(mContext), R.layout.item_popular_home, parent, false
        )
        return MyViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return arrayList!!.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.binding.tvName.text = arrayList!![position].title
        holder.binding.tvAddress.text = arrayList!![position].address
        holder.binding.tvPrice.text =  arrayList!![position].price + " SAR " + ",7"
        holder.binding.tvRating.text =  arrayList!![position].ratting_avg

        if(arrayList!![position].book_marked) holder.binding.ivBookmark.setImageResource(R.drawable.ic_bookmark_select)
        else holder.binding.ivBookmark.setImageResource(R.drawable.ic_bookmark1)
       if(arrayList!![position].image_urls.isNotEmpty()) {
           Glide.with(mContext)
               .load(arrayList!![position].image_urls[0])
               .diskCacheStrategy(DiskCacheStrategy.ALL)
               .placeholder(com.denzcoskun.imageslider.R.drawable.default_loading)
               .error(com.denzcoskun.imageslider.R.drawable.default_error)
               .centerInside()
               .into(holder.binding.ivImg)
       }

        else {
           Glide.with(mContext)
               .load(com.denzcoskun.imageslider.R.drawable.default_error)
               .diskCacheStrategy(DiskCacheStrategy.ALL)
               .placeholder(com.denzcoskun.imageslider.R.drawable.default_loading)
               .error(com.denzcoskun.imageslider.R.drawable.default_error)
               .centerInside()
               .into(holder.binding.ivImg)
       }

        holder.itemView.setOnClickListener {
            // mContext.startActivity(Intent(mContext, CheckGuestAct::class.java))
            mContext.startActivity(Intent(mContext, PropertyDetailAct::class.java)
                .putExtra("propertyData",arrayList!![position])
                .putExtra("type",type))
        }

        holder.binding.ivBookmark.setOnClickListener {
            listener.onBookmarkMost(arrayList!![position],position,"bookmark_most")
        }

    }


    interface OnBookmarkMostListener {
        fun onBookmarkMost(model: PropertyModel.Property, position: Int, type: String)

    }



    fun notifyAdapter(propertyList: ArrayList<PropertyModel.Property>) {
        arrayList = propertyList
        notifyDataSetChanged()
    }


    fun addItems(newItems: List<PropertyModel.Property>) {
        val startPosition = arrayList!!.size
        arrayList!!.addAll(newItems)
        notifyItemRangeInserted(startPosition, newItems.size)
    }
}