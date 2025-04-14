package com.ludian.ui.calender

import com.google.gson.annotations.SerializedName

data class CheckPriceModel(@SerializedName("data") val result: List<Property>,
                        /*   @SerializedName("discount_detail") val discount: Discount,*/
                           val message: String,
                           val status: Int
) {
    data class Property(
        @SerializedName("date_availability_id") val id: String,
        @SerializedName("date_availability_date") val date: String,
        @SerializedName("date_availability_price") val price: String,
        @SerializedName("date_availability_property_id") val propertyId: String,
        @SerializedName("date_availability_status") val avlStatus: String,
        @SerializedName("date_availability_note") val note: String,

        )

 /*   data class Discount(
        @SerializedName("property_discount_id") val id: String,
        @SerializedName("property_discount_title") val title: String,
        @SerializedName("property_discount_description") val subTitle: String,
        @SerializedName("property_discount_value") val discount: String,

        )*/

}
