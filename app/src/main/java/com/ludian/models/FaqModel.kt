package com.ludian.models

data class FaqModel(
    val `data`: List<Data>,
    val message: String,
    val status: String
){
    data class Data(
        val ans: String,
        val id: String,
        val qus: String
    )
}