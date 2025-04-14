package com.ludian.ui.profile

import android.os.Bundle
import android.text.Html
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.ludian.R
import com.ludian.databinding.ActivityAboutBinding
import com.ludian.utils.Helper
import com.ludian.utils.NetworkResult
import com.ludian.utils.SharedPrf
import com.ludian.viewmodels.ProfileViewModel
import dagger.hilt.android.AndroidEntryPoint
import org.json.JSONObject

@AndroidEntryPoint
class AboutAct  : AppCompatActivity() {
    private lateinit var binding : ActivityAboutBinding
    private lateinit var profileViewModel: ProfileViewModel
    private val sharedPrf by lazy { SharedPrf(this@AboutAct) }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_about)
        profileViewModel = ViewModelProvider(this).get(ProfileViewModel::class.java)

        initViews()
    }

    private fun initViews() {

        binding.ivBack.setOnClickListener {
            finish()
        }

        bindObservers()


        Helper.showProgressMessage(this@AboutAct,getString(R.string.please_wait))
        profileViewModel.getAboutUs(sharedPrf.getStoredTag(SharedPrf.TOKEN))

    }

    private fun bindObservers() {
        profileViewModel.aboutUsLiveData.observe(this, Observer { it ->
            Helper.hideProgressMessage()
            when (it) {
                is NetworkResult.Success -> {
                    try {
                        it.data?.let {
                            val jsonObject = JSONObject(it.string())
                            Log.e("about us Response===", jsonObject.toString())
                            if (jsonObject.getString("status") == "1") {
                                binding.tvData.text = Html.fromHtml(jsonObject.getJSONObject("data").getString("description"))
                                profileViewModel.clearAboutUsData()
                            } else {
                                Toast.makeText(
                                    this@AboutAct,
                                    jsonObject.getString("message"),
                                    Toast.LENGTH_LONG
                                ).show()
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
                        this@AboutAct,
                        getString(R.string.please_wait)
                    )
                }

                else -> {}
            }
        })


    }


    private fun showValidationErrors(error: String) {
        val text = String.format(resources.getString(R.string.txt_error_message, error))
        Toast.makeText(this@AboutAct, text, Toast.LENGTH_SHORT).show()

    }

}
