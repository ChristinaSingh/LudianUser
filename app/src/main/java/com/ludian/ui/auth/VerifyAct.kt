package com.ludian.ui.auth

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.util.Log
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doAfterTextChanged
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.ludian.R
import com.ludian.databinding.ActivityVerifyBinding
import com.ludian.utils.Helper
import com.ludian.utils.NetworkResult
import com.ludian.viewmodels.AuthViewModel
import dagger.hilt.android.AndroidEntryPoint
import org.json.JSONObject

@AndroidEntryPoint
class VerifyAct : AppCompatActivity() {
    private lateinit var binding: ActivityVerifyBinding
    private lateinit var authViewModel : AuthViewModel
    private var email : String?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_verify)
        authViewModel = ViewModelProvider(this).get(AuthViewModel::class.java)

        initViews()
    }

    private fun initViews() {

        if(intent!=null) email = intent.getStringExtra("email")


        binding.ivBack.setOnClickListener {
            finish()
        }

        binding.ivBack!!.setOnClickListener {
            finish()
        }


        configOtpEditText(
            binding.et1,
            binding.et2,
            binding.et3,
            binding.et4
        )

        binding.et4.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                if (!s.toString().equals("", true)) {
                    binding.btnSubmit.performClick()
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }
        })




        binding.btnSubmit.setOnClickListener {
            val et1txt = binding.et1.text.toString()
            val et2txt = binding.et2.text.toString()
            val et3txt = binding.et3.text.toString()
            val et4txt = binding.et4.text.toString()
            if (TextUtils.isEmpty(et1txt)) {
                binding.et1.error = getString(R.string.empty)
            } else if (TextUtils.isEmpty(et2txt)) {
                binding.et2.error = getString(R.string.empty)
            } else if (TextUtils.isEmpty(et3txt)) {
                binding.et3.error = getString(R.string.empty)
            } else if (TextUtils.isEmpty(et4txt)) {
                binding.et4.error = getString(R.string.empty)
            } else {
                val otp = et1txt + et2txt + et3txt + et4txt
                val otpRequest =  geOtpRequest(otp)
                Log.e("Otp Verify Request===", otpRequest.toString())
                Helper.showProgressMessage(this@VerifyAct,getString(R.string.please_wait))
                authViewModel.otpVerify(otpRequest)

            }
        }


        bindObservers()

    }

    private fun geOtpRequest(otp : String): Map<String, String> {
        return binding.run {

            mapOf(
                "email" to email!!,
                "otp" to otp
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
                        Log.e("Otp Verify Response===",jsonObject.toString())
                        if (jsonObject.getString("status") == "1") {
                            startActivity(Intent(this@VerifyAct, NewPassAct::class.java)
                                .putExtra("user_id",jsonObject.getJSONObject("data").getString("id")))
                            finish()


                        } else {
                            Toast.makeText(
                                this@VerifyAct,
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
                    Helper.showProgressMessage(this@VerifyAct,getString(R.string.please_wait))
                }
            }
        })
    }



    private fun configOtpEditText(vararg etList: EditText) {
        val afterTextChanged = { index: Int, e: Editable? ->
            val view = etList[index]
            val text = e.toString()
            when (view.id) {
                etList[0].id -> {
                    if (text.isNotEmpty()) etList[index + 1].requestFocus()
                }
                etList[etList.size - 1].id -> {
                    if (text.isEmpty()) etList[index - 1].requestFocus()
                }
                else -> {
                    if (text.isNotEmpty()) etList[index + 1].requestFocus()
                    else etList[index - 1].requestFocus()
                }
            }
            false
        }
        etList.forEachIndexed { index, editText ->
            editText.doAfterTextChanged { afterTextChanged(index, it) }
        }
    }


    private fun showValidationErrors(error: String) {
        val text = String.format(resources.getString(R.string.txt_error_message, error))
        Toast.makeText(this@VerifyAct,text, Toast.LENGTH_SHORT).show()
    }

}