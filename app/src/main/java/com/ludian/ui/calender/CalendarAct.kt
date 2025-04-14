package com.ludian.ui.calender

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.google.gson.Gson
import com.ludian.R
import com.ludian.databinding.ActivityCalendarBinding
import com.ludian.models.PropertyModel
import com.ludian.ui.booking.CheckGuestAct
import com.ludian.viewmodels.UserDataViewModel
import com.ludian.utils.Helper
import com.ludian.utils.NetworkResult
import com.ludian.utils.SharedPrf
import dagger.hilt.android.AndroidEntryPoint
import org.json.JSONObject
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.util.Locale


@AndroidEntryPoint
class CalendarAct : AppCompatActivity(), /*CalendarAdapter.OnItemListener,*/
    VerticalCalendarAdapter.OnMonthListener {
    private lateinit var binding: ActivityCalendarBinding
    private lateinit var userDataViewModel: UserDataViewModel
    private var propertyModel: PropertyModel.Property? = null

    private var monthYearText: TextView? = null
    private var selectedDate: LocalDate? = null
    private var arrayList: ArrayList<CalendarModel>? = null
    private var checkPriceArrayList: ArrayList<CheckPriceModel.Property>? = null

    private var selectedDateArray: ArrayList<String>? = null


    private var price: String? = null
    private var propertyId: String? = null

    private var startDate: String = ""
    private lateinit var verticalCalendarAdapter: VerticalCalendarAdapter

    private val sharedPrf by lazy { SharedPrf(this@CalendarAct) }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_calendar)
        userDataViewModel = ViewModelProvider(this).get(UserDataViewModel::class.java)

        selectedDate = LocalDate.now()
        initWidgets()
        addMonth()

    }

    private fun initWidgets() {
        selectedDateArray = ArrayList()
        price = intent?.getStringExtra("price")
        propertyId = intent?.getStringExtra("propertyId")
        propertyModel = intent.getSerializableExtra("propertyData") as PropertyModel.Property?


        monthYearText = findViewById(R.id.monthYearTV)


        binding.ivBack.setOnClickListener {
            finish()
        }

        binding.btnNext.setOnClickListener {
            if (startDate != "") {
                calculationRangeDatePrice()
                startActivity(
                    Intent(this@CalendarAct, CheckGuestAct::class.java)
                        .putExtra("propertyData", propertyModel)
                        .putExtra("startDate", startDate)
                        .putExtra("endDate", startDate)
                )
            } else {
                Toast.makeText(
                    this@CalendarAct,
                    getString(R.string.please_select_date),
                    Toast.LENGTH_SHORT
                ).show()
            }
        }



        arrayList = ArrayList()
        checkPriceArrayList = ArrayList()

        verticalCalendarAdapter =
            VerticalCalendarAdapter(this@CalendarAct, arrayList, this@CalendarAct)
        binding.rvMainCalendar!!.adapter = verticalCalendarAdapter


        bindObservers()

        Helper.showProgressMessage(this@CalendarAct, getString(R.string.please_wait))
        userDataViewModel.getPropertyPriceWithDay(sharedPrf.getStoredTag(SharedPrf.TOKEN),propertyId!!)

    }

    private fun calculationRangeDatePrice() {
        var totalPrice = 0.0
        val startDateList = mutableListOf<String>()
        for (i in 0 until arrayList!!.size) {
            for (j in 0 until arrayList!![i].monthList.size) {
                if (arrayList!![i].monthList[j].check) {
                     totalPrice = totalPrice +  arrayList!![i].monthList[j].price.toDouble()
                    startDateList.add(arrayList!![i].monthList[j].date)
                    }
            }
        }
        propertyModel!!.price = totalPrice.toString()
        startDate = startDateList.joinToString(separator = ",")
    }

    private fun setMonthView() {
        /*  monthYearText!!.text = monthYearFromDate(selectedDate!!)


          val daysInMonth = daysInMonthArray(selectedDate!!)


              val calendarAdapter = CalendarAdapter(this@CalendarAct,daysInMonth, this)
          val layoutManager: RecyclerView.LayoutManager = GridLayoutManager(applicationContext, 7)
          calendarRecyclerView!!.layoutManager = layoutManager
          calendarRecyclerView!!.adapter = calendarAdapter*/
    }

    /*
    private fun daysInMonthArray(date: LocalDate): ArrayList<String> {
        val daysInMonthArray = ArrayList<String>()
        val yearMonth = YearMonth.from(date)
        val daysInMonth = yearMonth!!.lengthOfMonth()
        val firstOfMonth: LocalDate = selectedDate!!.withDayOfMonth(1)
        val dayOfWeek = firstOfMonth.dayOfWeek.value
        for (i in 1..42) {
            if (i <= dayOfWeek || i > daysInMonth + dayOfWeek) {
                daysInMonthArray.add("")
            } else {
                daysInMonthArray.add((i - dayOfWeek).toString())
            }
        }
        return daysInMonthArray
    }
*/


    private fun daysInMonthArray(date: LocalDate): ArrayList<CalendarModel.Days> {
        val daysInMonthArray = ArrayList<CalendarModel.Days>()
        val yearMonth = YearMonth.from(date)
        val daysInMonth = yearMonth!!.lengthOfMonth()
        val firstOfMonth: LocalDate = selectedDate!!.withDayOfMonth(1)
        val dayOfWeek = firstOfMonth.dayOfWeek.value
        Log.e("yearMonth====", yearMonth.toString())
        Log.e("daysInMonth====", daysInMonth.toString())
        Log.e("firstOfMonth====", firstOfMonth.toString())
        Log.e("dayOfWeek====", dayOfWeek.toString())
        Log.e("date====", date.toString())
        val dateList = getAllDatesMonth(yearMonth)
        for (i in 1..42) {
            if (i <= dayOfWeek || i > daysInMonth + dayOfWeek) {
                daysInMonthArray.add(
                    CalendarModel.Days(
                        "",
                        "",
                        false,
                        "",
                        "",
                        "0",
                        "",
                        "0"

                    )
                )
            } else {
                //  daysInMonthArray.add((i - dayOfWeek).toString())
                //  daysInMonthArray.add(CalendarModel.Days((i - dayOfWeek).toString(),false,price!!))
                val currentDate = LocalDate.now()
                val day = i - dayOfWeek
                var dateType: String = ""
                // Determine if this day is the current date
                Log.e("iiiiii", i.toString())
                Log.e("day", day.toString())

                val isCurrentDate =
                    (day == currentDate.dayOfMonth && yearMonth == YearMonth.from(currentDate))
                val isPastDate = currentDate.isBefore(dateList[day - 1])
                val isFutureDate = currentDate.isAfter(dateList[day - 1])

                if (isCurrentDate) {
                    Log.e("DateType", "${dateList[day - 1]} is the current date")
                    dateType = "current"
                } else if (!isPastDate) {
                    Log.e("DateType", "${dateList[day - 1]} is a past date")
                    //  daysInMonthArray.add(CalendarModel.Days(day.toString(),false,price!!,"past"))
                    dateType = "past"

                } else if (!isFutureDate) {
                    Log.e("DateType", "${dateList[day - 1]} is a future date")
                    // daysInMonthArray.add(CalendarModel.Days(day.toString(),false,price!!,"future"))
                    dateType = "future"

                }
                Log.e("DateType", dateType)

                daysInMonthArray.add(
                    CalendarModel.Days(
                        day.toString(), dateList[day - 1].toString(),
                        false, price!!, dateType, "0", "", "0.0"
                    )
                )

            }
        }
        return daysInMonthArray
    }


    private fun getAllDatesMonth(yearMonth: YearMonth): MutableList<LocalDate> {
        val allMonthDates = mutableListOf<LocalDate>()

        // Start date is the first day of the current month
        val startDate = LocalDate.of(yearMonth.year, yearMonth.month, 1)

        // Iterate through each day of the month
        var currentDatePointer = startDate
        while (currentDatePointer.isBefore(startDate.plusMonths(1))) {
            allMonthDates.add(currentDatePointer)
            currentDatePointer = currentDatePointer.plusDays(1)
        }

        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd", Locale.ENGLISH)
        allMonthDates.forEach {
            println(it.format(formatter))
        }

        return allMonthDates
    }


    private fun monthYearFromDate(date: LocalDate): String? {
        val formatter = DateTimeFormatter.ofPattern("MMMM yyyy")
        return date.format(formatter)
    }

    fun previousMonthAction(view: View?) {
        selectedDate = selectedDate!!.minusMonths(1)
        setMonthView()
    }

    fun nextMonthAction(view: View?) {
        selectedDate = selectedDate!!.plusMonths(1)
        setMonthView()
    }

    /*
        override fun onItemClick(position: Int, dayText: String?) {
            if (!dayText.equals("")) {
                val message = "Selected Date $dayText " + monthYearFromDate(
                    selectedDate!!
                )
                Toast.makeText(this, message, Toast.LENGTH_LONG).show()
            }
        }
    */

    private fun addMonth() {
        /* arrayList!!.add(
            CalendarModel(
                monthYearFromDate(selectedDate!!)!!,
                selectedDate!!,
                daysInMonthArray(selectedDate!!)
            )
        )
        for (i in 1..12) {
            selectedDate = selectedDate!!.plusMonths(1)
            arrayList!!.add(
                CalendarModel(
                    monthYearFromDate(selectedDate!!)!!,
                    selectedDate!!,
                    daysInMonthArray(selectedDate!!)
                )
            )
        }
        verticalCalendarAdapter.notifyDataSetChanged()*/


        arrayList!!.add(
            CalendarModel(
                monthYearFromDate(selectedDate!!)!!,
                selectedDate!!,
                daysInMonthArray(selectedDate!!)/*,CalendarModel.Discount("","","","0")*/
            )
        )
        for (i in 1..12) {
            selectedDate = selectedDate!!.plusMonths(1)
            arrayList!!.add(
                CalendarModel(
                    monthYearFromDate(selectedDate!!)!!,
                    selectedDate!!,
                    daysInMonthArray(selectedDate!!)/*,CalendarModel.Discount("","","","0")*/
                )
            )
        }
        if (checkPriceArrayList!!.size > 0) {
            for (i in 0 until arrayList!!.size) {
                for (j in 0 until arrayList!![i].monthList.size) {
                    for (k in 0 until checkPriceArrayList!!.size) {
                        if (checkPriceArrayList!![k].date == arrayList!![i].monthList[j].date) {
                            arrayList!![i].monthList[j].price = checkPriceArrayList!![k].price
                            arrayList!![i].monthList[j].openBlock =
                                checkPriceArrayList!![k].avlStatus
                            arrayList!![i].monthList[j].note = checkPriceArrayList!![k].note
                            // arrayList!![i].monthList[j].discountValue = discountModel!!.discount
                        }
                    }
                }
            }

        }

        /* if(discountModel!=null) {
            for (i in 0 until arrayList!!.size) {
                arrayList!![i].discount!!.id = discountModel!!.id
                arrayList!![i].discount!!.discount = discountModel!!.discount
                arrayList!![i].discount!!.title = discountModel!!.title
                arrayList!![i].discount!!.subTitle = discountModel!!.subTitle
            }

        }*/



        verticalCalendarAdapter.notifyValue(arrayList!!)


    }

    /* override fun onMonth(position: Int, selectDate: LocalDate, dayText: String?) {
        if (!dayText.equals("")) {
            val message = "Selected Date $dayText " + monthYearFromDate(
                selectDate
            )
            Toast.makeText(this, message, Toast.LENGTH_LONG).show()
        }
    }*/


    override fun onMonth(
        mainPosition: Int,
        position: Int,
        selectDate: LocalDate,
        dayText: String?,
        check: Boolean
    ) {
        if (!dayText.equals("")) {
            val message = "Selected Date $dayText " + monthYearFromDate(selectDate)
           // Toast.makeText(this@CalendarAct, message, Toast.LENGTH_LONG).show()
            try {
                val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
                selectedDateArray!!.add(arrayList!![mainPosition].monthList[position].date)
                Log.e("date selected22====", arrayList!![mainPosition].monthList[position].date)

                if (selectedDateArray!!.size <= 2) {
                    arrayList!![mainPosition].monthList[position].check = check
                    if (selectedDateArray!!.size == 2) {
                        val datesBetween = getDatesBetween(
                            LocalDate.parse(selectedDateArray!![0], formatter),
                            LocalDate.parse(selectedDateArray!![1], formatter)
                        )

                        val dateStrings: List<String> =
                            datesBetween.map { it.format(formatter) }
                        dateRangeSelect(dateStrings)
                    }
                } else {
                    selectedDateArray!!.removeAt(0)
                    for (i in 0 until arrayList!!.size) {
                        for (j in 0 until arrayList!![i].monthList.size) {
                            arrayList!![i].monthList[j].check = false
                        }
                    }
                    val datesBetween = getDatesBetween(
                        LocalDate.parse(selectedDateArray!![0], formatter),
                        LocalDate.parse(selectedDateArray!![1], formatter)
                    )

                    val dateStrings: List<String> =
                        datesBetween.map { it.format(formatter) }
                    dateRangeSelect(dateStrings)

                }

                //  arrayList!![mainPosition].monthList[position].check = check



                  startDate = arrayList!![mainPosition].monthList[position].date
                verticalCalendarAdapter.notifyValue(arrayList!!)

            } catch (e: Exception) {
                e.printStackTrace()
            }

        }
    }

    private fun dateRangeSelect(dateStrings: List<String>) {
        for (i in 0 until arrayList!!.size) {
            for (j in 0 until arrayList!![i].monthList.size) {
                for (k in 0 until dateStrings.size) {
                    if (dateStrings[k] == arrayList!![i].monthList[j].date) {
                        arrayList!![i].monthList[j].check = true
                    }
                }
            }
        }
    }

    private fun getDatesBetween(startDate: LocalDate, endDate: LocalDate): List<LocalDate> {
        val dates = mutableListOf<LocalDate>()
        var currentDate = startDate

        while (!currentDate.isAfter(endDate)) {
            dates.add(currentDate)
            currentDate = currentDate.plusDays(1)
        }

        return dates
    }


    private fun bindObservers() {
        userDataViewModel.propertyPriceWithDayLiveData.observe(this, Observer { it ->
            Helper.hideProgressMessage()
            when (it) {
                is NetworkResult.Success -> {
                    it.data?.let {
                        val jsonObject = JSONObject(it.string())
                        Log.e("get propertyPriceWithDay Response===", jsonObject.toString())
                        if (jsonObject.getInt("status") == 1) {

                            val checkPriceModel: CheckPriceModel = Gson().fromJson(
                                jsonObject.toString(),
                                CheckPriceModel::class.java
                            )
                            checkPriceArrayList!!.clear()
                            checkPriceArrayList!!.addAll(checkPriceModel.result)
                            // discountModel = checkPriceModel.discount
                            try {
                                if (checkPriceArrayList!!.size > 0) {
                                    for (i in 0 until arrayList!!.size) {
                                        for (j in 0 until arrayList!![i].monthList.size) {
                                            for (k in 0 until checkPriceArrayList!!.size) {
                                                if (checkPriceArrayList!![k].date == arrayList!![i].monthList[j].date) {
                                                    arrayList!![i].monthList[j].price =
                                                        checkPriceArrayList!![k].price
                                                    arrayList!![i].monthList[j].openBlock =
                                                        checkPriceArrayList!![k].avlStatus
                                                    arrayList!![i].monthList[j].note =
                                                        checkPriceArrayList!![k].note
                                                    //  if(discountModel!=null)   arrayList!![i].monthList[j].discountValue =  discountModel!!.discount
                                                    //   else arrayList!![i].monthList[j].discountValue =  "0.0"

                                                }
                                            }
                                        }


                                    }

                                }
                                verticalCalendarAdapter.notifyValue(arrayList!!)


                                /*
                                                                if(discountModel!=null) {
                                                                    Log.e("discount id=====",discountModel!!.discount)
                                                                    for (i in 0 until arrayList!!.size) {
                                                                        arrayList!![i].discount!!.id = discountModel!!.id
                                                                        arrayList!![i].discount!!.discount = discountModel!!.discount
                                                                        arrayList!![i].discount!!.title = discountModel!!.title
                                                                        arrayList!![i].discount!!.subTitle = discountModel!!.subTitle
                                                                    }


                                                                }
                                */

                            } catch (e: Exception) {
                                e.printStackTrace()
                            }


                            verticalCalendarAdapter.notifyValue(arrayList!!)
                            userDataViewModel.clearPriceData()
                        } else {
                            // Toast.makeText(requireActivity(), "" + jsonObject.getString("message"), Toast.LENGTH_SHORT).show()

                        }
                        //  Log.e("TAG", "observers: $it.")
                    }


                }

                is NetworkResult.Error -> {
                    showValidationErrors(it.message.toString())
                }

                is NetworkResult.Loading -> {
                    Helper.showProgressMessage(this@CalendarAct, getString(R.string.please_wait))
                }

                else -> {}
            }
        })


    }

    private fun showValidationErrors(error: String) {
        val text = String.format(resources.getString(R.string.txt_error_message, error))
        Toast.makeText(this, text, Toast.LENGTH_SHORT).show()
    }


}