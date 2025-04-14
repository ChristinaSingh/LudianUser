package com.ludian.ui.booking

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Bitmap
import android.net.http.SslError
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.webkit.SslErrorHandler
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.ludian.R
import com.ludian.databinding.ActivityPaymentWebViewBinding
import com.ludian.utils.Helper
import com.ludian.utils.NetworkResult
import com.ludian.utils.SharedPrf
import com.ludian.viewmodels.UserDataViewModel
import dagger.hilt.android.AndroidEntryPoint
import org.json.JSONObject

@AndroidEntryPoint
class PaymentWebViewAct : AppCompatActivity() {
    private lateinit var binding: ActivityPaymentWebViewBinding
    private lateinit var userDataViewModel: UserDataViewModel
    private var startDate: String? = null
    private var endDate: String? = null
    private var guestCount: String? = null
    private var childCount: String? = null
    private var url: String? = null
    private var propertyId: String? = null
    private var userId: String? = null

    private var orderNumber: String? = null
    private var orderSubTotal: String? = null
    private var orderTotal: String? = null
    private var platformFee: String? = null
    private var taxFee: String? = null
    private var promoCodeId: String? = null
    private var promoCodeDiscount: String? = null
    private var promoCodeAmount: String? = null
    private  var user_applied_coupon_id :String? =""
    private val sharedPrf by lazy { SharedPrf(this@PaymentWebViewAct) }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_payment_web_view)
        userDataViewModel = ViewModelProvider(this).get(UserDataViewModel::class.java)
        initViews()
        bindObservers()


    }

    private fun initViews() {
        if (intent != null) {
            url = intent.getStringExtra("url")
            propertyId = intent.getStringExtra("property_id")
            guestCount = intent.getStringExtra("guests")
            childCount = intent.getStringExtra("children")
            userId = intent.getStringExtra("user_id")
            startDate = intent.getStringExtra("startDate")
            endDate = intent.getStringExtra("endDate")

            orderNumber = intent.getStringExtra("orderNumber")
            orderSubTotal = intent.getStringExtra("orderSubTotal")
            orderTotal = intent.getStringExtra("orderTotal")
          //  platformFee = intent.getStringExtra("platformFee")
            taxFee = intent.getStringExtra("taxFee")
            promoCodeId = intent.getStringExtra("promoCode_id")
            promoCodeDiscount = intent.getStringExtra("promoCode_discount")
            promoCodeAmount = intent.getStringExtra("promoCode_amount")
            user_applied_coupon_id = intent.getStringExtra("user_applied_coupon_id")



        }
        binding.webView.settings.javaScriptEnabled = true
        binding.webView.settings.loadWithOverviewMode = true
        binding.webView.settings.useWideViewPort = true
        binding.webView.settings.domStorageEnabled = true

        binding.webView.webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?): Boolean {
                // Handle URL loading here
                return false
            }

            override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                binding.progressBar.show()
                super.onPageStarted(view, url, favicon)
            }

            override fun onPageFinished(view: WebView?, url: String?) {
                binding.progressBar.hide()
                super.onPageFinished(view, url)
                Log.e("payment url=====", url!!)
                if (url.contains("status=success")) {
                    val userRequest = getUserRequest()
                    Log.e("Booking Request===", userRequest.toString())
                    Helper.showProgressMessage(this@PaymentWebViewAct,getString(R.string.please_wait))
                    userDataViewModel.propertyBooking(userRequest)
                }

            }
        }

        binding.progressBar.show()

        /* Enable Javascript in Webview */

        binding.webView.loadUrl(url!!);


    }


    private fun getUserRequest(): Map<String, String> {
        return binding.run {
            mapOf(
                "token" to sharedPrf.getStoredTag(SharedPrf.TOKEN)!!,
                "property_id" to propertyId!!,
                "guests" to guestCount!!,
                "children" to childCount!!,
                "user_id" to userId!!,
                "booking_request_date_start" to startDate!!,
                "booking_request_date_end" to endDate!!,

                "orderNumber" to orderNumber!!,
                "orderSubTotal" to orderSubTotal!!,
                "orderTotal" to orderTotal!!,
                "platformFee" to "0.0",
                "taxFee" to taxFee!!,
                "taxPercent" to "",
                "promoCode_id" to promoCodeId!!,
                "promoCode_discount" to promoCodeDiscount!!,
                "promoCode_amount" to promoCodeAmount!!,
                "user_applied_coupon_id" to user_applied_coupon_id!!,
                )

        }
    }


    private fun bindObservers() {
        userDataViewModel.propertyBookingLiveData.observe(this, Observer {
            Helper.hideProgressMessage()
            when (it) {
                is NetworkResult.Success -> {
                    try {
                        it.data?.let {
                            val jsonObject = JSONObject(it.string())
                            Log.e("Booking Response===",jsonObject.toString())
                            if (jsonObject.getInt("status") == 1) {
                                startActivity(Intent(this@PaymentWebViewAct, BookingCompleteAct::class.java))
                                finish()
                            } else {
                                Toast.makeText(
                                    this@PaymentWebViewAct,
                                    "" + jsonObject.getString("message"),
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                            //  Log.e("TAG", "observers: $it.")
                        }
                    }catch (e:Exception){
                        e.printStackTrace()
                    }
                }

                is NetworkResult.Error -> {
                    showValidationErrors(it.message.toString())
                }

                is NetworkResult.Loading -> {
                    Helper.showProgressMessage(this@PaymentWebViewAct,getString(R.string.please_wait))
                }

                else -> {}
            }
        })
    }

    @SuppressLint("StringFormatMatches")
    private fun showValidationErrors(error: String) {
        val text = String.format(resources.getString(R.string.txt_error_message, error))
        Toast.makeText(this@PaymentWebViewAct,text, Toast.LENGTH_SHORT).show()
    }

}