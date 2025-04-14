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
import com.ludian.databinding.ActivityFaqBinding
import com.ludian.models.FaqModel
import com.ludian.models.ReviewModel
import com.ludian.ui.profile.adapter.FaqAdapter
import com.ludian.utils.Helper
import com.ludian.utils.NetworkResult
import com.ludian.utils.SharedPrf
import com.ludian.viewmodels.ProfileViewModel
import dagger.hilt.android.AndroidEntryPoint
import org.json.JSONObject

@AndroidEntryPoint
class FaqAct :  AppCompatActivity() {
    private lateinit var binding : ActivityFaqBinding
    private lateinit var profileViewModel: ProfileViewModel
    private lateinit var faqAdapter: FaqAdapter
    private var arrayList:ArrayList<FaqModel.Data>?=null
    private val sharedPrf by lazy { SharedPrf(this@FaqAct) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_faq)
        profileViewModel = ViewModelProvider(this).get(ProfileViewModel::class.java)

        initViews()
    }

    private fun initViews() {

        arrayList = ArrayList()

        faqAdapter = FaqAdapter(this@FaqAct,arrayList)
        binding.rvFaq.adapter = faqAdapter


        binding.ivBack.setOnClickListener {
            finish()
        }

        bindObservers()


        Helper.showProgressMessage(this@FaqAct,getString(R.string.please_wait))
        profileViewModel.getFaq(sharedPrf.getStoredTag(SharedPrf.TOKEN))

    }


    private fun bindObservers() {
        profileViewModel.faqLiveData.observe(this, Observer { it ->
            Helper.hideProgressMessage()
            when (it) {
                is NetworkResult.Success -> {
                    try {
                        it.data?.let {
                            val jsonObject = JSONObject(it.string())
                            Log.e("faq Response===", jsonObject.toString())
                            if (jsonObject.getString("status") == "1") {
                                val faqModel: FaqModel = Gson().fromJson(
                                    jsonObject.toString(),
                                    FaqModel::class.java
                                )
                                arrayList!!.clear()
                                arrayList!!.addAll(faqModel.data)
                                faqAdapter.notifyAdapter(arrayList!!)
                                profileViewModel.clearFaqData()
                            } else {
                                arrayList!!.clear()
                                faqAdapter.notifyAdapter(arrayList!!)

                                Toast.makeText(
                                    this@FaqAct,
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
                        this@FaqAct,
                        getString(R.string.please_wait)
                    )
                }

                else -> {}
            }
        })


    }


    private fun showValidationErrors(error: String) {
        val text = String.format(resources.getString(R.string.txt_error_message, error))
        Toast.makeText(this@FaqAct, text, Toast.LENGTH_SHORT).show()

    }

}