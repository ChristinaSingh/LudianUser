package com.ludian.ui.notification

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.google.gson.Gson
import com.ludian.R
import com.ludian.databinding.ActivityExplorePropertyBinding
import com.ludian.databinding.ActivityNotificationsBinding
import com.ludian.models.ExploreModel
import com.ludian.models.NotificationModel
import com.ludian.models.PropertyModel
import com.ludian.ui.bookmark.BookmarkAdapter
import com.ludian.ui.notification.adapter.NotificationAdapter
import com.ludian.utils.Helper
import com.ludian.utils.NetworkResult
import com.ludian.utils.SharedPrf
import com.ludian.viewmodels.ExploreViewModel
import com.ludian.viewmodels.NotificationViewModel
import dagger.hilt.android.AndroidEntryPoint
import org.json.JSONObject

@AndroidEntryPoint
class NotificationAct :  AppCompatActivity() {
    private lateinit var binding: ActivityNotificationsBinding
    private lateinit var notificationViewModel: NotificationViewModel
    private val sharedPrf by lazy { SharedPrf(this@NotificationAct) }


    private lateinit var notificationAdapter: NotificationAdapter
    private var arrayList: ArrayList<NotificationModel.Data>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_notifications)
        notificationViewModel = ViewModelProvider(this).get(NotificationViewModel::class.java)
        initViews()
    }

    private fun initViews() {
        arrayList = ArrayList()

        notificationAdapter = NotificationAdapter(this@NotificationAct,arrayList!!)
        binding.rvNotification.adapter = notificationAdapter


        binding.ivBack.setOnClickListener {
            finish()
        }


        bindObservers()


       notificationDataCall()
    }

    private fun notificationDataCall() {
        val notificationRequest = notificationRequest()
        Log.e("Notification Request===", notificationRequest.toString())
        Helper.showProgressMessage(this@NotificationAct, getString(R.string.please_wait))
        notificationViewModel.notificationData(notificationRequest)
    }

    private fun notificationRequest(): Map<String, String> {
        return binding.run {
            mapOf(
                "token" to sharedPrf.getStoredTag(SharedPrf.TOKEN),
                "owner_id" to sharedPrf.getStoredTag(SharedPrf.USER_ID),
                "type" to "User"
                )

        }
    }

    private fun bindObservers() {
        notificationViewModel.notificationLiveData.observe(this, Observer { it ->
            Helper.hideProgressMessage()
            when (it) {
                is NetworkResult.Success -> {
                    it.data?.let {
                        val jsonObject = JSONObject(it.string())
                        Log.e("Notification Response===", jsonObject.toString())
                        if (jsonObject.getString("status") == "1") {
                            val notificationModel: NotificationModel = Gson().fromJson(
                                jsonObject.toString(),
                                NotificationModel::class.java
                            )
                            arrayList!!.clear()
                            arrayList!!.addAll(notificationModel.data)
                            notificationAdapter.notifyAdapter(arrayList!!)
                            binding.tvNotFound.visibility = View.GONE;


                            notificationViewModel.clearNotificationData()
                        } else {
                            arrayList!!.clear()
                            notificationAdapter.notifyAdapter(arrayList!!)
                            binding.tvNotFound.visibility = View.VISIBLE;


                        }
                        //  Log.e("TAG", "observers: $it.")
                    }


                }

                is NetworkResult.Error -> {
                    showValidationErrors(it.message.toString())
                }

                is NetworkResult.Loading -> {
                    Helper.showProgressMessage(this@NotificationAct, getString(R.string.please_wait))
                }

                else -> {}
            }
        })


    }

    @SuppressLint("StringFormatMatches")
    private fun showValidationErrors(error: String) {
        val text = String.format(resources.getString(R.string.txt_error_message, error))
        Toast.makeText(this@NotificationAct, text, Toast.LENGTH_SHORT).show()
    }

}