package com.ludian.ui.calender

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.ludian.R
import com.ludian.databinding.ItemCalMainBinding
import com.ludian.databinding.ItemCalendarBinding
import java.time.LocalDate


/*
class VerticalCalendarAdapter (
    private val mContext: Context,
    var arrayList: ArrayList<CalendarModel>?,
    private val listener : OnMonthListener
) : RecyclerView.Adapter<VerticalCalendarAdapter.MyViewHolder>(), CalendarAdapter.OnItemListener {



    class MyViewHolder(var binding: ItemCalMainBinding) :
        RecyclerView.ViewHolder(binding.root)


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding: ItemCalMainBinding = DataBindingUtil.inflate(
            LayoutInflater.from(mContext), R.layout.item_cal_main, parent, false
        )
        */
/*  val layoutParams: ViewGroup.LayoutParams = parent.layoutParams
          layoutParams.height = (parent.height * 0.166666666).toInt()*//*


        return MyViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return arrayList!!.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {

        holder.binding.monthYearTV.text = arrayList!![position].monthName

        holder.binding.calendarRecyclerView.adapter = CalendarAdapter(mContext,position,arrayList!![position].monthList,this@VerticalCalendarAdapter)

    }


    interface OnMonthListener {
        fun onMonth(position: Int,selectDate : LocalDate ,dayText: String?,)
    }

    override fun onItemClick(mamainPosition: Int,position: Int, dayText: String?) {
        listener.onMonth(position,arrayList!![mamainPosition].selectDate,dayText)
    }



    fun notifyValue(list: ArrayList<CalendarModel>){
        arrayList = list
        notifyDataSetChanged()
    }

}*/


class VerticalCalendarAdapter (
    private val mContext: Context,
    var arrayList: ArrayList<CalendarModel>?,
    private val listener : OnMonthListener
) : RecyclerView.Adapter<VerticalCalendarAdapter.MyViewHolder>(), CalendarAdapter.OnItemListener {



    class MyViewHolder(var binding: ItemCalMainBinding) :
        RecyclerView.ViewHolder(binding.root)


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding: ItemCalMainBinding = DataBindingUtil.inflate(
            LayoutInflater.from(mContext), R.layout.item_cal_main, parent, false
        )
        /*  val layoutParams: ViewGroup.LayoutParams = parent.layoutParams
          layoutParams.height = (parent.height * 0.166666666).toInt()*/

        return MyViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return arrayList!!.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {

        holder.binding.monthYearTV.text = arrayList!![position].monthName

        holder.binding.calendarRecyclerView.adapter = CalendarAdapter(mContext,position,arrayList!![position].monthList,this@VerticalCalendarAdapter)

       /* holder.binding.tvDiscount.setOnClickListener {
            listener.onDiscount(position)
        }*/
    }


    interface OnMonthListener {
        fun onMonth(mainPosition: Int,position: Int, selectDate : LocalDate, dayText: String?,check: Boolean)
      //  fun onDiscount(mainPosition: Int)

    }

    override fun onItemClick(mainPosition: Int,position: Int, dayText: String?,check : Boolean) {
        listener.onMonth(mainPosition,position,arrayList!![mainPosition].selectDate,dayText,check)
    }

    fun notifyValue(list: ArrayList<CalendarModel>){
        arrayList = list
        notifyDataSetChanged()
    }

}