package com.ludian.ui.booking.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.ludian.R
import com.ludian.databinding.ItemBookingCancelBinding
import com.ludian.models.BookingModel
import com.ludian.utils.Helper


class BookingCancelAdapter (
    private val mContext: Context,
    var arrayList: ArrayList<BookingModel.Data>?,
) : RecyclerView.Adapter<BookingCancelAdapter.MyViewHolder>() {



    class MyViewHolder(var binding: ItemBookingCancelBinding) :
        RecyclerView.ViewHolder(binding.root)


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding: ItemBookingCancelBinding = DataBindingUtil.inflate(
            LayoutInflater.from(mContext), R.layout.item_booking_cancel, parent, false
        )
        return MyViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return arrayList!!.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.binding.tvName.text = arrayList!![position].property.title
        holder.binding.tvAddress.text = arrayList!![position].property.address
        holder.binding.tvPrice.text = mContext.getString(R.string.price) + " : ${arrayList!![position].orderTotal} SAR "   //"$${String.format("%.2f", arrayList!![position].property.price)}"
       // holder.binding.tvDate.text =    mContext.getString(R.string.booking_date)+" : ${arrayList!![position].booking_request_date_start}"
        holder.binding.tvDate.text =    mContext.getString(R.string.booking_date)+" : ${Helper.formatDates(arrayList!![position].booking_request_date_start).joinToString(", ")}"

        holder.binding.tvStatus.text = arrayList!![position].booking_request_status

     /*   Glide.with(mContext)
            .load(arrayList!![position].property.image_urls[0])
            .diskCacheStrategy(DiskCacheStrategy.ALL)
            .placeholder(com.denzcoskun.imageslider.R.drawable.default_loading)
            .error(com.denzcoskun.imageslider.R.drawable.default_error)
            .centerInside()
            .into(holder.binding.ivImg)*/


        if(arrayList!![position].property.image_urls.isNotEmpty()) {
            Glide.with(mContext)
                .load(arrayList!![position].property.image_urls[0])
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
        }
    }


    fun notifyAdapter(bookingArrayList: ArrayList<BookingModel.Data>) {
        arrayList = bookingArrayList
        notifyDataSetChanged()
    }
}