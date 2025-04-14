package com.ludian.api

import com.ludian.utils.Constants.CHANGE_PASSWORD_API
import com.ludian.utils.Constants.FORGOT_PASSWORD_API
import com.ludian.utils.Constants.LOGIN_API
import com.ludian.utils.Constants.OTP_VERIFY_API
import com.ludian.utils.Constants.SIGNUP_API
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.FieldMap
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

interface UserAPI {

    @FormUrlEncoded
    @POST(SIGNUP_API)
    suspend fun signup(@FieldMap params: Map<String, String>): Response<ResponseBody>

    @FormUrlEncoded
    @POST(LOGIN_API)
    suspend fun login(@FieldMap params: Map<String, String>): Response<ResponseBody>


    @FormUrlEncoded
    @POST(FORGOT_PASSWORD_API)
    suspend fun forgotPasswordApi(@FieldMap params: Map<String, String>): Response<ResponseBody>

    @FormUrlEncoded
    @POST(OTP_VERIFY_API)
    suspend fun otpVerifyApi(@FieldMap params: Map<String, String>): Response<ResponseBody>


    @FormUrlEncoded
    @POST(CHANGE_PASSWORD_API)
    suspend fun createNewPasswordApi(@FieldMap params: Map<String, String>): Response<ResponseBody>

   /* @FormUrlEncoded
    @POST(FORGOT_PASSWORD_API)
    suspend fun forgotPasswordApi(@FieldMap params: Map<String, String>): Response<ResponseBody>*/
}