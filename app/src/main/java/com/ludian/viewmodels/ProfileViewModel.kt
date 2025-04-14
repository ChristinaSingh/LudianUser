package com.ludian.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ludian.repository.UserDataRepository
import com.ludian.utils.NetworkResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import okhttp3.ResponseBody
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(private val userDataRepository: UserDataRepository) : ViewModel() {

    val changePasswordLiveData: LiveData<NetworkResult<ResponseBody>?>
        get() = userDataRepository.changePasswordLiveData

    val faqLiveData: LiveData<NetworkResult<ResponseBody>?>
        get() = userDataRepository.faqLiveData

    val aboutUsLiveData: LiveData<NetworkResult<ResponseBody>?>
        get() = userDataRepository.aboutUsLiveData

    val privacyPolicyLiveData: LiveData<NetworkResult<ResponseBody>?>
        get() = userDataRepository.privacyPolicyLiveData




    fun changePassword(map: Map<String, String>){
        viewModelScope.launch {
            userDataRepository.changePasswordRepo(map)
        }
    }


    fun getFaq(token:String){
        viewModelScope.launch {
            userDataRepository.faqRepo(token)
        }
    }

    fun getAboutUs(token: String){
        viewModelScope.launch {
            userDataRepository.aboutUsRepo(token)
        }
    }

    fun getPrivacyPolicy(token: String){
        viewModelScope.launch {
            userDataRepository.privacyPolicyRepo(token)
        }
    }




    fun clearChangePasswordData() {
        userDataRepository.clearChangePasswordData()
    }


    fun clearFaqData() {
        userDataRepository.clearFaqData()
    }

    fun clearAboutUsData() {
        userDataRepository.clearAboutUsData()
    }

    fun clearPrivacyPolicyData() {
        userDataRepository.clearPrivacyPolicyData()
    }

}