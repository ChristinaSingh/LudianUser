package com.ludian.ui.calender

import android.content.Context
import android.graphics.Color
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.text.style.StrikethroughSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.ludian.R
import com.ludian.databinding.ItemCalendarBinding



class CalendarAdapter (
    private val mContext: Context,
    private val mainPosition : Int,
    var arrayList: ArrayList<CalendarModel.Days>?,
    private val listener : OnItemListener
) : RecyclerView.Adapter<CalendarAdapter.MyViewHolder>() {

     var checkCount: Int = 0


    class MyViewHolder(var binding: ItemCalendarBinding) :
        RecyclerView.ViewHolder(binding.root)


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding: ItemCalendarBinding = DataBindingUtil.inflate(
            LayoutInflater.from(mContext), R.layout.item_calendar, parent, false
        )

/* val layoutParams: ViewGroup.LayoutParams = parent.layoutParams
          layoutParams.height = (parent.height * 0.166666666).toInt()*/


        return MyViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return arrayList!!.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {



        if(arrayList!![position].day!="")  {
            holder.binding.llMain.visibility = View.VISIBLE
            holder.binding.cellDayText.text = arrayList!![position].day

            if(arrayList!![position].discountValue!=""){
                val orgPrice : Double = arrayList!![position].price.toDouble()
                val discountVal : Double = arrayList!![position].discountValue!!.toDouble()
                val discountPrice  = calculateDiscountedPrice(orgPrice,discountVal)
                // arrayList!![position].price = String.format("%.2f", discountPrice)
                holder.binding.tvPrice.text = String.format("%.2f", discountPrice)

            }

            else holder.binding.tvPrice.text = arrayList!![position].price
        }
        else {
            holder.binding.llMain.visibility = View.GONE
        }



        if(arrayList!![position].note!="") holder.binding.viewNote.visibility = View.VISIBLE
        else holder.binding.viewNote.visibility = View.GONE



        if(arrayList!![position].whichTypeDate=="current") {

            if(arrayList!![position].check){
                if(arrayList!![position].openBlock=="1"|| arrayList!![position].openBlock=="3"){
                    val spannableString = SpannableString(arrayList!![position].day)
                    // Apply strikethrough
                    spannableString.setSpan(StrikethroughSpan(), 0, arrayList!![position].day.length, 0)
                    // Apply color to the strikethrough text
                    spannableString.setSpan(ForegroundColorSpan(Color.WHITE), 0, arrayList!![position].day.length, 0)
                    // Set the SpannableString to TextView
                    holder.binding.cellDayText.text = spannableString
                }

                holder.binding.llMain.setBackgroundColor(mContext.getColor(R.color.color_primary))
                holder.binding.cellDayText.setTextColor(mContext.getColor(R.color.white))
                holder.binding.tvPrice.setTextColor(mContext.getColor(R.color.white))

            }
            else {
                val bg =  mContext.getDrawable(R.drawable.rounded_brown_with0)
                holder.binding.llMain.background = bg
                holder.binding.cellDayText.setTextColor(mContext.getColor(R.color.black))
                holder.binding.tvPrice.setTextColor(mContext.getColor(R.color.black))
                if(arrayList!![position].openBlock=="1" || arrayList!![position].openBlock=="3"){
                    val spannableString = SpannableString(arrayList!![position].day)
                    // Apply strikethrough
                    spannableString.setSpan(StrikethroughSpan(), 0, arrayList!![position].day.length, 0)
                    // Apply color to the strikethrough text
                    spannableString.setSpan(ForegroundColorSpan(Color.BLACK), 0, arrayList!![position].day.length, 0)
                    // Set the SpannableString to TextView
                    holder.binding.cellDayText.text = spannableString
                }

            }
        }
        else if(arrayList!![position].whichTypeDate=="future") {
            if(arrayList!![position].check){
                if(arrayList!![position].openBlock=="1" || arrayList!![position].openBlock=="3"){
                    val spannableString = SpannableString(arrayList!![position].day)
                    // Apply strikethrough
                    spannableString.setSpan(StrikethroughSpan(), 0, arrayList!![position].day.length, 0)
                    // Apply color to the strikethrough text
                    spannableString.setSpan(ForegroundColorSpan(Color.WHITE), 0, arrayList!![position].day.length, 0)
                    // Set the SpannableString to TextView
                    holder.binding.cellDayText.text = spannableString
                }

                holder.binding.llMain.setBackgroundColor(mContext.getColor(R.color.color_primary))
                holder.binding.cellDayText.setTextColor(mContext.getColor(R.color.white))
                holder.binding.tvPrice.setTextColor(mContext.getColor(R.color.white))


            }
            else {
                holder.binding.llMain.setBackgroundColor(mContext.getColor(R.color.color_gray_calendar))
                holder.binding.cellDayText.setTextColor(mContext.getColor(R.color.black))
                holder.binding.tvPrice.setTextColor(mContext.getColor(R.color.black))
                if(arrayList!![position].openBlock=="1" || arrayList!![position].openBlock=="3"){
                    val spannableString = SpannableString(arrayList!![position].day)
                    // Apply strikethrough
                    spannableString.setSpan(StrikethroughSpan(), 0, arrayList!![position].day.length, 0)
                    // Apply color to the strikethrough text
                    spannableString.setSpan(ForegroundColorSpan(Color.BLACK), 0, arrayList!![position].day.length, 0)
                    // Set the SpannableString to TextView
                    holder.binding.cellDayText.text = spannableString
                }

            }
        }
        else{
            holder.binding.llMain.setBackgroundColor(mContext.getColor(R.color.color_gray_light7))
            if(arrayList!![position].openBlock=="1" || arrayList!![position].openBlock=="3"){
                val spannableString = SpannableString(arrayList!![position].day)
                // Apply strikethrough
                spannableString.setSpan(StrikethroughSpan(), 0, arrayList!![position].day.length, 0)
                // Apply color to the strikethrough text
                spannableString.setSpan(ForegroundColorSpan(Color.BLACK), 0, arrayList!![position].day.length, 0)
                // Set the SpannableString to TextView
                holder.binding.cellDayText.text = spannableString
            }

        }




        holder.itemView.setOnClickListener {
            // openBlock == 3 booking not available on this date
            if(arrayList!![position].whichTypeDate=="current" || arrayList!![position].whichTypeDate=="future") {
                if (arrayList!![position].openBlock == "3") {
                   Toast.makeText(mContext,mContext.getString(R.string.booking_not_available_on_this_date),Toast.LENGTH_LONG).show()
                }
                else{
                    listener.onItemClick(mainPosition, position, arrayList!![position].day, true)
                }
            }
        }
    }


    interface OnItemListener {
        fun onItemClick(mainPosition: Int,position: Int, dayText: String?,check : Boolean)
    }

    private fun calculateDiscountedPrice(originalPrice: Double, discountPercentage: Double): Double {
        // Ensure the discount percentage is between 0 and 100
        require(discountPercentage in 0.0..100.0) { "Discount percentage must be between 0 and 100" }

        // Calculate the discount amount
        val discountAmount = originalPrice * (discountPercentage / 100)

        // Subtract the discount amount from the original price
        return originalPrice - discountAmount
    }


}



