package com.ludian.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.ludian.api.UserDataApi
import com.ludian.utils.NetworkResult
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import org.json.JSONObject
import retrofit2.Response
import javax.inject.Inject

class UserDataRepository @Inject constructor(private val userDataApi: UserDataApi) {

    private val _userDataLiveData = MutableLiveData<NetworkResult<ResponseBody>?>()
    val userResponseLiveData: LiveData<NetworkResult<ResponseBody>?>
        get() = _userDataLiveData


    private val _storyListLiveData = MutableLiveData<NetworkResult<ResponseBody>?>()
    val storyListLiveData: LiveData<NetworkResult<ResponseBody>?>
        get() = _storyListLiveData


    private val _recentSearchLiveData = MutableLiveData<NetworkResult<ResponseBody>?>()
    val recentSearchLiveData: LiveData<NetworkResult<ResponseBody>?>
        get() = _recentSearchLiveData


    private val _popularPropertyLiveData = MutableLiveData<NetworkResult<ResponseBody>?>()
    val popularPropertyLiveData: LiveData<NetworkResult<ResponseBody>?>
        get() = _popularPropertyLiveData


    private val _propertyPriceWithDayLiveData = MutableLiveData<NetworkResult<ResponseBody>?>()
    val propertyPriceWithDayLiveData: LiveData<NetworkResult<ResponseBody>?>
        get() = _propertyPriceWithDayLiveData



    private val _propertyBookingLiveData = MutableLiveData<NetworkResult<ResponseBody>?>()
    val propertyBookingLiveData: LiveData<NetworkResult<ResponseBody>?>
        get() = _propertyBookingLiveData

    private val _promoCodeLiveData = MutableLiveData<NetworkResult<ResponseBody>?>()
    val promoCodeLiveData: LiveData<NetworkResult<ResponseBody>?>
        get() = _promoCodeLiveData


    private val _bookingCompleteLiveData = MutableLiveData<NetworkResult<ResponseBody>?>()
    val bookingCompleteLiveData: LiveData<NetworkResult<ResponseBody>?>
        get() = _bookingCompleteLiveData


    private val _addBookmarkLiveData = MutableLiveData<NetworkResult<ResponseBody>?>()
    val addBookmarkLiveData: LiveData<NetworkResult<ResponseBody>?>
        get() = _addBookmarkLiveData

    private val _bookmarkLiveData = MutableLiveData<NetworkResult<ResponseBody>?>()
    val bookmarkLiveData: LiveData<NetworkResult<ResponseBody>?>
        get() = _bookmarkLiveData

    private val _searchLiveData = MutableLiveData<NetworkResult<ResponseBody>?>()
    val searchLiveData: LiveData<NetworkResult<ResponseBody>?>
        get() = _searchLiveData


    private val _addReviewLiveData = MutableLiveData<NetworkResult<ResponseBody>?>()
    val addReviewLiveData: LiveData<NetworkResult<ResponseBody>?>
        get() = _addReviewLiveData


    private val _reviewLiveData = MutableLiveData<NetworkResult<ResponseBody>?>()
    val reviewLiveData: LiveData<NetworkResult<ResponseBody>?>
        get() = _reviewLiveData


    private val _changePasswordLiveData = MutableLiveData<NetworkResult<ResponseBody>?>()
    val changePasswordLiveData: LiveData<NetworkResult<ResponseBody>?>
        get() = _changePasswordLiveData


    private val _faqLiveData = MutableLiveData<NetworkResult<ResponseBody>?>()
    val faqLiveData: LiveData<NetworkResult<ResponseBody>?>
        get() = _faqLiveData


    private val _aboutUsLiveData = MutableLiveData<NetworkResult<ResponseBody>?>()
    val aboutUsLiveData: LiveData<NetworkResult<ResponseBody>?>
        get() = _aboutUsLiveData


    private val _privacyPolicyLiveData = MutableLiveData<NetworkResult<ResponseBody>?>()
    val privacyPolicyLiveData: LiveData<NetworkResult<ResponseBody>?>
        get() = _privacyPolicyLiveData



    private val _exploreLiveData = MutableLiveData<NetworkResult<ResponseBody>?>()
    val exploreLiveData: LiveData<NetworkResult<ResponseBody>?>
        get() = _exploreLiveData


    private val _explorePropertyLiveData = MutableLiveData<NetworkResult<ResponseBody>?>()
    val explorePropertyLiveData: LiveData<NetworkResult<ResponseBody>?>
        get() = _explorePropertyLiveData


