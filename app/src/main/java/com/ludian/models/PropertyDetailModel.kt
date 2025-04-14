package com.ludian.models

import com.ludian.models.PropertyModel.Property
import java.io.Serializable

class PropertyDetailModel( val message: String,
                           val properties: Property,
                           val status: Int,) : Serializable{

    data class Property(
        val address: String,
        val admin_status: String,
        val approval_status: String,
        val bathrooms: String,
        val bed_count: String,
        val bedrooms: String,
        val created_at: String,
        val description: String,
        val guests: String,
        val image_urls: List<String>,
        val latitude: String,
        val longitude: String,
        var price: String,
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
        val book_marked:Boolean,
    ) : Serializable

}