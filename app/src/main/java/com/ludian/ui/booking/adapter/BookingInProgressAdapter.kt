package com.ludian.ui.booking.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.ludian.R
import com.ludian.databinding.ItemBookingBinding
import com.ludian.databinding.ItemBookingCancelBinding
import com.ludian.databinding.ItemBookingProgressBinding
import com.ludian.models.BookingModel
import com.ludian.ui.booking.BookingDetailAct
import com.ludian.utils.Helper

class BookingInProgressAdapter (
    private val mContext: Context,
    var arrayList: ArrayList<BookingModel.Data>?,
    val listener:OnBookingCompleteListener
) : RecyclerView.Adapter<BookingInProgressAdapter.MyViewHolder>() {


    class MyViewHolder(var binding: ItemBookingProgressBinding) :
        RecyclerView.ViewHolder(binding.root)


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding: ItemBookingProgressBinding = DataBindingUtil.inflate(
            LayoutInflater.from(mContext), R.layout.item_booking_progress, parent, false
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


        if(arrayList!![position].booking_request_status=="ACCEPT"){
            holder.binding.cardComplete.visibility = View.VISIBLE
            holder.binding.cardStaus.visibility = View.GONE
        }
        else {
            holder.binding.cardComplete.visibility = View.GONE
            holder.binding.cardStaus.visibility = View.VISIBLE
        }


      /*  Glide.with(mContext)
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



        holder.binding.tvShowDetail.setOnClickListener {
            mContext.startActivity(Intent(mContext,BookingDetailAct::class.java)
                .putExtra("BookingData",arrayList!![position])
                .putExtra("position",position.toString()))
        }

        holder.binding.cardComplete.setOnClickListener {
            listener.onComplete(arrayList!![position],position,"COMPLETE")
        }
    }



    interface OnBookingCompleteListener {
        fun onComplete(model: BookingModel.Data, position: Int, type: String)

    }

    fun notifyAdapter(bookingArrayList: ArrayList<BookingModel.Data>) {
        arrayList = bookingArrayList
        notifyDataSetChanged()
    }
}