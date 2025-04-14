package com.ludian.ui.propertydeeplink

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.google.gson.Gson
import com.ludian.R
import com.ludian.databinding.ActivityPropertyDetailBinding
import com.ludian.databinding.ActivityPropertyDetailDeeplinkBinding
import com.ludian.models.PropertyDetailModel
import com.ludian.models.PropertyModel
import com.ludian.models.ReviewModel
import com.ludian.ui.booking.AddReviewBottomSheet
import com.ludian.ui.booking.adapter.ImageSliderAdapter
import com.ludian.ui.booking.adapter.PreviewAdapter
import com.ludian.ui.booking.adapter.ReviewAdapter
import com.ludian.ui.calender.CalendarAct
import com.ludian.utils.Helper
import com.ludian.utils.NetworkResult
import com.ludian.utils.SharedPrf
import com.ludian.viewmodels.PropertyDetailViewModel
import dagger.hilt.android.AndroidEntryPoint
import org.json.JSONObject
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Timer
import java.util.TimerTask

@AndroidEntryPoint
class DeeplinkPropertyDetailAct : AppCompatActivity(),AddReviewBottomSheet.AddReviewListener {
    private lateinit var binding: ActivityPropertyDetailDeeplinkBinding

    private var propertyModel : PropertyDetailModel?=null
    private var imageArrayList : ArrayList<String> = ArrayList()
    private lateinit var propertyDetailViewModel: PropertyDetailViewModel
    private val sharedPrf by lazy { SharedPrf(this) }
    private lateinit var reviewAdapter: ReviewAdapter
    private var arrayList : ArrayList<ReviewModel.Data> = ArrayList()

    private var currentPage = 0
    private val DELAY_MS: Long = 3000 // Delay in milliseconds before auto sliding starts
    private val PERIOD_MS: Long = 3000 // Period in milliseconds between each slide

