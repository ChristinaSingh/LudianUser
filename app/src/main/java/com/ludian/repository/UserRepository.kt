package com.ludian.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.ludian.api.UserAPI
import com.ludian.utils.NetworkResult
import okhttp3.ResponseBody
import org.json.JSONObject
import retrofit2.Response
import javax.inject.Inject

class UserRepository @Inject constructor(private val userAPI: UserAPI) {

    private val _userResponseLiveData = MutableLiveData<NetworkResult<ResponseBody>>()
    val userResponseLiveData: LiveData<NetworkResult<ResponseBody>>
        get() = _userResponseLiveData

    suspend fun registerUser(params: Map<String, String>) {
        _userResponseLiveData.postValue(NetworkResult.Loading())
        val response = userAPI.signup(params)
        handleResponse(response)
    }

    suspend fun loginUser(params: Map<String, String>) {
        _userResponseLiveData.postValue(NetworkResult.Loading())
        val response =userAPI.login(params)
        handleResponse(response)
    }


    suspend fun forgotPasswordRepo(params: Map<String, String>) {
        _userResponseLiveData.postValue(NetworkResult.Loading())
        val response =userAPI.forgotPasswordApi(params)
        handleResponse(response)
    }


    suspend fun otpVerifyRepo(params: Map<String, String>) {
        _userResponseLiveData.postValue(NetworkResult.Loading())
        val response =userAPI.otpVerifyApi(params)
        handleResponse(response)
    }


    suspend fun createNewPasswordRepo(params: Map<String, String>) {
        _userResponseLiveData.postValue(NetworkResult.Loading())
        val response =userAPI.createNewPasswordApi(params)
        handleResponse(response)
    }






    private fun handleResponse(response: Response<ResponseBody>) {
        if (response.isSuccessful && response.body() != null) {
            _userResponseLiveData.postValue(NetworkResult.Success(response.body()!!))
        }
        else if(response.errorBody()!=null){
            val errorObj = JSONObject(response.errorBody()!!.charStream().readText())
            _userResponseLiveData.postValue(NetworkResult.Error(errorObj.getString("message")))
        }
        else{
            _userResponseLiveData.postValue(NetworkResult.Error("Something Went Wrong"))
        }
    }
}