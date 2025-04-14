package com.ludian.api

import com.ludian.models.BookTestModel
import com.ludian.utils.Constants
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.FieldMap
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Query

interface UserDataApi {

    @GET(Constants.GET_USER_PROFILE_API)
    suspend fun getUserProfileApi(@Query("token") token :String): Response<ResponseBody>


    @Multipart
    @POST(Constants.UPDATE_PROFILE_API)
    suspend fun updateProfileApi(
        @Part("token") token :RequestBody,
        @Part("user_id") userId: RequestBody,
        @Part("first_name") firstName: RequestBody,
        @Part("last_name") lastName: RequestBody,
        @Part("email") email: RequestBody,
        @Part("mobile") mobile: RequestBody,
        @Part profilePicture: MultipartBody.Part
    ): Response<ResponseBody>

    @FormUrlEncoded
    @POST(Constants.GET_ALL_NEAREST_PROPERTY_API)
    suspend fun getAllNearestPropertyApi(@FieldMap params: Map<String, String>): Response<ResponseBody>

    @GET(Constants.GET_ALL_NEAR_RECENT_SEARCH_API)
    suspend fun getAllNearRecentSearchApi(@Query("user_id") userId: String): Response<ResponseBody>

    @GET(Constants.GET_ALL_MOST_POPULAR_PROPERTY_API)
    suspend fun getAllMostPopularPropertyApi(@Query("user_id") userId: String): Response<ResponseBody>

    @GET(Constants.GET_PROPERTY_PRICE_WITH_DAY_API)
    suspend fun getPropertyPriceWithDayApi(
        @Query("token") token: String,
        @Query("property_id") propertyId: String): Response<ResponseBody>

     @FormUrlEncoded
     @POST(Constants.PROPERTY_BOOKING_API)
    suspend fun propertyBookingApi(@FieldMap params: Map<String, String>): Response<ResponseBody>



    /*@GET(Constants.GET_PROMO_CODE_API)
    suspend fun getPromoCodeApi(): Response<ResponseBody>*/

    @GET(Constants.GET_PROMO_CODE_API)
    suspend fun getPromoCodeApi(@Query("token") token:String,@Query("property_id") propertyId: String): Response<ResponseBody>



   /* @GET(Constants.APPLY_PROMO_CODE_API)
    suspend fun applyPromoCodeApi(@Query("user_id") userId: String,@Query("promocode") promoCode: String): Response<ResponseBody>*/

    @FormUrlEncoded
    @POST(Constants.APPLY_PROMO_CODE_API)
    suspend fun applyPromoCodeApi(@FieldMap params: Map<String, String>): Response<ResponseBody>



    @FormUrlEncoded
    @POST(Constants.GET_BOOKING_API)
    suspend fun getBookingApi(@FieldMap params: Map<String, String>): Response<ResponseBody>

    @FormUrlEncoded
    @POST(Constants.BOOKING_COMPLETE_API)
    suspend fun bookingCompleteApi(@FieldMap params: Map<String, String>): Response<ResponseBody>



    @FormUrlEncoded
    @POST(Constants.ADD_TO_BOOKMARK_API)
    suspend fun addToBookmarkApi(@FieldMap params: Map<String, String>): Response<ResponseBody>

    @GET(Constants.GET_BOOKMARK_API)
    suspend fun getBookmarkApi(@Query("token") token:String): Response<ResponseBody>


    @GET(Constants.SEARCH_PROPERTY_API)
    suspend fun searchPropertyApi(@Query("token") token: String,
                                  @Query("name") name: String,
                                  @Query("rating") rating: String,
                                  @Query("lat") lat: String,
                                  @Query("lon") lon: String,
                                  @Query("price") price: String
                                  ): Response<ResponseBody>

    @GET(Constants.SAVE_RECENT_SAVE_API)
    suspend fun saveRecentSearchApi(@Query("token") token: String,
        @Query("property_id") propertyId: String): Response<ResponseBody>


    @FormUrlEncoded
    @POST(Constants.ADD_PROPERTY_REVIEW_API)
    suspend fun addReviewApi(@FieldMap params: Map<String, String>): Response<ResponseBody>



    @FormUrlEncoded
    @POST(Constants.GET_PROPERTY_REVIEW_API)
    suspend fun getReviewApi(@FieldMap params: Map<String, String>): Response<ResponseBody>


    @FormUrlEncoded
    @POST(Constants.PASSWORD_CHANGE_API)
    suspend fun changePasswordApi(@FieldMap params: Map<String, String>): Response<ResponseBody>

    @GET(Constants.PRIVACY_POLICY_API)
    suspend fun privacyPolicyApi(@Query("token")token: String): Response<ResponseBody>

    @GET(Constants.GET_FAQ_API)
    suspend fun faqApi(@Query("token")token: String): Response<ResponseBody>

    @GET(Constants.ABOUT_US_API)
    suspend fun aboutUsApi(@Query("token")token: String): Response<ResponseBody>

    @GET(Constants.EXPLORE_API)
    suspend fun exploreApi(@Query("token")token: String): Response<ResponseBody>

    @FormUrlEncoded
    @POST(Constants.EXPLORE_PROPERTY_API)
    suspend fun explorePropertyListApi(@FieldMap params: Map<String, String>): Response<ResponseBody>

    @FormUrlEncoded
    @POST(Constants.NOTIFICATION_API)
    suspend fun notificationApi(@FieldMap params: Map<String, String>): Response<ResponseBody>


    @FormUrlEncoded
    @POST(Constants.PAYMENT_API)
    suspend fun paymentApi(@FieldMap params: Map<String, String>): Response<ResponseBody>


    @FormUrlEncoded
    @POST(Constants.CHAT_NOTIFICATION_API)
    suspend fun sendNotificationChatApi(@FieldMap params: Map<String, String>): Response<ResponseBody>


    @FormUrlEncoded
    @POST(Constants.PROPERTY_DETAIL_API)
    suspend fun getPropertyDetailApi(@FieldMap params: Map<String, String>): Response<ResponseBody>

    @FormUrlEncoded
    @POST(Constants.ALL_CHAT_MSG_API)
    suspend fun allChatMsgApi(@FieldMap params: Map<String, String>): Response<ResponseBody>

    @GET(Constants.TAX_API)
    suspend fun taxDataApi(@Query("token")token: String): Response<ResponseBody>

    @GET("")
    suspend fun getBookDataApi(@Query("page") page: Int,
                               @Query("limit") limit: Int): List<BookTestModel>


}