    private val handler = Handler()
    private val timer = Timer()
    private var type:String=""
    private var propertyId:String=""


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_property_detail_deeplink)
        propertyDetailViewModel = ViewModelProvider(this).get(PropertyDetailViewModel::class.java)

        initViews()
    }

    private fun initViews() {

        val deepLinkUri: Uri? = intent?.data

        deepLinkUri?.let {
            // Retrieve query parameters from the URI (e.g., paymentId)
            propertyId = it.getQueryParameter("propertyId").toString()
            type = it.getQueryParameter("type").toString()
            getPropertyDetail()
        }


      //  propertyModel = intent.getSerializableExtra("propertyData") as PropertyModel.Property?
     //   type = intent.getStringExtra("type")!!


        binding.btnBook.setOnClickListener {
            startActivity(
                Intent(this@DeeplinkPropertyDetailAct, CalendarAct::class.java)
                .putExtra("price",propertyModel!!.properties.price)
                .putExtra("propertyId",propertyModel!!.properties.property_id)
                .putExtra("propertyData",propertyModel!!))
        }


        binding.llShare.setOnClickListener {
            shareLink(propertyModel!!.properties.property_id);
        }



        binding.ivBack.setOnClickListener {
            finish()
        }





        binding.rlReview.setOnClickListener {
           //  val addReviewBottomSheet = AddReviewBottomSheet
           //     .newInstance(sharedPrf.getStoredTag(SharedPrf.USER_ID),this@DeeplinkPropertyDetailAct,propertyModel!!)
          //  addReviewBottomSheet.show(supportFragmentManager, "")
        }


        if (type=="search") {
            propertyDetailViewModel.saveRecentProperty(sharedPrf.getStoredTag(SharedPrf.TOKEN),propertyModel!!.properties.property_id)
        }


        bindObservers()


    }


    private fun getAllReviews(){
        val getReviewRequest = getReviewRequest()
        Log.e("get All review Request===", getReviewRequest.toString())
        Helper.showProgressMessage(this@DeeplinkPropertyDetailAct,getString(R.string.please_wait))
        propertyDetailViewModel.getReviews(getReviewRequest)
    }


    private fun getPropertyDetail(){
        val getPropertyDetailRequest = getPropertyDetailRequest()
        Log.e("get Property Detail Request===", getPropertyDetailRequest.toString())
        Helper.showProgressMessage(this@DeeplinkPropertyDetailAct,getString(R.string.please_wait))
        propertyDetailViewModel.getPropertyDetailDeepLink(getPropertyDetailRequest)
    }




    private fun setViewData() {

        imageArrayList.clear()
        imageArrayList.addAll(propertyModel!!.properties.image_urls)

        arrayList = ArrayList()

        reviewAdapter = ReviewAdapter(this@DeeplinkPropertyDetailAct,arrayList)
        binding.rvReview.adapter = reviewAdapter



        binding.viewPager.adapter = ImageSliderAdapter(this@DeeplinkPropertyDetailAct,imageArrayList)
        binding.dotsIndicator.setViewPager2(binding.viewPager)
        startAutoSlide()


        binding.rvPreview.adapter = PreviewAdapter(this@DeeplinkPropertyDetailAct,imageArrayList)

        binding.tvName.text = propertyModel!!.properties.title
        binding.tvAddress.text = propertyModel!!.properties.address
        binding.tvDescription.text = propertyModel!!.properties.description
        binding.tvPrice.text = "$" +  propertyModel!!.properties.price + ",7"

        binding.tvBedroom.text =   propertyModel!!.properties.bedrooms + " bedrooms"
        binding.tvGuest.text =   propertyModel!!.properties.guests + " guests max."
        //  binding.tvHouse.text =  propertyModel!!.house + " House"
        //   binding.tvSize.text =  propertyModel!!.size + " m²"
        binding.tvBathroom.text =  propertyModel!!.properties.bathrooms + " bathrooms"

        if(propertyModel!!.properties.book_marked) binding.ivBookmark.setImageResource(R.drawable.ic_bookmark_select)
        else binding.ivBookmark.setImageResource(R.drawable.ic_bookmark1)

        /* if(propertyModel!!.wifi=="1") {
             binding.iv1.visibility = View.VISIBLE
             binding.tvWifi.visibility = View.VISIBLE
         }
         else {
             binding.iv1.visibility = View.GONE
             binding.tvWifi.visibility = View.GONE
         }

         if(propertyModel!!.breakfast=="1") {
             binding.iv2.visibility = View.VISIBLE
             binding.tvBreakfast.visibility = View.VISIBLE
         }
         else {
             binding.iv2.visibility = View.GONE
             binding.tvBreakfast.visibility = View.GONE
         }*/


        getAllReviews()

    }


    private fun startAutoSlide() {
        val update = Runnable {
            if (currentPage == propertyModel!!.properties.image_urls.size) {
                currentPage = 0
            }
            binding.viewPager.setCurrentItem(currentPage++, true)
        }

        timer.schedule(object : TimerTask() {
            override fun run() {
                handler.post(update)
            }
        }, DELAY_MS, PERIOD_MS)
    }




    private fun bindObservers() {
        propertyDetailViewModel.searchLiveData.observe(this, Observer { it ->
            Helper.hideProgressMessage()
            when (it) {
                is NetworkResult.Success -> {
                    try {
                        it.data?.let {
                            val jsonObject = JSONObject(it.string())
                            Log.e("search save Recent property Response===", jsonObject.toString())
                            if (jsonObject.getInt("status") == 1) {


                                propertyDetailViewModel.clearSearchData()
                            } else {

                            }
                        }
                    }catch (e:Exception){
                        e.printStackTrace()
                    }
                }

                is NetworkResult.Error -> {
                    showValidationErrors(it.message.toString())
                }

                is NetworkResult.Loading -> {
                    Helper.showProgressMessage(this@DeeplinkPropertyDetailAct, getString(R.string.please_wait))
                }

                else -> {}
            }
        })

        propertyDetailViewModel.addReviewResponseLiveData.observe(this, Observer { it ->
            Helper.hideProgressMessage()
            when (it) {
                is NetworkResult.Success -> {
                    try {
                        it.data?.let {
                            val jsonObject = JSONObject(it.string())
                            Log.e("add review on property Response===", jsonObject.toString())
                            if (jsonObject.getString("status") == "1") {
                                getAllReviews()
                                propertyDetailViewModel.clearAddReviewData()
                            } else {

                            }
                        }
                    }catch (e:Exception){
                        e.printStackTrace()
                    }
                }

                is NetworkResult.Error -> {
                    showValidationErrors(it.message.toString())
                }

                is NetworkResult.Loading -> {
                    Helper.showProgressMessage(this@DeeplinkPropertyDetailAct, getString(R.string.please_wait))
                }

                else -> {}
            }
        })


        propertyDetailViewModel.reviewResponseLiveData.observe(this, Observer { it ->
            Helper.hideProgressMessage()
            when (it) {
                is NetworkResult.Success -> {
                    try {
                        it.data?.let {
                            val jsonObject = JSONObject(it.string())
                            Log.e("get review on property Response===", jsonObject.toString())
                            if (jsonObject.getString("status") == "1") {
                                val reviewModel: ReviewModel = Gson().fromJson(
                                    jsonObject.toString(),
                                    ReviewModel::class.java
                                )
                                arrayList.clear()
                                arrayList.addAll(reviewModel.data)
                                reviewAdapter.notifyAdapter(arrayList)

                                propertyDetailViewModel.clearReviewData()
                            } else {
                                arrayList!!.clear()
                                reviewAdapter.notifyAdapter(arrayList)



                            }
                        }
                    }catch (e:Exception){
                        e.printStackTrace()
                    }
                }

                is NetworkResult.Error -> {
                    showValidationErrors(it.message.toString())
                }

                is NetworkResult.Loading -> {
                    Helper.showProgressMessage(this@DeeplinkPropertyDetailAct, getString(R.string.please_wait))
                }

                else -> {}
            }
        })


        propertyDetailViewModel.propertyDetailLiveData.observe(this, Observer { it ->
            Helper.hideProgressMessage()
            when (it) {
                is NetworkResult.Success -> {
                    try {
                        it.data?.let {
                            val jsonObject = JSONObject(it.string())
                            Log.e("getProperty Detail Response===", jsonObject.toString())
                            if (jsonObject.getString("status") == "1") {
                                propertyModel = Gson().fromJson(
                                    jsonObject.toString(),
                                    PropertyDetailModel::class.java
                                )
                                setViewData()

                                propertyDetailViewModel.clearPropertyDetailData()
                            } else {
                                arrayList!!.clear()
                                reviewAdapter.notifyAdapter(arrayList)



                            }
                        }
                    }catch (e:Exception){
                        e.printStackTrace()
                    }
                }

                is NetworkResult.Error -> {
                    showValidationErrors(it.message.toString())
                }

                is NetworkResult.Loading -> {
                    Helper.showProgressMessage(this@DeeplinkPropertyDetailAct, getString(R.string.please_wait))
                }

                else -> {}
            }
        })


    }


    private fun showValidationErrors(error: String) {
        val text = String.format(resources.getString(R.string.txt_error_message, error))
        Toast.makeText(this@DeeplinkPropertyDetailAct,text, Toast.LENGTH_SHORT).show()
    }

    override fun addReview(userId: String, propertyId: String, rating: String, comment: String) {
        val currentDate = LocalDate.now()
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
        val formattedDate = currentDate.format(formatter)
        val addReviewRequest = addReviewRequest(userId, propertyId, rating, comment,formattedDate)
        Log.e("add Review Request Request===", addReviewRequest.toString())
        Helper.showProgressMessage(this@DeeplinkPropertyDetailAct,getString(R.string.please_wait))
        propertyDetailViewModel.addReview(addReviewRequest)
    }



    private fun getReviewRequest(): Map<String, String> {
        return binding.run {
            mapOf(
                "token" to sharedPrf.getStoredTag(SharedPrf.TOKEN),
                "property_id" to propertyModel!!.properties.property_id,
            )

        }
    }

    private fun getPropertyDetailRequest(): Map<String, String> {
        return binding.run {
            mapOf(
                "token" to sharedPrf.getStoredTag(SharedPrf.TOKEN),
                "property_id" to propertyId,
            )

        }
    }




    private fun addReviewRequest(userId: String, propertyId: String, rating: String, comment: String,currentDate:String): Map<String, String> {
        return binding.run {
            mapOf(
                "token" to sharedPrf.getStoredTag(SharedPrf.TOKEN),
                "user_id" to userId ,
                "property_id" to propertyId,
                "rating" to rating,
                "review" to comment,
                "current_date" to currentDate
            )

        }
    }

    private fun shareLink(propertyId: String) {
        val link = createDeepLink(propertyId)
        val sendIntent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, link)
            type = "text/plain"
        }
        startActivity(Intent.createChooser(sendIntent, "Share property link"))
    }

    private fun createDeepLink(propertyId: String): String {
        return "https://www.ludian.com/property?propertyId=$propertyId"
    }

}