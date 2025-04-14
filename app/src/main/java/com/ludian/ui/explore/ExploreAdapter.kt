package com.ludian.ui.explore

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
import com.ludian.databinding.ItemExploreBinding
import com.ludian.models.ExploreModel
import com.ludian.models.PropertyModel


class ExploreAdapter (
    private val mContext: Context,
    var arrayList:ArrayList<ExploreModel.Property>?,
) : RecyclerView.Adapter<ExploreAdapter.MyViewHolder>() {



    class MyViewHolder(var binding: ItemExploreBinding) :
        RecyclerView.ViewHolder(binding.root)


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding: ItemExploreBinding = DataBindingUtil.inflate(
            LayoutInflater.from(mContext), R.layout.item_explore, parent, false
        )
        return MyViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return arrayList!!.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
         holder.binding.tvName.text = arrayList!![position].propertyCategoryName

        Glide.with(mContext)
            .load(arrayList!![position].propertyCategoryImage)
            .diskCacheStrategy(DiskCacheStrategy.ALL)
            .placeholder(com.denzcoskun.imageslider.R.drawable.default_loading)
            .error(com.denzcoskun.imageslider.R.drawable.default_error)
            .centerInside()
            .into(holder.binding.ivImg)


        holder.itemView.setOnClickListener {
            mContext.startActivity(Intent(mContext,ExplorePropertyAct::class.java)
                .putExtra("categoryId",arrayList!![position].propertyCategoryId))
        }
    }


    fun notifyAdapter(propertyList: ArrayList<ExploreModel.Property>) {
        arrayList = propertyList
        notifyDataSetChanged()
    }
}