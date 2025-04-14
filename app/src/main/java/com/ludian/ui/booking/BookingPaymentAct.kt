package com.ludian.ui.booking

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.ludian.R
import com.ludian.databinding.ActivityBookingPaymentBinding
import com.ludian.models.PromoCodeModel
import com.ludian.models.PropertyModel
import com.ludian.utils.Constants
import com.ludian.viewmodels.UserDataViewModel
import com.ludian.utils.Helper
import com.ludian.utils.NetworkResult
import com.ludian.utils.SharedPrf
import dagger.hilt.android.AndroidEntryPoint
import org.json.JSONObject
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.UUID

@AndroidEntryPoint
class BookingPaymentAct : AppCompatActivity(), PromoCodeBottomSheet.SelectCodeListener {
    private lateinit var binding: ActivityBookingPaymentBinding
    private lateinit var userDataViewModel: UserDataViewModel

    private var propertyModel : PropertyModel.Property? =null
   // private var plateformFees: Double = 2.99
    private var taxFees: Double = 0.0
    private var miscFees: Double = 0.99
    private var subTotal : Double = 0.0
    private var total : Double = 0.0

    private var startDate: String?=null
    private var endDate: String?=null
    private var guestCount: String?=null
    private var childCount: String?=null
    private var orderNumber: String?=null
    private var promoCodeId: String=""
    private var promoCodeDiscount: String=""
    private var promoCodeAmount: String=""
    private  var user_applied_coupon_id :String =""

