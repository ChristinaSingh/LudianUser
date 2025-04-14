package com.ludian.ui.chat

data class ChatMsgModel(
    val message: String,
    val result: List<Result>,
    val status: Int

){
    data class Result(
        val booking_id: String,
        val chat_audio: String,
        val chat_document: String,
        val chat_image: String,
        val chat_message: String,
        val chat_video: String,
        val clear_chat: String,
        val contact: String,
        val current_date_time: String,
        val date: String,
        val id: String,
        val lat: String,
        val lon: String,
        val name: String,
        val receiver_detail: ReceiverDetail,
        val receiver_id: String,
        val sender_detail: SenderDetail,
        val sender_id: String,
        val status: String,
        val time_ago: String
    ){
        data class ReceiverDetail(
            val id: String,
            val image: String,
            val user_name: String
        )


        data class SenderDetail(
            val id: String,
            val image: String,
            val user_name: String
        )

    }
}