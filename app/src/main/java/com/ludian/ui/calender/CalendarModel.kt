package com.ludian.ui.calender

import java.time.LocalDate

/*
data class CalendarModel(var monthName : String,val selectDate : LocalDate,var monthList: ArrayList<String>)
*/


data class CalendarModel(
    var monthName: String, val selectDate: LocalDate, var monthList: ArrayList<Days>
   // var discount: Discount?
){

    data class Days(var day : String,var date : String, var check : Boolean,var price :String,var whichTypeDate : String,var openBlock:String,var note:String,var discountValue:String?)

 /*   data class Discount(
        var id: String,
        var title: String,
        var subTitle: String,
        var discount: String,

        )*/

}