    private val sharedPrf by lazy { SharedPrf(this) }
    private  var promoCodeModel : PromoCodeModel.Result?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_booking_payment)
        userDataViewModel = ViewModelProvider(this).get(UserDataViewModel::class.java)

        initViews()
    }

    private fun initViews() {
        propertyModel = intent.getSerializableExtra("propertyData") as PropertyModel.Property?
        startDate = intent?.getStringExtra("startDate")
        endDate = intent?.getStringExtra("endDate")
        guestCount = intent?.getStringExtra("guestCount")
        childCount = intent?.getStringExtra("childCount")


        binding.tvName.text = propertyModel!!.title
        binding.tvAddress.text = propertyModel!!.address
       binding.tvRating.text = propertyModel!!.ratting_avg
        binding.tvPrice.text =   propertyModel!!.price + " SAR "  + ",7"
        binding.tvBookingDate.text = getString(R.string.booking_on) + " : ${startDate}"

        subTotal = propertyModel!!.price.toDouble()

        Glide.with(this@BookingPaymentAct)
            .load(propertyModel!!.image_urls[0])
            .diskCacheStrategy(DiskCacheStrategy.ALL)
            .placeholder(com.denzcoskun.imageslider.R.drawable.default_loading)
            .error(com.denzcoskun.imageslider.R.drawable.default_error)
            .centerInside()
            .into(binding.ivImg)




        binding.ivBack.setOnClickListener {
            finish()
        }

        binding.tvConditions.setOnClickListener{
            val termsConditionBottomSheet = TermsConditionBottomSheet
                .newInstance(Constants.TERMS_CONDITION)
            termsConditionBottomSheet.show(supportFragmentManager, "")
        }


        binding.btnBook.setOnClickListener {
           if(binding.checkAgree.isChecked) {
               orderNumber = UUID.randomUUID().toString()
               val addPaymentRequest = addPaymentRequest(orderNumber!!)
               Log.e("payment Request===", addPaymentRequest.toString())
               Helper.showProgressMessage(this@BookingPaymentAct, getString(R.string.please_wait))
               userDataViewModel.payment(addPaymentRequest)

           }

            else {
                Toast.makeText(this@BookingPaymentAct,getString(R.string.accept_terms_condition),Toast.LENGTH_LONG).show()
           }


        }


        binding.tvCheckPromoCode.setOnClickListener {
            val promoCodeBottomSheet = PromoCodeBottomSheet
                .newInstance(sharedPrf.getStoredTag(SharedPrf.USER_ID),propertyModel!!.property_id,this@BookingPaymentAct)
            promoCodeBottomSheet.show(supportFragmentManager, "")
        }


        binding.btnApply.setOnClickListener {
            if(promoCodeId =="")
                Toast.makeText(this@BookingPaymentAct,getString(R.string.enter_promo_code),Toast.LENGTH_LONG).show()
            else{
                Helper.hideKeyboard(it)
                val applyCodeRequest = applyCodeRequest()
                Log.e("apply promo code Request===", applyCodeRequest.toString())
                Helper.showProgressMessage(this@BookingPaymentAct,getString(R.string.please_wait))
              //  userDataViewModel.applyPromoCode(sharedPrf.getStoredTag(SharedPrf.USER_ID),binding.edPromoCode.text.toString())
                userDataViewModel.applyPromoCode(applyCodeRequest)

            }
        }




        bindObservers()



        Helper.showProgressMessage(this@BookingPaymentAct,getString(R.string.please_wait))
        userDataViewModel.getTaxData(sharedPrf.getStoredTag(SharedPrf.TOKEN))

    }


    private fun getUserRequest(): Map<String, String> {
        return binding.run {
            mapOf(
                "property_id" to propertyModel!!.property_id,
                "guests" to guestCount!!,
                "children" to childCount!!,
                "user_id" to sharedPrf.getStoredTag(SharedPrf.USER_ID),
                "booking_request_date_start" to startDate!!,
                "booking_request_date_end" to endDate!!,
            )

        }
    }


    private fun addPaymentRequest(orderNumber:String): Map<String, String> {
        return binding.run {
            mapOf(
                "token" to sharedPrf.getStoredTag(SharedPrf.TOKEN),
                "user_id" to sharedPrf.getStoredTag(SharedPrf.USER_ID),
                "order_number" to orderNumber,
/*
                "amount" to  "1.00",
*/
                "amount" to  total.toString(),
                "currency" to "SAR",
                "first_name" to "test user",
                "last_name" to "Test",
                "country" to "SA",
                "city" to "Riyadh",
                "zip" to "12221",
                "email" to "usr1@gmail.com",
                "mobile" to "966565555555",
                "ipaddress" to "176.44.76.222",
                "address" to "just testdd",
                )

        }
    }

    private fun applyCodeRequest(): Map<String, String> {
        return binding.run {
            mapOf(
                "token" to sharedPrf.getStoredTag(SharedPrf.TOKEN) ,
                "user_id" to sharedPrf.getStoredTag(SharedPrf.USER_ID) ,
                "coupon_id" to promoCodeId,
                "property_id" to propertyModel!!.property_id
            /*
                                "promocode" to binding.edPromoCode.text.toString()
                */
            )

        }
    }



    private fun monthYearFromDate(date: LocalDate): String? {
        val formatter = DateTimeFormatter.ofPattern("MMMM yyyy")
        return date.format(formatter)
    }



    private fun bindObservers() {


        userDataViewModel.paymentLiveData.observe(this, Observer {
            Helper.hideProgressMessage()
            when (it) {
                is NetworkResult.Success -> {
                    try {
                        it.data?.let {
                            val jsonObject = JSONObject(it.string())
                            Log.e("payment Response===",jsonObject.toString())
                            if (jsonObject.getInt("status") == 1) {

                                startActivity(Intent(this@BookingPaymentAct, PaymentWebViewAct::class.java)
                                    .putExtra("url",jsonObject.getString("redirect_url"))
                                    .putExtra("property_id",propertyModel!!.property_id!!)
                                    .putExtra("guests", guestCount!!)
                                    .putExtra("children",childCount!!)
                                    .putExtra("user_id", sharedPrf.getStoredTag(SharedPrf.USER_ID))
                                    .putExtra("startDate",startDate!!)
                                    .putExtra("endDate",endDate!!)
                                    .putExtra("orderNumber",orderNumber)
                                    .putExtra("orderSubTotal",subTotal.toString())
                                    .putExtra("orderTotal",total.toString())
                                    //.putExtra("platformFee",plateformFees.toString())
                                    .putExtra("taxFee",taxFees.toString())
                                    .putExtra("promoCode_id",promoCodeId)
                                    .putExtra("promoCode_discount",promoCodeDiscount)
                                    .putExtra("promoCode_amount",promoCodeAmount)
                                    .putExtra("user_applied_coupon_id",user_applied_coupon_id)
                                )

                                finish()

                            } else {
                                Toast.makeText(
                                    this@BookingPaymentAct,
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
                    Helper.showProgressMessage(this@BookingPaymentAct,getString(R.string.please_wait))
                }

                else -> {}
            }
        })



/*
        userDataViewModel.propertyBookingLiveData.observe(this, Observer {
            Helper.hideProgressMessage()
            when (it) {
                is NetworkResult.Success -> {
                   try {
                       it.data?.let {
                           val jsonObject = JSONObject(it.string())
                           Log.e("Booking Response===",jsonObject.toString())
                           if (jsonObject.getInt("status") == 1) {
                               startActivity(Intent(this@BookingPaymentAct, BookingCompleteAct::class.java))
                               finish()
                           } else {
                               Toast.makeText(
                                   this@BookingPaymentAct,
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
                    Helper.showProgressMessage(this@BookingPaymentAct,getString(R.string.please_wait))
                }

                else -> {}
            }
        })
*/


        userDataViewModel.promoCodeLiveData.observe(this, Observer {
            Helper.hideProgressMessage()
            when (it) {
                is NetworkResult.Success -> {
                   try {
                       it.data?.let {
                           val jsonObject = JSONObject(it.string())
                           Log.e("PromoCode Response===",jsonObject.toString())
                           if (jsonObject.getInt("status") == 1) {
                               user_applied_coupon_id = jsonObject.getString("user_applied_coupon_id")
                              /* if(promoCodeModel!!.type=="FLAT") {
                                   subTotal = subTotal - promoCodeModel!!.description.toInt()
                                   total = subTotal  + taxFees
                                   binding.tvSubTotal.text =  "${String.format("%.2f", subTotal)} SAR "
                                   binding.tvTotal.text =  "SAR${String.format("%.2f", total)} SAR "
                               }
                               else {

                                   Log.e("discount value====", calculatePercentage(promoCodeModel!!.description.toDouble(),subTotal).toString())

                                   subTotal = subTotal -   calculatePercentage(promoCodeModel!!.description.toDouble(),subTotal)
                                   total = subTotal  + taxFees
                                   binding.tvSubTotal.text =  "${String.format("%.2f", subTotal)} SAR "
                                   binding.tvTotal.text =  "${String.format("%.2f", total)} SAR "
                               }*/

                               promoCodeAmount = calculatePercentage(promoCodeModel!!.discountPercent.toDouble(),subTotal).toString()
                               subTotal = subTotal -   calculatePercentage(promoCodeModel!!.discountPercent.toDouble(),subTotal)
                               total = subTotal  + taxFees
                               binding.tvSubTotal.text =  "${String.format("%.2f", subTotal)} SAR "
                               binding.tvTotal.text =  "${String.format("%.2f", total)} SAR "

                               Toast.makeText(this@BookingPaymentAct,getString(R.string.applied),Toast.LENGTH_LONG).show()


                               userDataViewModel.clearPromoCodeData()

                           }
                           else {
                               Toast.makeText(
                                   this@BookingPaymentAct,
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
                    Helper.showProgressMessage(this@BookingPaymentAct,getString(R.string.please_wait))
                }

                else -> {}
            }
        })



        userDataViewModel.taxLiveData.observe(this, Observer {
            Helper.hideProgressMessage()
            when (it) {
                is NetworkResult.Success -> {
                    try {
                        it.data?.let {
                            val jsonObject = JSONObject(it.string())
                            Log.e("Tax cal Response===",jsonObject.toString())
                            if (jsonObject.getInt("status") == 1) {

                                Log.e("discount value====", calculatePercentage(jsonObject.getJSONObject("result").getString("tax_amount").toDouble()  ,subTotal).toString())

                                taxFees = calculatePercentage(jsonObject.getJSONObject("result").getString("tax_amount").toDouble(),subTotal)

                                total = taxFees   + subTotal

                                binding.tvSubTotal.text =  "${String.format("%.2f", subTotal)} SAR "
                                binding.tvTaxFees.text =  "${String.format("%.2f", taxFees)} SAR "
                                //  binding.tvPlatformFees.text =  "SAR${String.format("%.2f", plateformFees)}"
                                // binding.tvMiscFees.text =  "$${String.format("%.2f", miscFees)}"
                                binding.tvTotal.text =  "${String.format("%.2f", total)} SAR "






                                userDataViewModel.clearTaxData()

                            }
                            else {
                                Toast.makeText(
                                    this@BookingPaymentAct,
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
                    Helper.showProgressMessage(this@BookingPaymentAct,getString(R.string.please_wait))
                }

                else -> {}
            }
        })



    }

    private fun calculatePercentage(part: Double, whole: Double): Double {
        return if (whole != 0.0) {
            (part * whole) / 100
        } else {
            0.0 // or handle the division by zero case as needed
        }
    }



    @SuppressLint("StringFormatMatches")
    private fun showValidationErrors(error: String) {
        val text = String.format(resources.getString(R.string.txt_error_message, error))
        Toast.makeText(this@BookingPaymentAct,text, Toast.LENGTH_SHORT).show()
    }

    override fun onSelectedCode(value: PromoCodeModel.Result) {
        promoCodeModel = value
        promoCodeId = promoCodeModel!!.id
        //promoCodeAmount = promoCodeModel!!.promoCode
        promoCodeDiscount = promoCodeModel!!.discountPercent
        binding.edPromoCode.setText(value.title)
        Helper.hideKeyboard(binding.edPromoCode)

    }


}