    private val _notificationLiveData = MutableLiveData<NetworkResult<ResponseBody>?>()
    val notificationLiveData: LiveData<NetworkResult<ResponseBody>?>
        get() = _notificationLiveData


    private val _paymentLiveData = MutableLiveData<NetworkResult<ResponseBody>?>()
    val paymentLiveData: LiveData<NetworkResult<ResponseBody>?>
        get() = _paymentLiveData

    private val _propertyDetailLiveData = MutableLiveData<NetworkResult<ResponseBody>?>()
    val propertyDetailLiveData: LiveData<NetworkResult<ResponseBody>?>
        get() = _propertyDetailLiveData

    private val _chatLiveData = MutableLiveData<NetworkResult<ResponseBody>?>()
    val chatLiveData: LiveData<NetworkResult<ResponseBody>?>
        get() = _chatLiveData

    private val _allChatMsgLiveData = MutableLiveData<NetworkResult<ResponseBody>?>()
    val allChatMsgLiveData: LiveData<NetworkResult<ResponseBody>?>
        get() = _allChatMsgLiveData


    private val _taxLiveData = MutableLiveData<NetworkResult<ResponseBody>?>()
    val taxLiveData: LiveData<NetworkResult<ResponseBody>?>
        get() = _taxLiveData



    suspend fun getUserProfileRepo(token:String) {
        _userDataLiveData.postValue(NetworkResult.Loading())
        val response = userDataApi.getUserProfileApi(token)
        handleResponse(response)
    }


    suspend fun updateProfileRepo(
        token: RequestBody,
        userId: RequestBody,
        firstName: RequestBody,
        lastName: RequestBody,
        email: RequestBody,
        mobile: RequestBody,
        profilePicture: MultipartBody.Part
    ) {
        _userDataLiveData.postValue(NetworkResult.Loading())
        val response = userDataApi.updateProfileApi(token,userId,firstName,lastName,email,mobile,profilePicture)
        handleResponse(response)
    }

    suspend fun getNearestPropertyRepo(map: Map<String, String>) {
        _storyListLiveData.postValue(NetworkResult.Loading())
        val response = userDataApi.getAllNearestPropertyApi(map)
        if (response.isSuccessful && response.body() != null) {
            _storyListLiveData.postValue(NetworkResult.Success(response.body()!!))
        } else if (response.errorBody() != null) {
            val errorObj = JSONObject(response.errorBody()!!.charStream().readText())
            _storyListLiveData.postValue(NetworkResult.Error(errorObj.getString("message")))
        } else {
            _storyListLiveData.postValue(NetworkResult.Error("Something Went Wrong"))
        }
    }



    suspend fun getAllRecentSearchRepo(userId:String) {
        _recentSearchLiveData.postValue(NetworkResult.Loading())
        val response = userDataApi.getAllNearRecentSearchApi(userId)
        handleResponse1(response)
    }


    suspend fun getAllMostPopularPropertyRepo(userId: String) {
        _popularPropertyLiveData.postValue(NetworkResult.Loading())
        val response = userDataApi.getAllMostPopularPropertyApi(userId)
        handleResponse2(response)
    }


    suspend fun getPropertyPriceWithDayRepo(token:String,propertyId: String) {
        _propertyPriceWithDayLiveData.postValue(NetworkResult.Loading())
        val response = userDataApi.getPropertyPriceWithDayApi(token,propertyId)
        if (response.isSuccessful && response.body() != null) {
            _propertyPriceWithDayLiveData.postValue(NetworkResult.Success(response.body()!!))
        } else if (response.errorBody() != null) {
            val errorObj = JSONObject(response.errorBody()!!.charStream().readText())
            _propertyPriceWithDayLiveData.postValue(NetworkResult.Error(errorObj.getString("message")))
        } else {
            _propertyPriceWithDayLiveData.postValue(NetworkResult.Error("Something Went Wrong"))
        }
    }


    suspend fun propertyBookingRepo(map:Map<String,String>) {
        _propertyBookingLiveData.postValue(NetworkResult.Loading())
        val response = userDataApi.propertyBookingApi(map)
        handleResponse3(response)
    }


    suspend fun getPromoCodeRepo(token:String,propertyId: String) {
        _userDataLiveData.postValue(NetworkResult.Loading())
        val response = userDataApi.getPromoCodeApi(token,propertyId)
        handleResponse(response)
    }


