package com.ludian.ui.auth

import android.content.Intent
import android.os.Bundle
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.ludian.R
import com.ludian.databinding.ActivitySignupBinding
import com.ludian.utils.Helper
import com.ludian.utils.NetworkResult
import com.ludian.utils.SharedPrf
import com.ludian.viewmodels.AuthViewModel
import dagger.hilt.android.AndroidEntryPoint
import org.json.JSONObject

@AndroidEntryPoint
class SignupAct : AppCompatActivity() {
    private lateinit var binding: ActivitySignupBinding
    private lateinit var authViewModel : AuthViewModel
    private var chkTermsCondition: Boolean = false
    private val sharedPrf by lazy { SharedPrf(this) }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_signup)
        authViewModel = ViewModelProvider(this).get(AuthViewModel::class.java)
        initViews()
    }

    private fun initViews() {
        // I agree to the medidoc Terms of Service and Privacy Policy



        if (sharedPrf.getStoredTag(SharedPrf.LANGUAGE) == "ar") {
            binding.edFirstName.textAlignment = View.TEXT_ALIGNMENT_VIEW_START
            binding.edLastName.textAlignment = View.TEXT_ALIGNMENT_VIEW_START
            binding.edEmail.textAlignment = View.TEXT_ALIGNMENT_VIEW_START
            binding.edMobile.textAlignment = View.TEXT_ALIGNMENT_VIEW_START
            binding.edPassword.textAlignment = View.TEXT_ALIGNMENT_VIEW_START


        } else {
            binding.edFirstName.textAlignment = View.TEXT_ALIGNMENT_VIEW_END
            binding.edLastName.textAlignment = View.TEXT_ALIGNMENT_VIEW_END
            binding.edEmail.textAlignment = View.TEXT_ALIGNMENT_VIEW_END
            binding.edMobile.textAlignment = View.TEXT_ALIGNMENT_VIEW_END
            binding.edPassword.textAlignment = View.TEXT_ALIGNMENT_VIEW_END
            val spannableString = SpannableString(getString(R.string.i_agree))
            spannableString.setSpan(ForegroundColorSpan(getColor(R.color.color_primary)), 23, 28, 0);
            spannableString.setSpan(
                ForegroundColorSpan(getColor(R.color.color_primary)),
                44,
                spannableString.length,
                0
            );
            binding.tvConditions.text = spannableString;
        }





        binding.btnLogin.setOnClickListener {
            startActivity(Intent(this@SignupAct, LoginAct::class.java))
            finish()
        }


        binding.btnSignup.setOnClickListener {
            Helper.hideKeyboard(it)
            val validationResult = validateUserInput()
            if (validationResult.first) {
                val userRequest = getUserRequest()
                Log.e("Signup Request===", userRequest.toString())
                Helper.showProgressMessage(this@SignupAct,getString(R.string.please_wait))
                authViewModel.registerUser(userRequest)
            } else {
                showValidationErrors(validationResult.second)
            }
        }


        binding.checkAgree.setOnClickListener {
            if(!chkTermsCondition) {
                binding.checkAgree.isChecked = true
                chkTermsCondition = true
            }
            else{
                binding.checkAgree.isChecked = false
                chkTermsCondition = false
            }
        }


        bindObservers()
    }


    private fun validateUserInput(): Pair<Boolean, String> {
        val fName = binding.edFirstName.text.toString()
        val lName = binding.edLastName.text.toString()
        val emailAddress = binding.edEmail.text.toString()
        val mobileNumber = binding.edMobile.text.toString()
        val password = binding.edPassword.text.toString()
        return authViewModel.validateSignup(
            this@SignupAct,
            fName,
            lName,
            emailAddress,
            mobileNumber,
            password,
            chkTermsCondition
        )
    }

    private fun showValidationErrors(error: String) {
        val text = String.format(resources.getString(R.string.txt_error_message, error))
        Toast.makeText(this@SignupAct,text,Toast.LENGTH_SHORT).show()
    }



    private fun getUserRequest(): Map<String, String> {
        return binding.run {

            mapOf(
                "first_name" to binding.edFirstName.text.toString(),
                "last_name" to binding.edLastName.text.toString(),
                "email" to binding.edEmail.text.toString(),
                "mobile" to binding.edMobile.text.toString(),
                "password" to binding.edPassword.text.toString(),
                "type" to "User"
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
                        Log.e("Signup Response===",jsonObject.toString())
                        if (jsonObject.getString("status") == "1") {
                            Toast.makeText(
                                this@SignupAct,
                                "Signup Successfully",
                                Toast.LENGTH_SHORT
                            ).show()
                            startActivity(Intent(this@SignupAct, LoginAct::class.java))
                            finish()


                        } else {
                            Toast.makeText(
                                this@SignupAct,
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
                    Helper.showProgressMessage(this@SignupAct,getString(R.string.please_wait))
                }
            }
        })
    }


    override fun onDestroy() {
        super.onDestroy()
        //_binding = null

    }

}