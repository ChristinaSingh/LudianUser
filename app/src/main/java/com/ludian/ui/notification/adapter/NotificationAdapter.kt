package com.ludian.ui.notification.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.ludian.R
import com.ludian.databinding.ItemNotificationBinding
import com.ludian.models.NotificationModel
import com.ludian.utils.SharedPrf


class NotificationAdapter (
    private val mContext: Context,
    var arrayList: ArrayList<NotificationModel.Data>?
) : RecyclerView.Adapter<NotificationAdapter.MyViewHolder>() {

    private val sharedPrf by lazy { SharedPrf(mContext) }

    class MyViewHolder(var binding: ItemNotificationBinding) :
        RecyclerView.ViewHolder(binding.root)


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding: ItemNotificationBinding = DataBindingUtil.inflate(
            LayoutInflater.from(mContext), R.layout.item_notification, parent, false
        )
        return MyViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return arrayList!!.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        // holder.binding.chk.text = arrayList!!.get(position)

        if(sharedPrf.getStoredTag(SharedPrf.LANGUAGE)=="en")   holder.binding.tvName.text = arrayList!![position].description
        else   holder.binding.tvName.text = arrayList!![position].description_ar

            holder.binding.tvDate.text = arrayList!![position].date_time

        /*    Glide.with(mContext)
                .load(arrayList!![position].image_urls[0])
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .placeholder(com.denzcoskun.imageslider.R.drawable.default_loading)
                .error(com.denzcoskun.imageslider.R.drawable.default_error)
                .centerInside()
                .into(holder.binding.ivImg)*/

    }


    fun notifyAdapter(notificationList: ArrayList<NotificationModel.Data>) {
        arrayList = notificationList
        notifyDataSetChanged()
    }


}