   /* suspend fun applyPromoCodeRepo(userId:String,promoCode:String) {
        _promoCodeLiveData.postValue(NetworkResult.Loading())
        val response = userDataApi.applyPromoCodeApi(userId,promoCode)
        handleResponse4(response)
    }*/


    suspend fun applyPromoCodeRepo(map:Map<String,String>) {
        _promoCodeLiveData.postValue(NetworkResult.Loading())
        val response = userDataApi.applyPromoCodeApi(map)
        handleResponse4(response)
    }


    suspend fun getBookingRepo(map:Map<String,String>) {
        _propertyBookingLiveData.postValue(NetworkResult.Loading())
        val response = userDataApi.getBookingApi(map)
        handleResponse3(response)
    }

    suspend fun bookingCompleteRepo(param:Map<String,String>) {
        _bookingCompleteLiveData.postValue(NetworkResult.Loading())
        val response = userDataApi.bookingCompleteApi(param)
        if (response.isSuccessful && response.body() != null) {
            _bookingCompleteLiveData.postValue(NetworkResult.Success(response.body()!!))
        } else if (response.errorBody() != null) {
            val errorObj = JSONObject(response.errorBody()!!.charStream().readText())
            _bookingCompleteLiveData.postValue(NetworkResult.Error(errorObj.getString("message")))
        } else {
            _bookingCompleteLiveData.postValue(NetworkResult.Error("Something Went Wrong"))
        }
    }



    suspend fun addBookmarkRepo(param:Map<String,String>) {
        _addBookmarkLiveData.postValue(NetworkResult.Loading())
        val response = userDataApi.addToBookmarkApi(param)
        if (response.isSuccessful && response.body() != null) {
            _addBookmarkLiveData.postValue(NetworkResult.Success(response.body()!!))
        } else if (response.errorBody() != null) {
            val errorObj = JSONObject(response.errorBody()!!.charStream().readText())
            _addBookmarkLiveData.postValue(NetworkResult.Error(errorObj.getString("message")))
        } else {
            _addBookmarkLiveData.postValue(NetworkResult.Error("Something Went Wrong"))
        }
    }


    suspend fun getBookmarkRepo(token:String) {
        _bookmarkLiveData.postValue(NetworkResult.Loading())
        val response = userDataApi.getBookmarkApi(token)
        if (response.isSuccessful && response.body() != null) {
            _bookmarkLiveData.postValue(NetworkResult.Success(response.body()!!))
        } else if (response.errorBody() != null) {
            val errorObj = JSONObject(response.errorBody()!!.charStream().readText())
            _bookmarkLiveData.postValue(NetworkResult.Error(errorObj.getString("message")))
        } else {
            _bookmarkLiveData.postValue(NetworkResult.Error("Something Went Wrong"))
        }
    }


    suspend fun searchPropertyRepo(token:String,name:String,rating:String,lat:String,lon:String,price:String) {
        _searchLiveData.postValue(NetworkResult.Loading())
        val response = userDataApi.searchPropertyApi(token,name,rating,lat,lon,price)
        if (response.isSuccessful && response.body() != null) {
            _searchLiveData.postValue(NetworkResult.Success(response.body()!!))
        } else if (response.errorBody() != null) {
            val errorObj = JSONObject(response.errorBody()!!.charStream().readText())
            _searchLiveData.postValue(NetworkResult.Error(errorObj.getString("message")))
        } else {
            _searchLiveData.postValue(NetworkResult.Error("Something Went Wrong"))
        }
    }



    suspend fun saveRecentPropertyRepo(token:String,propertyId:String) {
        _searchLiveData.postValue(NetworkResult.Loading())
        val response = userDataApi.saveRecentSearchApi(token,propertyId)
        if (response.isSuccessful && response.body() != null) {
            _searchLiveData.postValue(NetworkResult.Success(response.body()!!))
        } else if (response.errorBody() != null) {
            val errorObj = JSONObject(response.errorBody()!!.charStream().readText())
            _searchLiveData.postValue(NetworkResult.Error(errorObj.getString("message")))
        } else {
            _searchLiveData.postValue(NetworkResult.Error("Something Went Wrong"))
        }
    }




    suspend fun addReviewRepo(map:Map<String,String>) {
        _addReviewLiveData.postValue(NetworkResult.Loading())
        val response = userDataApi.addReviewApi(map)
        if (response.isSuccessful && response.body() != null) {
            _addReviewLiveData.postValue(NetworkResult.Success(response.body()!!))
        } else if (response.errorBody() != null) {
            val errorObj = JSONObject(response.errorBody()!!.charStream().readText())
            _addReviewLiveData.postValue(NetworkResult.Error(errorObj.getString("message")))
        } else {
            _addReviewLiveData.postValue(NetworkResult.Error("Something Went Wrong"))
        }
    }

