package com.ludian.models

import java.io.Serializable

data class NotificationModel(
    val `data`: List<Data>,
    val message: String,
    val status: String
):Serializable {
    data class Data(
        val id: String,
        val user_id: String,
        val owner_id: String,
        val description: String,
        val description_ar: String,

        val date_time: String

    )
}

