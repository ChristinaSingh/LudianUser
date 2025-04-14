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
class ExploreViewModel@Inject constructor(private val userDataRepository: UserDataRepository) : ViewModel() {

    val exploreLiveData: LiveData<NetworkResult<ResponseBody>?>
        get() = userDataRepository.exploreLiveData

    val explorePropertyLiveData: LiveData<NetworkResult<ResponseBody>?>
        get() = userDataRepository.explorePropertyLiveData



    fun explore(token:String){
        viewModelScope.launch {
            userDataRepository.exploreRepo(token)
        }
    }


    fun exploreProperty(map:Map<String,String>){
        viewModelScope.launch {
            userDataRepository.explorePropertyRepo(map)
        }
    }







    fun clearExploreData() {
        userDataRepository.clearExploreData()
    }


    fun clearExplorePropertyData() {
        userDataRepository.clearExplorePropertyData()
    }
}