    suspend fun getReviewRepo(map:Map<String,String>) {
        _reviewLiveData.postValue(NetworkResult.Loading())
        val response = userDataApi.getReviewApi(map)
        if (response.isSuccessful && response.body() != null) {
            _reviewLiveData.postValue(NetworkResult.Success(response.body()!!))
        } else if (response.errorBody() != null) {
            val errorObj = JSONObject(response.errorBody()!!.charStream().readText())
            _reviewLiveData.postValue(NetworkResult.Error(errorObj.getString("message")))
        } else {
            _reviewLiveData.postValue(NetworkResult.Error("Something Went Wrong"))
        }
    }


    suspend fun changePasswordRepo(map:Map<String,String>) {
        _changePasswordLiveData.postValue(NetworkResult.Loading())
        val response = userDataApi.changePasswordApi(map)
        if (response.isSuccessful && response.body() != null) {
            _changePasswordLiveData.postValue(NetworkResult.Success(response.body()!!))
        } else if (response.errorBody() != null) {
            val errorObj = JSONObject(response.errorBody()!!.charStream().readText())
            _changePasswordLiveData.postValue(NetworkResult.Error(errorObj.getString("message")))
        } else {
            _changePasswordLiveData.postValue(NetworkResult.Error("Something Went Wrong"))
        }
    }

    suspend fun faqRepo(token:String) {
        _faqLiveData.postValue(NetworkResult.Loading())
        val response = userDataApi.faqApi(token)
        if (response.isSuccessful && response.body() != null) {
            _faqLiveData.postValue(NetworkResult.Success(response.body()!!))
        } else if (response.errorBody() != null) {
            val errorObj = JSONObject(response.errorBody()!!.charStream().readText())
            _faqLiveData.postValue(NetworkResult.Error(errorObj.getString("message")))
        } else {
            _faqLiveData.postValue(NetworkResult.Error("Something Went Wrong"))
        }
    }

    suspend fun aboutUsRepo(token: String) {
        _aboutUsLiveData.postValue(NetworkResult.Loading())
        val response = userDataApi.aboutUsApi(token)
        if (response.isSuccessful && response.body() != null) {
            _aboutUsLiveData.postValue(NetworkResult.Success(response.body()!!))
        } else if (response.errorBody() != null) {
            val errorObj = JSONObject(response.errorBody()!!.charStream().readText())
            _aboutUsLiveData.postValue(NetworkResult.Error(errorObj.getString("message")))
        } else {
            _aboutUsLiveData.postValue(NetworkResult.Error("Something Went Wrong"))
        }
    }

    suspend fun privacyPolicyRepo(token: String) {
        _privacyPolicyLiveData.postValue(NetworkResult.Loading())
        val response = userDataApi.privacyPolicyApi(token)
        if (response.isSuccessful && response.body() != null) {
            _privacyPolicyLiveData.postValue(NetworkResult.Success(response.body()!!))
        } else if (response.errorBody() != null) {
            val errorObj = JSONObject(response.errorBody()!!.charStream().readText())
            _privacyPolicyLiveData.postValue(NetworkResult.Error(errorObj.getString("message")))
        } else {
            _privacyPolicyLiveData.postValue(NetworkResult.Error("Something Went Wrong"))
        }
    }


    suspend fun exploreRepo(token: String) {
        _exploreLiveData.postValue(NetworkResult.Loading())
        val response = userDataApi.exploreApi(token)
        if (response.isSuccessful && response.body() != null) {
            _exploreLiveData.postValue(NetworkResult.Success(response.body()!!))
        } else if (response.errorBody() != null) {
            val errorObj = JSONObject(response.errorBody()!!.charStream().readText())
            _exploreLiveData.postValue(NetworkResult.Error(errorObj.getString("message")))
        } else {
            _exploreLiveData.postValue(NetworkResult.Error("Something Went Wrong"))
        }
    }


    suspend fun explorePropertyRepo(map: Map<String, String>) {
        _explorePropertyLiveData.postValue(NetworkResult.Loading())
        val response = userDataApi.explorePropertyListApi(map)
        if (response.isSuccessful && response.body() != null) {
            _explorePropertyLiveData.postValue(NetworkResult.Success(response.body()!!))
        } else if (response.errorBody() != null) {
            val errorObj = JSONObject(response.errorBody()!!.charStream().readText())
            _explorePropertyLiveData.postValue(NetworkResult.Error(errorObj.getString("message")))
        } else {
            _explorePropertyLiveData.postValue(NetworkResult.Error("Something Went Wrong"))
        }
    }




