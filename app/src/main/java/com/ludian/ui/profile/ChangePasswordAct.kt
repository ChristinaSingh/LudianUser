package com.ludian.ui.profile

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.google.gson.Gson
import com.ludian.R
import com.ludian.databinding.ActivityChangePassBinding
import com.ludian.models.ReviewModel
import com.ludian.utils.Helper
import com.ludian.utils.NetworkResult
import com.ludian.utils.SharedPrf
import com.ludian.viewmodels.ProfileViewModel
import com.ludian.viewmodels.PropertyDetailViewModel
import dagger.hilt.android.AndroidEntryPoint
import org.json.JSONObject

@AndroidEntryPoint
class ChangePasswordAct : AppCompatActivity() {
    private lateinit var binding: ActivityChangePassBinding
    private lateinit var profileViewModel: ProfileViewModel
    private val sharedPrf by lazy { SharedPrf(this) }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_change_pass)
        profileViewModel = ViewModelProvider(this).get(ProfileViewModel::class.java)

        initViews()
    }

    private fun initViews() {

        binding.ivBack!!.setOnClickListener {
            finish()
        }

        binding.btnSave.setOnClickListener {
            if(binding.edNewPassword.text.toString()==""){
                binding.edNewPassword.requestFocus()
                binding.edNewPassword.error = getString(R.string.required)
            }
            else if(binding.edConfirmPassword.text.toString()==""){
                binding.edConfirmPassword.requestFocus()
                binding.edConfirmPassword.error = getString(R.string.required)
            }

            else if(binding.edNewPassword.text.toString()!=binding.edConfirmPassword.text.toString()){
                binding.edConfirmPassword.requestFocus()
                binding.edConfirmPassword.error = getString(R.string.password_dont_matched)
            }

            else{
                val changePasswordRequest = changePasswordRequest(sharedPrf.getStoredTag(SharedPrf.USER_ID),binding.edNewPassword.text.toString(),binding.edConfirmPassword.text.toString())
                Log.e("change password Request===", changePasswordRequest.toString())
                Helper.showProgressMessage(this@ChangePasswordAct,getString(R.string.please_wait))
                profileViewModel.changePassword(changePasswordRequest)
            }

        }


        bindObservers()
    }


    private fun bindObservers() {
        profileViewModel.changePasswordLiveData.observe(this, Observer { it ->
            Helper.hideProgressMessage()
            when (it) {
                is NetworkResult.Success -> {
                    try {
                        it.data?.let {
                            val jsonObject = JSONObject(it.string())
                            Log.e("change password Response===", jsonObject.toString())
                            if (jsonObject.getString("status") == "1") {
                                Toast.makeText(
                                    this@ChangePasswordAct,
                                    getString(R.string.password_changed_successfully),
                                    Toast.LENGTH_LONG
                                ).show()
                                finish()
                                profileViewModel.clearChangePasswordData()
                            } else {

                            }
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }

                is NetworkResult.Error -> {
                    showValidationErrors(it.message.toString())
                }

                is NetworkResult.Loading -> {
                    Helper.showProgressMessage(
                        this@ChangePasswordAct,
                        getString(R.string.please_wait)
                    )
                }

                else -> {}
            }
        })


    }


    private fun showValidationErrors(error: String) {
        val text = String.format(resources.getString(R.string.txt_error_message, error))
        Toast.makeText(this@ChangePasswordAct, text, Toast.LENGTH_SHORT).show()

    }

    private fun changePasswordRequest(userId: String, password: String, confirmPassword: String): Map<String, String> {
        return binding.run {
            mapOf(
                "token" to sharedPrf.getStoredTag(SharedPrf.TOKEN),
                "user_id" to userId ,
                "password" to password,
                "confirm_password" to confirmPassword
            )
        }
    }




}