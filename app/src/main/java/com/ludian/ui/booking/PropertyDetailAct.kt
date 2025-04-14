package com.ludian.ui.booking

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.google.gson.Gson
import com.ludian.R
import com.ludian.databinding.ActivityPropertyDetailBinding
import com.ludian.models.PropertyModel
import com.ludian.models.ReviewModel
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
class PropertyDetailAct : AppCompatActivity(), AddReviewBottomSheet.AddReviewListener {
    private lateinit var binding: ActivityPropertyDetailBinding

    private var propertyModel : PropertyModel.Property? =null
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




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_property_detail)
        propertyDetailViewModel = ViewModelProvider(this).get(PropertyDetailViewModel::class.java)

        initViews()
    }

    private fun initViews() {


        propertyModel = intent.getSerializableExtra("propertyData") as PropertyModel.Property?
        type = intent.getStringExtra("type")!!

        setViewData()


        binding.btnBook.setOnClickListener {
            startActivity(Intent(this@PropertyDetailAct, CalendarAct::class.java)
                .putExtra("price",propertyModel!!.price)
                .putExtra("propertyId",propertyModel!!.property_id)
                .putExtra("propertyData",propertyModel))
        }


        binding.llShare.setOnClickListener {
            shareLink(propertyModel!!.property_id);
        }



        binding.ivBack.setOnClickListener {
            finish()
        }






        /*binding.rlReview.setOnClickListener {
            val addReviewBottomSheet = AddReviewBottomSheet
                .newInstance(sharedPrf.getStoredTag(SharedPrf.USER_ID),this@PropertyDetailAct,propertyModel!!)
            addReviewBottomSheet.show(supportFragmentManager, "")
        }*/

        bindObservers()

      if (type=="search") {
          propertyDetailViewModel.saveRecentProperty(sharedPrf.getStoredTag(SharedPrf.TOKEN),propertyModel!!.property_id)
      }


        getAllReviews()
    }


    private fun getAllReviews(){
        val getReviewRequest = getReviewRequest()
        Log.e("get All review Request===", getReviewRequest.toString())
        Helper.showProgressMessage(this@PropertyDetailAct,getString(R.string.please_wait))
        propertyDetailViewModel.getReviews(getReviewRequest)
    }


    private fun setViewData() {

        imageArrayList.clear()
        imageArrayList.addAll(propertyModel!!.image_urls)

        arrayList = ArrayList()

        reviewAdapter = ReviewAdapter(this@PropertyDetailAct,arrayList)
        binding.rvReview.adapter = reviewAdapter



        binding.viewPager.adapter = ImageSliderAdapter(this@PropertyDetailAct,imageArrayList)
        binding.dotsIndicator.setViewPager2(binding.viewPager)
        startAutoSlide()


        binding.rvPreview.adapter = PreviewAdapter(this@PropertyDetailAct,imageArrayList)

        binding.tvName.text = propertyModel!!.title
        binding.tvAddress.text = propertyModel!!.address
        binding.tvDescription.text = propertyModel!!.description
        binding.tvPrice.text =   propertyModel!!.price + " SAR "  + ",7"
        binding.tvRating.text = propertyModel!!.ratting_avg


        binding.tvBedroom.text =   propertyModel!!.bedrooms + " "+ getString(R.string.bedrooms)
        binding.tvGuest.text =   propertyModel!!.guests + " " + getString(R.string.guest_max)
        binding.tvHouse.text =  propertyModel!!.rent_string_name
        binding.tvSize.text =  propertyModel!!.squreMeter + " m²"
        binding.tvBathroom.text =  propertyModel!!.bathrooms + " " + getString(R.string.bathrooms)

        if(propertyModel!!.book_marked) binding.ivBookmark.setImageResource(R.drawable.ic_bookmark_select)
        else binding.ivBookmark.setImageResource(R.drawable.ic_bookmark1)

        if(propertyModel!!.ratting_avg=="0.0") {
            binding.iv3.visibility = View.GONE
            binding.tvRating.visibility = View.GONE
        }
        else {
            binding.iv3.visibility = View.VISIBLE
            binding.tvRating.visibility = View.VISIBLE
        }

        if(propertyModel!!.wifi) {
            binding.iv1.visibility = View.VISIBLE
            binding.tvWifi.visibility = View.VISIBLE
        }
        else {
            binding.iv1.visibility = View.GONE
            binding.tvWifi.visibility = View.GONE
        }






    }


    private fun startAutoSlide() {
        val update = Runnable {
            if (currentPage == propertyModel!!.image_urls.size) {
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
                    Helper.showProgressMessage(this@PropertyDetailAct, getString(R.string.please_wait))
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
                    Helper.showProgressMessage(this@PropertyDetailAct, getString(R.string.please_wait))
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
                    Helper.showProgressMessage(this@PropertyDetailAct, getString(R.string.please_wait))
                }

                else -> {}
            }
        })

    }


    private fun showValidationErrors(error: String) {
        val text = String.format(resources.getString(R.string.txt_error_message, error))
        Toast.makeText(this@PropertyDetailAct,text, Toast.LENGTH_SHORT).show()
    }

    override fun addReview(userId: String, propertyId: String, rating: String, comment: String) {
        val currentDate = LocalDate.now()
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
        val formattedDate = currentDate.format(formatter)
        val addReviewRequest = addReviewRequest(userId, propertyId, rating, comment,formattedDate)
        Log.e("add Review Request Request===", addReviewRequest.toString())
        Helper.showProgressMessage(this@PropertyDetailAct,getString(R.string.please_wait))
        propertyDetailViewModel.addReview(addReviewRequest)
    }



    private fun getReviewRequest(): Map<String, String> {
        return binding.run {
            mapOf(
                "token" to sharedPrf.getStoredTag(SharedPrf.TOKEN),
                "property_id" to propertyModel!!.property_id,
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
        return "https://www.ludian.com/property?propertyId=$propertyId&type=$type"
    }

}