    suspend fun notificationRepo(map: Map<String, String>) {
        _notificationLiveData.postValue(NetworkResult.Loading())
        val response = userDataApi.notificationApi(map)
        if (response.isSuccessful && response.body() != null) {
            _notificationLiveData.postValue(NetworkResult.Success(response.body()!!))
        } else if (response.errorBody() != null) {
            val errorObj = JSONObject(response.errorBody()!!.charStream().readText())
            _notificationLiveData.postValue(NetworkResult.Error(errorObj.getString("message")))
        } else {
            _notificationLiveData.postValue(NetworkResult.Error("Something Went Wrong"))
        }
    }



    suspend fun paymentRepo(map: Map<String, String>) {
        _paymentLiveData.postValue(NetworkResult.Loading())
        val response = userDataApi.paymentApi(map)
        if (response.isSuccessful && response.body() != null) {
            _paymentLiveData.postValue(NetworkResult.Success(response.body()!!))
        } else if (response.errorBody() != null) {
            val errorObj = JSONObject(response.errorBody()!!.charStream().readText())
            _paymentLiveData.postValue(NetworkResult.Error(errorObj.getString("message")))
        } else {
            _paymentLiveData.postValue(NetworkResult.Error("Something Went Wrong"))
        }
    }


    suspend fun sendNotificationChatRepo(map: Map<String, String>) {
        _chatLiveData.postValue(NetworkResult.Loading())
        val response = userDataApi.sendNotificationChatApi(map)
        if (response.isSuccessful && response.body() != null) {
            _chatLiveData.postValue(NetworkResult.Success(response.body()!!))
        } else if (response.errorBody() != null) {
            val errorObj = JSONObject(response.errorBody()!!.charStream().readText())
            _chatLiveData.postValue(NetworkResult.Error(errorObj.getString("message")))
        } else {
            _chatLiveData.postValue(NetworkResult.Error("Something Went Wrong"))
        }
    }


    suspend fun allChatMsgRepo(map: Map<String, String>) {
        _allChatMsgLiveData.postValue(NetworkResult.Loading())
        val response = userDataApi.allChatMsgApi(map)
        if (response.isSuccessful && response.body() != null) {
            _allChatMsgLiveData.postValue(NetworkResult.Success(response.body()!!))
        } else if (response.errorBody() != null) {
            val errorObj = JSONObject(response.errorBody()!!.charStream().readText())
            _allChatMsgLiveData.postValue(NetworkResult.Error(errorObj.getString("message")))
        } else {
            _allChatMsgLiveData.postValue(NetworkResult.Error("Something Went Wrong"))
        }
    }





    suspend fun getPropertyDetailRepo(map:Map<String,String>) {
        _propertyDetailLiveData.postValue(NetworkResult.Loading())
        val response = userDataApi.getPropertyDetailApi(map)
        if (response.isSuccessful && response.body() != null) {
            _propertyDetailLiveData.postValue(NetworkResult.Success(response.body()!!))
        } else if (response.errorBody() != null) {
            val errorObj = JSONObject(response.errorBody()!!.charStream().readText())
            _propertyDetailLiveData.postValue(NetworkResult.Error(errorObj.getString("message")))
        } else {
            _propertyDetailLiveData.postValue(NetworkResult.Error("Something Went Wrong"))
        }
    }


    suspend fun getTaxDataRepo(token: String) {
        _taxLiveData.postValue(NetworkResult.Loading())
        val response = userDataApi.taxDataApi(token)
        if (response.isSuccessful && response.body() != null) {
            _taxLiveData.postValue(NetworkResult.Success(response.body()!!))
        } else if (response.errorBody() != null) {
            val errorObj = JSONObject(response.errorBody()!!.charStream().readText())
            _taxLiveData.postValue(NetworkResult.Error(errorObj.getString("message")))
        } else {
            _taxLiveData.postValue(NetworkResult.Error("Something Went Wrong"))
        }
    }








    private fun handleResponse(response: Response<ResponseBody>) {
        if (response.isSuccessful && response.body() != null) {
            _userDataLiveData.postValue(NetworkResult.Success(response.body()!!))
        }
        else if(response.errorBody()!=null){
            val errorObj = JSONObject(response.errorBody()!!.charStream().readText())
            _userDataLiveData.postValue(NetworkResult.Error(errorObj.getString("message")))
        }
        else{
            _userDataLiveData.postValue(NetworkResult.Error("Something Went Wrong"))
        }
    }


