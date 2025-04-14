package com.ludian.ui.auth

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.ludian.R
import com.ludian.databinding.ActivityNewPassBinding
import com.ludian.utils.Helper
import com.ludian.utils.NetworkResult
import com.ludian.utils.SharedPrf
import com.ludian.viewmodels.AuthViewModel
import dagger.hilt.android.AndroidEntryPoint
import org.json.JSONObject

@AndroidEntryPoint
class NewPassAct : AppCompatActivity() {
    private lateinit var binding : ActivityNewPassBinding
    private lateinit var authViewModel : AuthViewModel
    private var userId : String?=null
    private val sharedPrf by lazy { SharedPrf(this@NewPassAct) }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_new_pass)
        authViewModel = ViewModelProvider(this).get(AuthViewModel::class.java)

        initViews()
    }

    private fun initViews() {

        if(intent!=null) userId = intent.getStringExtra("user_id")


        binding.ivBack!!.setOnClickListener {
            finish()
        }

        binding.btnSave!!.setOnClickListener {
            Helper.hideKeyboard(it)
            val validationResult = validateUserInput()
            if (validationResult.first) {
                val userRequest = getCreateNewPasswordRequest()
                Log.e("Create New Password Request===", userRequest.toString())
                Helper.showProgressMessage(this@NewPassAct,getString(R.string.please_wait))
                authViewModel.createNewPassword(userRequest)
            } else {
                showValidationErrors(validationResult.second)
            }
        }

     bindObservers()


    }

    private fun validateUserInput(): Pair<Boolean, String> {
        val password = binding.edNewPassword.text.toString()
        val confirmPassword = binding.edConfirmPassword.text.toString()
        return authViewModel.validatePassword(
            this@NewPassAct,
            password,
            confirmPassword
        )
    }

    private fun showValidationErrors(error: String) {
        val text = String.format(resources.getString(R.string.txt_error_message, error))
        Toast.makeText(this@NewPassAct,text, Toast.LENGTH_SHORT).show()
    }


    private fun getCreateNewPasswordRequest(): Map<String, String> {
        return binding.run {

            mapOf(
                "token" to sharedPrf.getStoredTag(SharedPrf.TOKEN),
                "user_id" to userId!!,
                "password" to binding.edNewPassword.text.toString(),
                "confirm_password" to binding.edConfirmPassword.text.toString()
            )

        }
    }

    private fun bindObservers() {
        authViewModel.userResponseLiveData.observe(this, Observer {
            Helper.hideProgressMessage()
            when (it) {
                is NetworkResult.Success -> {
                    it.data?.let {
                        val jsonObject = JSONObject(it.string())
                        Log.e("Create New Password Response===",jsonObject.toString())
                        if (jsonObject.getString("status") == "1") {
                            Toast.makeText(
                                this@NewPassAct,
                                "Password change successfully",
                                Toast.LENGTH_SHORT
                            ).show()
                            startActivity(Intent(this@NewPassAct, LoginAct::class.java))
                            finish()


                        } else {
                            Toast.makeText(
                                this@NewPassAct,
                                "" + jsonObject.getString("message"),
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                        //  Log.e("TAG", "observers: $it.")
                    }




                }

                is NetworkResult.Error -> {
                    showValidationErrors(it.message.toString())
                }

                is NetworkResult.Loading -> {
                    Helper.showProgressMessage(this@NewPassAct,getString(R.string.please_wait))
                }
            }
        })
    }


}
