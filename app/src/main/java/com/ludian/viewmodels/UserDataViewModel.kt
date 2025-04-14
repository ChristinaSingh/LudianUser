package com.ludian.viewmodels

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ludian.R
import com.ludian.repository.UserDataRepository
import com.ludian.utils.Helper
import com.ludian.utils.NetworkResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import javax.inject.Inject

@HiltViewModel
class UserDataViewModel @Inject constructor(private  val  userDataRepository: UserDataRepository) : ViewModel() {

    val userResponseLiveData: LiveData<NetworkResult<ResponseBody>?>
        get() = userDataRepository.userResponseLiveData


    val storyListLiveData: LiveData<NetworkResult<ResponseBody>?>
        get() = userDataRepository.storyListLiveData

    val recentSearchLiveData: LiveData<NetworkResult<ResponseBody>?>
        get() = userDataRepository.recentSearchLiveData


    val popularPropertyLiveData: LiveData<NetworkResult<ResponseBody>?>
        get() = userDataRepository.popularPropertyLiveData


    val propertyPriceWithDayLiveData: LiveData<NetworkResult<ResponseBody>?>
        get() = userDataRepository.propertyPriceWithDayLiveData

    val propertyBookingLiveData: LiveData<NetworkResult<ResponseBody>?>
        get() = userDataRepository.propertyBookingLiveData

    val promoCodeLiveData: LiveData<NetworkResult<ResponseBody>?>
        get() = userDataRepository.promoCodeLiveData

    val bookingCompleteLiveData: LiveData<NetworkResult<ResponseBody>?>
        get() = userDataRepository.bookingCompleteLiveData


    val addBookmarkLiveData: LiveData<NetworkResult<ResponseBody>?>
        get() = userDataRepository.addBookmarkLiveData

    val bookmarkLiveData: LiveData<NetworkResult<ResponseBody>?>
        get() = userDataRepository.bookmarkLiveData

    val searchLiveData: LiveData<NetworkResult<ResponseBody>?>
        get() = userDataRepository.searchLiveData


    val paymentLiveData: LiveData<NetworkResult<ResponseBody>?>
        get() = userDataRepository.paymentLiveData

    val chatLiveData: LiveData<NetworkResult<ResponseBody>?>
        get() = userDataRepository.chatLiveData

    val allChatMsgLiveData: LiveData<NetworkResult<ResponseBody>?>
        get() = userDataRepository.allChatMsgLiveData


    val taxLiveData: LiveData<NetworkResult<ResponseBody>?>
        get() = userDataRepository.taxLiveData


    val addReviewResponseLiveData: LiveData<NetworkResult<ResponseBody>?>
        get() = userDataRepository.addReviewLiveData


    fun getUserProfile(token:String){
        viewModelScope.launch {
            userDataRepository.getUserProfileRepo(token)
        }
    }


    fun updateProfile(
        token: RequestBody,
        userId: RequestBody,
        firstName: RequestBody,
        lastName: RequestBody,
        email: RequestBody,
        mobile: RequestBody,
        profilePicture: MultipartBody.Part
    ) {
        viewModelScope.launch {
            userDataRepository.updateProfileRepo(
               token,
                userId,
                firstName,
                lastName,
                email,
                mobile,
                profilePicture
            )
        }
    }




    fun getNearestProperty(map: Map<String, String>){
        viewModelScope.launch {
            userDataRepository.getNearestPropertyRepo(map)
        }
    }


    fun getAllRecentSearch(userId:String){
        viewModelScope.launch {
            userDataRepository.getAllRecentSearchRepo(userId)
        }
    }

    fun getAllMostPopularProperty(userId: String){
        viewModelScope.launch {
            userDataRepository.getAllMostPopularPropertyRepo(userId)
        }
    }


    fun getPropertyPriceWithDay(token:String,propertyId: String) {
        viewModelScope.launch {
            userDataRepository.getPropertyPriceWithDayRepo(token,propertyId)
        }
    }


    fun propertyBooking(map:Map<String,String>) {
        viewModelScope.launch {
            userDataRepository.propertyBookingRepo(map)
        }
    }


