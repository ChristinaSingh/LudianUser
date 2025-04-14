package com.ludian.models

data class BookTestModel(
    val breeds:List<Breeds>?,
    val url:String?
)

data class Breeds(
    val id:Int?,
    val name:String?

)
