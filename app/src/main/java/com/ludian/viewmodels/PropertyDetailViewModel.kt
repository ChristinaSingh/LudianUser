package com.ludian.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ludian.api.UserDataApi
import com.ludian.repository.UserDataRepository
import com.ludian.utils.NetworkResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import okhttp3.ResponseBody
import javax.inject.Inject

@HiltViewModel
class PropertyDetailViewModel @Inject constructor(private val userDataRepository: UserDataRepository) : ViewModel() {


    val searchLiveData: LiveData<NetworkResult<ResponseBody>?>
        get() = userDataRepository.searchLiveData

    val addReviewResponseLiveData: LiveData<NetworkResult<ResponseBody>?>
        get() = userDataRepository.addReviewLiveData

    val reviewResponseLiveData: LiveData<NetworkResult<ResponseBody>?>
        get() = userDataRepository.reviewLiveData


    val propertyDetailLiveData: LiveData<NetworkResult<ResponseBody>?>
        get() = userDataRepository.propertyDetailLiveData

    fun saveRecentProperty(token:String,propertyId:String){
        viewModelScope.launch {
            userDataRepository.saveRecentPropertyRepo(token,propertyId)
        }
    }


    fun addReview(map: Map<String,String>){
        viewModelScope.launch {
            userDataRepository.addReviewRepo(map)
        }
    }

    fun getReviews(map: Map<String,String>){
        viewModelScope.launch {
            userDataRepository.getReviewRepo(map)
        }
    }



    fun getPropertyDetailDeepLink(map: Map<String,String>){
        viewModelScope.launch {
            userDataRepository.getPropertyDetailRepo(map)
        }
    }


    fun clearSearchData() {
        userDataRepository.clearSearchData()
    }

    fun clearAddReviewData() {
       userDataRepository.clearAddReviewData()
    }


    fun clearReviewData() {
        userDataRepository.clearReviewData()
    }


    fun clearPropertyDetailData() {
        userDataRepository.clearPropertyDetailData()
    }


}