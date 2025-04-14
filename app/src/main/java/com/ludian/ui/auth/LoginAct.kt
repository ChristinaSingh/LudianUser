package com.ludian.ui.auth

import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.messaging.FirebaseMessaging
import com.ludian.R
import com.ludian.databinding.ActivityLoginBinding
import com.ludian.ui.home.HomeAct
import com.ludian.utils.Constants.USER_TOKEN
import com.ludian.utils.Helper
import com.ludian.utils.NetworkResult
import com.ludian.utils.SharedPrf
import com.ludian.utils.TokenManager
import com.ludian.viewmodels.AuthViewModel
import dagger.hilt.android.AndroidEntryPoint
import org.json.JSONObject
import java.util.Locale
import javax.inject.Inject

@AndroidEntryPoint
class LoginAct : AppCompatActivity() {
    private lateinit var binding : ActivityLoginBinding
    private lateinit var authViewModel : AuthViewModel
    private val sharedPrf by lazy { SharedPrf(this) }
    private lateinit var token : String

    @Inject
    lateinit var tokenManager: TokenManager


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_login)
        authViewModel = ViewModelProvider(this).get(AuthViewModel::class.java)
        initViews()
    }

    private fun initViews() {

        showLang(sharedPrf.getStoredTag(SharedPrf.LANGUAGE))


        if (sharedPrf.getStoredTag(SharedPrf.LANGUAGE) == "ar") {
            binding.edEmail.textAlignment = View.TEXT_ALIGNMENT_VIEW_START
            binding.edPassword.textAlignment = View.TEXT_ALIGNMENT_VIEW_START

        } else {

            binding.edEmail.textAlignment = View.TEXT_ALIGNMENT_VIEW_END
            binding.edPassword.textAlignment = View.TEXT_ALIGNMENT_VIEW_END

        }






        FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
            if (!task.isSuccessful) {
                Log.w("FCMToken", "Fetching FCM registration token failed", task.exception)
                return@addOnCompleteListener
            }

            // Get the new FCM registration token
             token = task.result
            Log.d("FCMToken", "FCM registration token: $token")

            // Send the token to your server or handle it as needed
        }


        binding.btnSignup!!.setOnClickListener {
            startActivity(Intent(this@LoginAct, SignupAct::class.java))
        }

        binding.btnForgotPassword!!.setOnClickListener {
            startActivity(Intent(this@LoginAct, ForgotPassAct::class.java))
        }


        binding.tvLanguage.setOnClickListener {
            showLanguageDialog(this@LoginAct)
        }


        binding.btnLogin.setOnClickListener {
            Helper.hideKeyboard(it)
            val validationResult = validateUserInput()
            if (validationResult.first) {
                val userRequest = getUserRequest()
                Log.e("Login Request===", userRequest.toString())
                Helper.showProgressMessage(this@LoginAct,getString(R.string.please_wait))
                authViewModel.loginUser(userRequest)
            } else {
                showValidationErrors(validationResult.second)
            }
        }

        bindObservers()

    }


    private fun validateUserInput(): Pair<Boolean, String> {
        val emailAddress = binding.edEmail.text.toString()
        val password = binding.edPassword.text.toString()
        return authViewModel.validateLogin(
            this@LoginAct,
            emailAddress,
            password
        )
    }

    private fun showValidationErrors(error: String) {
        val text = String.format(resources.getString(R.string.txt_error_message, error))
        Toast.makeText(this@LoginAct,text, Toast.LENGTH_SHORT).show()
    }


    private fun getUserRequest(): Map<String, String> {
        return binding.run {

            mapOf(
                "email" to binding.edEmail.text.toString(),
                "password" to binding.edPassword.text.toString(),
                "device_id" to token,
                "device_type" to "ANDROID",
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
                        Log.e("Login Response===",jsonObject.toString())
                        if (jsonObject.getString("status") == "1") {
                            tokenManager.saveToken(jsonObject.getJSONObject("data").getString("access_token"))
                            sharedPrf.setStoredTag(SharedPrf.USER_ID, jsonObject.getJSONObject("data").getString("id"))
                            sharedPrf.setStoredTag(SharedPrf.LOGIN, "true")
                            sharedPrf.setStoredTag(SharedPrf.TOKEN,jsonObject.getJSONObject("data").getString("access_token"))
                            Toast.makeText(
                                this@LoginAct,
                                "Login Successfully",
                                Toast.LENGTH_SHORT
                            ).show()
                            startActivity(Intent(this@LoginAct, HomeAct::class.java))
                            finish()


                        } else {
                            Toast.makeText(
                                this@LoginAct,
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
                    Helper.showProgressMessage(this@LoginAct,getString(R.string.please_wait))
                }

                else -> {}
            }
        })
    }


    override fun onDestroy() {
        super.onDestroy()
        //_binding = null

    }


    private fun showLanguageDialog(context: Context) {
        val languages = arrayOf(getString(R.string.english), getString(R.string.arabic))

        AlertDialog.Builder(context)
            .setTitle(getString(R.string.choose_language))
            .setItems(languages) { dialog, which ->
                when (which) {
                    0 -> setLocale("en") // English
                    1 -> setLocale("ar") // Arabic
                }
                dialog.dismiss()
            }
            .create()
            .show()
    }


    private fun setLocale(lang: String) {
        val locale = Locale(lang)
        Locale.setDefault(locale)

        val config = Configuration()
        config.setLocale(locale)
        resources.updateConfiguration(config, resources.displayMetrics)

        when (lang) {
            "en" -> binding.tvLanguage.text = getString(R.string.english)
            "ar" -> binding.tvLanguage.text = getString(R.string.arabic)
            else -> binding.tvLanguage.text = getString(R.string.english)
        }

        binding.tvLogin.text = getString(R.string.login)
        binding.tvLogin11.text = getString(R.string.enter_your_email_password)
        binding.edEmail.setHint(getString(R.string.email_address))
        binding.edPassword.setHint(getString(R.string.password))
        binding.btnForgotPassword.text = getString(R.string.fogot_your_password)
        binding.btnLogin.text = getString(R.string.login)
        binding.btnSignup11.text = getString(R.string.do_not_have_an_account)
        binding.btnSignup.text = getString(R.string.signup)
        binding.tvOr.text = getString(R.string.or)
        binding.btnGPlus.text = getString(R.string.sign_in_with_google)



        sharedPrf.setStoredTag(SharedPrf.LANGUAGE, lang)

        // Restart activity to apply changes
         startActivity(Intent(this@LoginAct,SplashAct::class.java).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or  Intent.FLAG_ACTIVITY_CLEAR_TOP))
         finish()
    }


    private fun showLang(language: String) {
        when (language) {
            "en" -> binding.tvLanguage.text = getString(R.string.english)
            "ar" -> binding.tvLanguage.text = getString(R.string.arabic)
            else -> binding.tvLanguage.text = getString(R.string.english)
        }
    }



}