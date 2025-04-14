package com.ludian.ui.booking.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.ludian.R
import com.ludian.databinding.ItemReviewBinding
import com.ludian.models.ReviewModel
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException


class ReviewAdapter (
    private val mContext: Context,
    var arrayList: ArrayList<ReviewModel.Data>?,
) : RecyclerView.Adapter<ReviewAdapter.MyViewHolder>() {



    class MyViewHolder(var binding: ItemReviewBinding) :
        RecyclerView.ViewHolder(binding.root)


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding: ItemReviewBinding = DataBindingUtil.inflate(
            LayoutInflater.from(mContext), R.layout.item_review, parent, false
        )
        return MyViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return arrayList!!.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val inputDateFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd")
        val outputDateFormat = DateTimeFormatter.ofPattern("dd MMM yyyy")
        val inputDateString = arrayList!![position].current_date


        holder.binding.tvName.text = arrayList!![position].name
        holder.binding.tvComment.text = arrayList!![position].review
        holder.binding.ratingBar.rating = arrayList!![position].rating.toFloat()

        Glide.with(mContext)
            .load(arrayList!![position].image)
            .diskCacheStrategy(DiskCacheStrategy.ALL)
            .placeholder(com.denzcoskun.imageslider.R.drawable.default_loading)
            .error(com.denzcoskun.imageslider.R.drawable.default_error)
            .centerInside()
            .into(holder.binding.ivImg)

        try {
            val date = LocalDate.parse(inputDateString, inputDateFormat)
            val formattedDateString = date.format(outputDateFormat)
            holder.binding.tvDate.text = formattedDateString
            println(formattedDateString) // Output: 30 Aug 2024
        } catch (e: DateTimeParseException) {
            println("Invalid date format")
        }

    }

    fun notifyAdapter(reviewArrayList: ArrayList<ReviewModel.Data>) {
        arrayList = reviewArrayList
        notifyDataSetChanged()
    }

}