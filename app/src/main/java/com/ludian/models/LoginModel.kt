package com.ludian.models

import com.google.gson.annotations.SerializedName

data class LoginModel(
    val message: String,
    @SerializedName("data") val userData: UserData,
    val status: String
) {
    data class UserData(
        @SerializedName("id") val id: String,
        @SerializedName("email") val email: String,
        @SerializedName("first_name") val fName: String,
        @SerializedName("last_name") val lName: String,
        @SerializedName("mobile") val mobile: String,
        @SerializedName("type") val type: String,
        @SerializedName("access_token") val token: String

    )

}
