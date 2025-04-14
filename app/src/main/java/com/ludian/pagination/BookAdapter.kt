package com.ludian.pagination

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.ludian.databinding.EachRowBinding
import com.ludian.models.BookTestModel
import javax.inject.Inject


class BookAdapter @Inject constructor(): PagingDataAdapter<BookTestModel, BookAdapter.BookingViewHolder>(Diff()) {


    override fun onBindViewHolder(holder: BookingViewHolder, position: Int) {
        val dogs = getItem(position)
        if(dogs!= null){
            holder.binds(dogs)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BookingViewHolder  =
        BookingViewHolder(EachRowBinding.inflate(LayoutInflater.from(parent.context),parent,false))


    class BookingViewHolder(private val binding: EachRowBinding) : RecyclerView.ViewHolder(binding.root){
        fun binds(dogs:BookTestModel){
            binding.apply {
               // image.load(dogs.url)

            }
        }
    }

    class Diff : DiffUtil.ItemCallback<BookTestModel>(){
        override fun areItemsTheSame(oldItem: BookTestModel, newItem: BookTestModel): Boolean  =
            oldItem.url == newItem.url

        override fun areContentsTheSame(oldItem: BookTestModel, newItem: BookTestModel): Boolean =
            oldItem == newItem
    }
}