    private fun handleResponse1(response: Response<ResponseBody>) {
        if (response.isSuccessful && response.body() != null) {
            _recentSearchLiveData.postValue(NetworkResult.Success(response.body()!!))
        }
        else if(response.errorBody()!=null){
            val errorObj = JSONObject(response.errorBody()!!.charStream().readText())
            _recentSearchLiveData.postValue(NetworkResult.Error(errorObj.getString("message")))
        }
        else{
            _recentSearchLiveData.postValue(NetworkResult.Error("Something Went Wrong"))
        }
    }

    private fun handleResponse2(response: Response<ResponseBody>) {
        if (response.isSuccessful && response.body() != null) {
            _popularPropertyLiveData.postValue(NetworkResult.Success(response.body()!!))
        }
        else if(response.errorBody()!=null){
            val errorObj = JSONObject(response.errorBody()!!.charStream().readText())
            _popularPropertyLiveData.postValue(NetworkResult.Error(errorObj.getString("message")))
        }
        else{
            _popularPropertyLiveData.postValue(NetworkResult.Error("Something Went Wrong"))
        }
    }


    private fun handleResponse3(response: Response<ResponseBody>) {
        if (response.isSuccessful && response.body() != null) {
            _propertyBookingLiveData.postValue(NetworkResult.Success(response.body()!!))
        }
        else if(response.errorBody()!=null){
            val errorObj = JSONObject(response.errorBody()!!.charStream().readText())
            _propertyBookingLiveData.postValue(NetworkResult.Error(errorObj.getString("message")))
        }
        else{
            _propertyBookingLiveData.postValue(NetworkResult.Error("Something Went Wrong"))
        }
    }



    private fun handleResponse4(response: Response<ResponseBody>) {
        if (response.isSuccessful && response.body() != null) {
            _promoCodeLiveData.postValue(NetworkResult.Success(response.body()!!))
        }
        else if(response.errorBody()!=null){
            val errorObj = JSONObject(response.errorBody()!!.charStream().readText())
            _promoCodeLiveData.postValue(NetworkResult.Error(errorObj.getString("message")))
        }
        else{
            _promoCodeLiveData.postValue(NetworkResult.Error("Something Went Wrong"))
        }
    }




    fun clearData(){
        _userDataLiveData.value = null
    }




    fun clearStoryPropertyData(){
        _storyListLiveData.value = null
    }

    fun clearRecentSearchData(){
        _recentSearchLiveData.value = null
    }


    fun clearMostPopularData(){
        _popularPropertyLiveData.value = null
    }

    fun clearPriceData() {
        _propertyPriceWithDayLiveData.value = null
    }


    fun clearPromoCodeData() {
        _promoCodeLiveData.value = null
    }



    fun clearBookingData() {
        _propertyBookingLiveData.value = null
    }


    fun clearBookingCompleteData() {
        _bookingCompleteLiveData.value = null
    }


    fun clearBookmarkData() {
        _bookmarkLiveData.value = null
    }


    fun clearAddBookmarkData() {
        _addBookmarkLiveData.value = null
    }



    fun clearSearchData() {
        _searchLiveData.value = null
    }


    fun clearAddReviewData() {
        _addReviewLiveData.value = null
    }


    fun clearReviewData() {
        _reviewLiveData.value = null
    }



    fun clearChangePasswordData() {
        _changePasswordLiveData.value = null
    }


    fun clearFaqData() {
        _faqLiveData.value = null
    }

    fun clearAboutUsData() {
        _aboutUsLiveData.value = null
    }

    fun clearPrivacyPolicyData() {
        _privacyPolicyLiveData.value = null
    }


    fun clearExploreData() {
        _exploreLiveData.value = null
    }

    fun clearExplorePropertyData() {
        _explorePropertyLiveData.value = null
    }


    fun clearNotificationData() {
        _notificationLiveData.value = null
    }


    fun clearPaymentData() {
        _paymentLiveData.value = null
    }

    fun clearPropertyDetailData() {
        _propertyDetailLiveData.value = null
    }

    fun clearChatData() {
        _chatLiveData.value = null
    }


    fun clearAllChatMsgData() {
        _allChatMsgLiveData.value = null
    }


    fun clearTaxData() {
        _taxLiveData.value = null
    }





}