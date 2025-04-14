package com.ludian.models

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class PromoCodeModel(
    val message: String,
    val result: List<Result>,
    val status: Int,
) : Serializable {
    data class Result(
      /*  @SerializedName("promocode_id") val id: String,
        @SerializedName("promocode_name") val promoCode: String,
        @SerializedName("promocode_value") val description: String,
        @SerializedName("promocode_type") val type: String,
        @SerializedName("used") val promoCodeStatus: Boolean,
        var check: Boolean = false*/

      @SerializedName("id") val id: String,
    @SerializedName("seller_id") val sellerId: String,
    @SerializedName("property_id") val propertyId: String,
    @SerializedName("title") val title: String,
    @SerializedName("discount_percent") val discountPercent: String,
    @SerializedName("start_date") val startDate: String,
    @SerializedName("end_date") val endDate: String,
      var check: Boolean = false

    )
}
