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
import com.ludian.databinding.ActivityForgotPassBinding
import com.ludian.utils.Helper
import com.ludian.utils.NetworkResult
import com.ludian.viewmodels.AuthViewModel
import dagger.hilt.android.AndroidEntryPoint
import org.json.JSONObject

@AndroidEntryPoint
class ForgotPassAct : AppCompatActivity(){
    private lateinit var binding : ActivityForgotPassBinding
    private lateinit var authViewModel : AuthViewModel
    private var type: String = ""
    private var emailAddress : String?=""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_forgot_pass)
        authViewModel = ViewModelProvider(this).get(AuthViewModel::class.java)

        initViews()
    }

    private fun initViews() {


        binding.ivBack.setOnClickListener {
            finish()
        }


        binding.llMobile.setOnClickListener {
            type = "mobile"
            binding.llMobile.background = getDrawable(R.drawable.rounded_corner_stroke_10)
            binding.llEmail.background = getDrawable(R.drawable.rounded_corner_white_10)

        }


        binding.llEmail.setOnClickListener {
            type = "email"
            binding.llMobile.background = getDrawable(R.drawable.rounded_corner_white_10)
            binding.llEmail.background = getDrawable(R.drawable.rounded_corner_stroke_10)

        }




        binding.btnSubmit.setOnClickListener {
            Helper.hideKeyboard(it)
            val validationResult = validateUserInput()
            if (validationResult.first) {
                val userRequest = getUserRequest()
                Log.e("Forgot pass Request===", userRequest.toString())
                Helper.showProgressMessage(this@ForgotPassAct,getString(R.string.please_wait))
                authViewModel.forgetPassword(userRequest)
            } else {
                showValidationErrors(validationResult.second)
            }
        }

        bindObservers()

    }


    private fun validateUserInput(): Pair<Boolean, String> {

        emailAddress = if(type=="email") binding.edEmail.text.toString()
        else binding.edPhone.text.toString()
        return authViewModel.validateForgotPassword(
            this@ForgotPassAct,
            emailAddress!!,
            type
        )
    }

    private fun showValidationErrors(error: String) {
        val text = String.format(resources.getString(R.string.txt_error_message, error))
        Toast.makeText(this@ForgotPassAct,text, Toast.LENGTH_SHORT).show()
    }


    private fun getUserRequest(): Map<String, String> {
        return binding.run {

            mapOf(
                "email_phone" to binding.edEmail.text.toString(),
                "type" to type
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
                        Log.e("Forgot Response===",jsonObject.toString())
                        if (jsonObject.getString("status") == "1") {
                            Toast.makeText(
                                this@ForgotPassAct,
                                "Otp send Successfully",
                                Toast.LENGTH_SHORT
                            ).show()
                            startActivity(Intent(this@ForgotPassAct, VerifyAct::class.java)
                                .putExtra("email",emailAddress!!))
                            finish()


                        } else {
                            Toast.makeText(
                                this@ForgotPassAct,
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
                    Helper.showProgressMessage(this@ForgotPassAct,getString(R.string.please_wait))
                }
            }
        })
    }


    override fun onDestroy() {
        super.onDestroy()
        //_binding = null

    }


}
