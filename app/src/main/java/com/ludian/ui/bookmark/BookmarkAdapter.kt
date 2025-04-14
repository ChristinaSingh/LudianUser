package com.ludian.ui.bookmark

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.ludian.R
import com.ludian.databinding.ItemBookmarkBinding
import com.ludian.models.PropertyModel
import com.ludian.ui.booking.PropertyDetailAct


class BookmarkAdapter (
    private val mContext: Context,
                          var arrayList: ArrayList<PropertyModel.Property>?,
    val type:String
) : RecyclerView.Adapter<BookmarkAdapter.MyViewHolder>() {



    class MyViewHolder(var binding: ItemBookmarkBinding) :
        RecyclerView.ViewHolder(binding.root)


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding: ItemBookmarkBinding = DataBindingUtil.inflate(
            LayoutInflater.from(mContext), R.layout.item_bookmark, parent, false
        )
        return MyViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return arrayList!!.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        // holder.binding.chk.text = arrayList!!.get(position)


        if(arrayList!![position].image_urls.isNotEmpty()) {
            holder.binding.tvName.text = arrayList!![position].title
            holder.binding.tvAddress.text = arrayList!![position].address
            holder.binding.tvPrice.text =  arrayList!![position].price + " SAR " + ",7"
            holder.binding.tvRating.text =  arrayList!![position].ratting_avg


            Glide.with(mContext)
                .load(arrayList!![position].image_urls[0])
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .placeholder(com.denzcoskun.imageslider.R.drawable.default_loading)
                .error(com.denzcoskun.imageslider.R.drawable.default_error)
                .centerInside()
                .into(holder.binding.ivImg)
        }

        holder.itemView.setOnClickListener {
            mContext.startActivity(Intent(mContext, PropertyDetailAct::class.java)
                .putExtra("propertyData",arrayList!![position])
                .putExtra("type",type))
        }
    }


    fun notifyAdapter(bookmarkList: ArrayList<PropertyModel.Property>) {
        arrayList = bookmarkList
        notifyDataSetChanged()
    }


}