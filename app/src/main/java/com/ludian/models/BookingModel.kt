package com.ludian.models

import java.io.Serializable

data class BookingModel(
    val `data`: List<Data>,
    val message: String,
    val status: String
):Serializable {
    data class Data(
        val booking_request_children: String,
        val booking_request_created_at: String,
        val booking_request_date_end: String,
        val booking_request_date_start: String,
        val booking_request_deleted_at: Any,
        val booking_request_guests: String,
        val booking_request_id: String,
        val booking_request_promocode_id: Any,
        val booking_request_property_id: String,
        val booking_request_status: String,
        val booking_request_updated_at: String,
        val booking_request_user_id: String,
        val `property`: Property,
        val orderNumber: String,
        val orderSubTotal: String,
        val orderTotal: String,
        val platformFee: String,
        val taxFee: String,
        val payment_status: String,
        val promoCode_discount: String,
        val promoCode_amount: String,
        val checkout_date: String


        ) : Serializable {
        data class Property(
            val address: String,
            val admin_status: String,
            val approval_status: String,
            val bathrooms: String,
            val bed_count: String,
            val bedrooms: String,
            val created_at: String,
            val description: String,
            val discount_id: String,
            val guests: String,
            val image_names: String,
            val image_urls: List<String>,
            val latitude: String,
            val longitude: String,
            val price: String,
            val price_tax: String,
            val property_category_id: String,
            val property_id: String,
            val rent_id: String,
            val rent_string_name: String,
            val request_accept_type_id: String,
            val select_amenities_ids: String,
            val select_facility_ids: String,
            val select_safety_ids: String,
            val title: String,
            val updated_at: String,
            val user_id: String,
            val user_name: String,
            val squreMeter: String,
            val average_rating: String,
            val wifi: Boolean,
            val image:String

        ) : Serializable
    }
}