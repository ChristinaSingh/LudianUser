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
class NotificationViewModel @Inject constructor(private val userDataRepository: UserDataRepository) : ViewModel() {

    val notificationLiveData: LiveData<NetworkResult<ResponseBody>?>
        get() = userDataRepository.notificationLiveData


    fun notificationData(map:Map<String,String>){
        viewModelScope.launch {
            userDataRepository.notificationRepo(map)
        }
    }

    fun clearNotificationData() {
        userDataRepository.clearNotificationData()
    }

}