///change for range dummy

/*class CalendarAdapter (
    private val mContext: Context,
    private val mainPosition: Int,
    var arrayList: ArrayList<CalendarModel.Days>?,
    private val listener: OnItemListener
) : RecyclerView.Adapter<CalendarAdapter.MyViewHolder>() {

    private var selectedStartDate: Int? = null
    private var selectedEndDate: Int? = null
    private var isSelectingRange = false

    class MyViewHolder(var binding: ItemCalendarBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding: ItemCalendarBinding = DataBindingUtil.inflate(
            LayoutInflater.from(mContext), R.layout.item_calendar, parent, false
        )
        return MyViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return arrayList?.size ?: 0
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val dayItem = arrayList!![position]
        val dayNumber = dayItem.day.toIntOrNull() ?: return

        // Highlight selected range
        val isWithinRange = isDateInRange(dayNumber)
        val isStart = dayNumber == selectedStartDate
        val isEnd = dayNumber == selectedEndDate

        if (dayItem.day.isNotEmpty()) {
            holder.binding.llMain.visibility = View.VISIBLE
            holder.binding.cellDayText.text = dayItem.day

            // Set price
            if (dayItem.discountValue!!.isNotEmpty()) {
                val orgPrice: Double = dayItem.price.toDouble()
                val discountVal: Double = dayItem.discountValue!!.toDouble()
                val discountPrice = calculateDiscountedPrice(orgPrice, discountVal)
                holder.binding.tvPrice.text = String.format("%.2f", discountPrice)
            } else {
                holder.binding.tvPrice.text = dayItem.price
            }

            // Set background and text color based on selection
            if (isWithinRange) {
                holder.binding.llMain.setBackgroundColor(mContext.getColor(R.color.color_selected_range))
                holder.binding.cellDayText.setTextColor(mContext.getColor(R.color.white))
            } else {
              //  holder.binding.llMain.setBackgroundColor(getDefaultBackground(dayItem))
             //   holder.binding.cellDayText.setTextColor(getDefaultTextColor(dayItem))
            }

            // Highlight start and end dates
            if (isStart) {
                holder.binding.llMain.setBackgroundColor(mContext.getColor(R.color.color_start_date))
            } else if (isEnd) {
                holder.binding.llMain.setBackgroundColor(mContext.getColor(R.color.color_end_date))
            }
        } else {
            holder.binding.llMain.visibility = View.GONE
        }

        holder.itemView.setOnClickListener {
            handleDateClick(dayNumber)
        }
    }

    private fun isDateInRange(date: Int): Boolean {
        return selectedStartDate != null && selectedEndDate != null && date in (selectedStartDate!!..selectedEndDate!!)
    }

    private fun handleDateClick(date: Int) {
        if (selectedStartDate == null) {
            // First date selected
            selectedStartDate = date
            selectedEndDate = date
            isSelectingRange = true
        } else if (isSelectingRange) {
            // Second date selected
            if (date >= selectedStartDate!!) {
                selectedEndDate = date
            } else {
                // Reverse the range
                selectedStartDate = date
                selectedEndDate = date
            }
            notifyDataSetChanged() // Update the UI to reflect the range
        } else {
            // Update the range
            selectedStartDate = date
            selectedEndDate = date
        }

        // Notify listener if needed
        listener.onItemClick(mainPosition, date, arrayList!![date - 1].day, isSelectingRange)
    }

    private fun getDefaultBackground(dayItem: CalendarModel.Days): Int {
        return when (dayItem.whichTypeDate) {
            "current" -> mContext.getColor(if (dayItem.check) R.color.color_primary else R.drawable.rounded_brown_with0)
            "future" -> mContext.getColor(if (dayItem.check) R.color.color_primary else R.color.color_gray_calendar)
            else -> mContext.getColor(R.color.color_gray_light7)
        }
    }

    private fun getDefaultTextColor(dayItem: CalendarModel.Days): Int {
        return if (dayItem.check) mContext.getColor(R.color.white) else mContext.getColor(R.color.black)
    }

    interface OnItemListener {
        fun onItemClick(mainPosition: Int, position: Int, dayText: String?, check: Boolean)
    }

    private fun calculateDiscountedPrice(originalPrice: Double, discountPercentage: Double): Double {
        require(discountPercentage in 0.0..100.0) { "Discount percentage must be between 0 and 100" }
        val discountAmount = originalPrice * (discountPercentage / 100)
        return originalPrice - discountAmount
    }
}*/
