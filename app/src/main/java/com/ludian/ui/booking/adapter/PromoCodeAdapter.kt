package com.ludian.ui.booking.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.ludian.R
import com.ludian.databinding.ItemPromoCodeBinding
import com.ludian.models.PromoCodeModel


class PromoCodeAdapter (
    private val mContext: Context,
    var arrayList: ArrayList<PromoCodeModel.Result>?
    , val listener : OnPromoCodeListener
) : RecyclerView.Adapter<PromoCodeAdapter.MyViewHolder>() {



    class MyViewHolder(var binding: ItemPromoCodeBinding) :
        RecyclerView.ViewHolder(binding.root)


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding: ItemPromoCodeBinding = DataBindingUtil.inflate(
            LayoutInflater.from(mContext), R.layout.item_promo_code, parent, false
        )
        return MyViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return arrayList!!.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {

        holder.binding.tvName.text = arrayList!![position].title
        holder.binding.tvDescription.text = mContext.getString(R.string.expire_on) + " : " +  arrayList!![position].endDate
        holder.binding.tvDiscount.text = arrayList!![position].discountPercent + "%"



   //   if(arrayList!![position].type=="FLAT")  holder.binding.tvDescription.text = "$${arrayList!![position].description} discount"
    //    else  holder.binding.tvDescription.text = "${arrayList!![position].description}% discount"

        if(arrayList!![position].check) holder.binding.ivSelect.setImageResource(R.drawable.ic_select_radio)
        else holder.binding.ivSelect.setImageResource(R.drawable.ic_unselect_radio)

        holder.itemView.setOnClickListener {
          //  if (!arrayList!![position].promoCodeStatus) {
                for (i in 0 until arrayList!!.size) {
                    arrayList!![i].check = false
                }
                arrayList!![position].check = true
                listener.onPromoCode(arrayList!!, position)
            //}
           // else Toast.makeText(mContext,mContext.getString(R.string.already_applied),Toast.LENGTH_LONG).show()
        }
    }

    interface   OnPromoCodeListener{
        fun onPromoCode(propertyList: ArrayList<PromoCodeModel.Result>, position: Int)

    }


    fun notifyAdapter(propertyList: ArrayList<PromoCodeModel.Result>) {
        arrayList = propertyList
        notifyDataSetChanged()
    }


}