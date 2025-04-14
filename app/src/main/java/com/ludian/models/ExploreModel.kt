package com.ludian.models

import com.google.gson.annotations.SerializedName


data class ExploreModel(
    val result: List<Property>,
    val message: String,
    val status: Int
) {
    data class Property(
        @SerializedName("property_category_id") val propertyCategoryId: String,
        @SerializedName("name") val propertyCategoryName: String,
        @SerializedName("image") val propertyCategoryImage: String,
        @SerializedName("property_category_admin_status") val propertyCategoryAdminStatus: String,


    )
}