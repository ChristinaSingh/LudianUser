package com.ludian.models

data class ReviewModel(
    val `data`: List<Data>,
    val message: String,
    val status: String
){
    data class Data(
        val current_date: String,
        val date_time: String,
        val id: String,
        val image: String,
        val mobile: String,
        val name: String,
        val property_id: String,
        val rating: String,
        val review: String,
        val user_id: String
    )
}