    fun getPromoCode(token: String,propertyId: String){
        viewModelScope.launch {
            userDataRepository.getPromoCodeRepo(token,propertyId)
        }
    }


 /*   fun applyPromoCode(userId:String,promoCode:String) {
        viewModelScope.launch {
            userDataRepository.applyPromoCodeRepo(userId,promoCode)
        }
    }*/

    fun applyPromoCode(map:Map<String,String>){
        viewModelScope.launch {
            userDataRepository.applyPromoCodeRepo(map)
        }
    }




    fun getBooking(map:Map<String,String>){
        viewModelScope.launch {
            userDataRepository.getBookingRepo(map)
        }
    }

    fun bookingComplete(map:Map<String,String>){
        viewModelScope.launch {
            userDataRepository.bookingCompleteRepo(map)
        }
    }


    fun addToBookmark(map:Map<String,String>){
        viewModelScope.launch {
            userDataRepository.addBookmarkRepo(map)
        }
    }


    fun getBookmark(token: String){
        viewModelScope.launch {
            userDataRepository.getBookmarkRepo(token)
        }
    }


    fun searchProperty(token:String,name:String,rating:String,lat:String,lon:String,price:String){
        viewModelScope.launch {
            userDataRepository.searchPropertyRepo(token,name, rating, lat, lon, price)
        }
    }


    fun payment(map:Map<String,String>){
        viewModelScope.launch {
            userDataRepository.paymentRepo(map)
        }
    }



    fun sendNotificationChat(map:Map<String,String>){
        viewModelScope.launch {
            userDataRepository.sendNotificationChatRepo(map)
        }
    }


    fun allChatMsg(map:Map<String,String>){
        viewModelScope.launch {
            userDataRepository.allChatMsgRepo(map)
        }
    }


    fun getTaxData(token: String){
        viewModelScope.launch {
            userDataRepository.getTaxDataRepo(token)
        }
    }


    fun addReview(map: Map<String,String>){
        viewModelScope.launch {
            userDataRepository.addReviewRepo(map)
        }
    }



    fun clearData(){
        userDataRepository.clearData()
    }


    fun clearStoryPropertyData(){
        userDataRepository.clearStoryPropertyData()
    }


    fun clearRecentSearchData(){
        userDataRepository.clearRecentSearchData()
    }


    fun clearMostPopularPropertyData(){
        userDataRepository.clearMostPopularData()
    }


    fun clearPriceData() {
        userDataRepository.clearPriceData()
    }


    fun clearPromoCodeData() {
        userDataRepository.clearPromoCodeData()
    }


    fun clearBookingData() {
        userDataRepository.clearBookingData()
    }


    fun clearBookingCompleteData() {
        userDataRepository.clearBookingCompleteData()
    }


    fun clearBookmarkData() {
        userDataRepository.clearBookmarkData()
    }

    fun clearAddBookmarkData() {
        userDataRepository.clearAddBookmarkData()
    }

    fun clearSearchData() {
        userDataRepository.clearSearchData()
    }

    fun clearPaymentData() {
        userDataRepository.clearPaymentData()
    }

    fun clearChatData() {
        userDataRepository.clearChatData()
    }

    fun clearAllChatMsgData() {
        userDataRepository.clearAllChatMsgData()
    }

    fun clearTaxData() {
        userDataRepository.clearTaxData()
    }



    fun clearAddReviewData() {
        userDataRepository.clearAddReviewData()
    }





    fun validateProfile(
        context: Context, fName: String, lName: String, emailAddress: String, mobile: String
    ): Pair<Boolean, String> {

        var result = Pair(true, "")

        if (fName == "") {
            result = Pair(false, context.getString(R.string.please_enter_first_name))

        } else if (lName == "") {
            result = Pair(false, context.getString(R.string.please_enter_last_name))
        } else if (emailAddress == "") {
            result = Pair(false, context.getString(R.string.please_enter_email))
        } else if (!Helper.isValidEmail(emailAddress)) {
            result = Pair(false, context.getString(R.string.email_is_invalid))
        } else if (mobile == "") {
            result = Pair(false, context.getString(R.string.please_enter_mobile_number))

        }

        return result

    }

}