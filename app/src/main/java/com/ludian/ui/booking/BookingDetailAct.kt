package com.ludian.ui.booking

import android.annotation.SuppressLint
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
import com.ludian.databinding.ActivityBookingDetailBinding
import com.ludian.models.BookingModel
import com.ludian.models.ReviewModel
import com.ludian.ui.booking.adapter.ImageSliderAdapter
import com.ludian.ui.booking.adapter.PreviewAdapter
import com.ludian.ui.booking.adapter.ReviewAdapter
import com.ludian.ui.chat.ChatAct
import com.ludian.utils.Helper
import com.ludian.utils.NetworkResult
import com.ludian.utils.SharedPrf
import com.ludian.viewmodels.PropertyDetailViewModel
import com.ludian.viewmodels.UserDataViewModel
import dagger.hilt.android.AndroidEntryPoint
import org.json.JSONObject
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Timer
import java.util.TimerTask

@AndroidEntryPoint
class BookingDetailAct :  AppCompatActivity(),AddReviewBottomSheet.AddReviewListener{
    private lateinit var binding: ActivityBookingDetailBinding
    private lateinit var userDataViewModel: UserDataViewModel

    private var bookingModel : BookingModel.Data? =null
    private var imageArrayList : ArrayList<String> = ArrayList()
    private lateinit var propertyDetailViewModel: PropertyDetailViewModel
    private val sharedPrf by lazy { SharedPrf(this) }
    private lateinit var reviewAdapter: ReviewAdapter
    private var arrayList : ArrayList<ReviewModel.Data> = ArrayList()

    private var currentPage = 0
    private val DELAY_MS: Long = 3000 // Delay in milliseconds before auto sliding starts
    private val PERIOD_MS: Long = 3000 // Period in milliseconds between each slide
    private var position : Int = 0
    private val handler = Handler()
    private val timer = Timer()
    private var type:String=""


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_booking_detail)
        userDataViewModel = ViewModelProvider(this).get(UserDataViewModel::class.java)

        initViews()
    }

    private fun initViews() {

        bookingModel = intent.getSerializableExtra("BookingData") as BookingModel.Data?
        position  = intent.getStringExtra("position")!!.toInt()
        setViewData()

        binding.ivBack.setOnClickListener {
            finish()
        }

        binding.cardComplete.setOnClickListener {
            val bookingCompleteRequest = bookingCompleteRequest(bookingModel!!.booking_request_id)
            Log.e("Booking Complete Request===", bookingCompleteRequest.toString())
            Helper.showProgressMessage(this@BookingDetailAct,getString(R.string.please_wait))
            userDataViewModel.bookingComplete(bookingCompleteRequest)
        }


        binding.cardChat.setOnClickListener {
            startActivity(Intent(this, ChatAct::class.java).apply {
                putExtra("UserId", bookingModel!!.property.user_id)
                putExtra("UserName", bookingModel!!.property.user_name)
                putExtra("UserImage", "")
                putExtra("id", sharedPrf.getStoredTag(SharedPrf.USER_ID))
                putExtra("name", "")
                putExtra("img", "")
                putExtra("bookingID", bookingModel!!.booking_request_id)

            })
        }



        bindObserver()
    }

    private fun setViewData() {

        imageArrayList.clear()
        imageArrayList.addAll(bookingModel!!.property.image_urls)


        binding.rvPreview.adapter = PreviewAdapter(this@BookingDetailAct,imageArrayList)


        arrayList = ArrayList()

        binding.viewPager.adapter = ImageSliderAdapter(this@BookingDetailAct,imageArrayList)
        binding.dotsIndicator.setViewPager2(binding.viewPager)
        startAutoSlide()

        binding.tvName.text = bookingModel!!.property.title
        binding.tvAddress.text =  bookingModel!!.property.address
        binding.tvDescription.text =  bookingModel!!.property.description
        binding.tvPrice.text =   bookingModel!!.orderTotal+ " SAR "  + ",7"
        binding.tvStatus.text = bookingModel!!.booking_request_status
       // binding.tvDate1.text =    getString(R.string.booking_date)+" : ${bookingModel!!.booking_request_date_start}"
        binding.tvDate1.text =    getString(R.string.booking_date)+" : ${formatDates(bookingModel!!.booking_request_date_start).joinToString(",")}"

        binding.tvRating.text = bookingModel!!.property.average_rating
        binding.tvChildren.text = getString(R.string.children) + " : " + bookingModel!!.booking_request_children
        binding.tvPerson.text = getString(R.string.person) + " : " + bookingModel!!.booking_request_guests


        binding.tvBedroom.text =   bookingModel!!.property.bedrooms + " "+ getString(R.string.bedrooms)
        binding.tvGuest.text =   bookingModel!!.property.guests + " " + getString(R.string.guest_max)
        binding.tvHouse.text =  bookingModel!!.property.rent_string_name
        binding.tvSize.text =  bookingModel!!.property.squreMeter + " m²"
        binding.tvBathroom.text =  bookingModel!!.property.bathrooms + " " + getString(R.string.bathrooms)


         if(bookingModel!!.property.average_rating=="0.0") {
             binding.iv3.visibility = View.GONE
             binding.tvRating.visibility = View.GONE
      }
      else {
          binding.iv3.visibility = View.VISIBLE
             binding.tvRating.visibility = View.VISIBLE
      }

      if(bookingModel!!.property.wifi) {
          binding.iv1.visibility = View.VISIBLE
          binding.tvWifi.visibility = View.VISIBLE
      }
      else {
          binding.iv1.visibility = View.GONE
          binding.tvWifi.visibility = View.GONE
      }



        if(bookingModel!!.booking_request_status=="ACCEPT"){
            binding.cardComplete.visibility = View.VISIBLE
            binding.cardChat.visibility = View.VISIBLE

            binding.cardStaus.visibility = View.GONE
        }
        else {
            binding.cardComplete.visibility = View.GONE
            binding.cardChat.visibility = View.VISIBLE
            binding.cardStaus.visibility = View.VISIBLE
        }






    }


    private fun startAutoSlide() {
        val update = Runnable {
            if (currentPage ==  bookingModel!!.property.image_urls.size) {
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



    private fun bindObserver(){
        userDataViewModel.propertyBookingLiveData.observe(this, Observer {
            Helper.hideProgressMessage()
            when (it) {
                is NetworkResult.Success -> {
                    try {
                        it.data?.let {
                            val jsonObject = JSONObject(it.string())
                            Log.e("get booking Response===",jsonObject.toString())
                            if (jsonObject.getInt("status") == 1) {

                                val bookingMode: BookingModel = Gson().fromJson(
                                    jsonObject.toString(),
                                    BookingModel::class.java
                                )
                               bookingModel = bookingMode.data[position]
                                setViewData()

                                userDataViewModel.clearBookingData()

                            }
                            else {
                                /* Toast.makeText(
                                     requireActivity(),
                                     "" + jsonObject.getString("message"),
                                     Toast.LENGTH_SHORT
                                 ).show()*/
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
                    Helper.showProgressMessage(this@BookingDetailAct,getString(R.string.please_wait))
                }

                else -> {}
            }
        })

        userDataViewModel.bookingCompleteLiveData.observe(this, Observer {
            Helper.hideProgressMessage()
            when (it) {
                is NetworkResult.Success -> {
                    try {
                        it.data?.let {
                            val jsonObject = JSONObject(it.string())
                            Log.e("booking Complete Response===",jsonObject.toString())
                            if (jsonObject.getInt("status") == 1) {

                                val addReviewBottomSheet = AddReviewBottomSheet
                                    .newInstance(sharedPrf.getStoredTag(SharedPrf.USER_ID),this@BookingDetailAct,bookingModel!!)
                                addReviewBottomSheet.show(supportFragmentManager, "")
                                userDataViewModel.clearBookingCompleteData()


                            }
                            else {
                                Toast.makeText(
                                    this@BookingDetailAct,
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
                    Helper.showProgressMessage(this@BookingDetailAct,getString(R.string.please_wait))
                }

                else -> {}
            }
        })


        userDataViewModel.addReviewResponseLiveData.observe(this, Observer { it ->
            Helper.hideProgressMessage()
            when (it) {
                is NetworkResult.Success -> {
                    try {
                        it.data?.let {
                            val jsonObject = JSONObject(it.string())
                            Log.e("add review on property Response===", jsonObject.toString())
                            if (jsonObject.getString("status") == "1") {
                                Toast.makeText(this,getString(R.string.rate_sucessfully),Toast.LENGTH_LONG).show()
                                val getBookingRequest = getBookingRequest("complete")
                                Log.e("Get Booking Request===", getBookingRequest.toString())
                                Helper.showProgressMessage(this@BookingDetailAct,getString(R.string.please_wait))
                                userDataViewModel.getBooking(getBookingRequest)

                                userDataViewModel.clearAddReviewData()
                            } else {
                                Toast.makeText(
                                    this@BookingDetailAct,
                                    "" + jsonObject.getString("message"),
                                    Toast.LENGTH_SHORT
                                ).show()
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
                    Helper.showProgressMessage(this@BookingDetailAct, getString(R.string.please_wait))
                }

                else -> {}
            }
        })


    }


    @SuppressLint("StringFormatMatches")
    private fun showValidationErrors(error: String) {
        val text = String.format(resources.getString(R.string.txt_error_message, error))
        Toast.makeText(this@BookingDetailAct,text, Toast.LENGTH_SHORT).show()
    }


    private fun getBookingRequest(bookingType:String): Map<String, String> {
        return binding.run {
            mapOf(
                "token" to sharedPrf.getStoredTag(SharedPrf.TOKEN),
                "type" to bookingType
            )

        }
    }


    private fun bookingCompleteRequest(bookingId:String): Map<String, String> {
        return binding.run {
            mapOf(
                "token" to sharedPrf.getStoredTag(SharedPrf.TOKEN),
                "booking_id" to bookingId,
                "checkout_date" to Helper.getCurrent()
            )
        }
    }



    private fun formatDates(dateString: String): List<String> {
        // Define the output date format
        val outputFormatter = DateTimeFormatter.ofPattern("dd MMM yyyy")

        // Split the input string by commas
        val dateList = dateString.split(",")

        // Format each date and store them in a list
        return dateList.map { dateString ->
            // Parse each date string into LocalDate and format it
            val date = LocalDate.parse(dateString)
            date.format(outputFormatter)
        }
    }


    override fun addReview(userId: String, propertyId: String, rating: String, comment: String) {
        val currentDate = LocalDate.now()
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
        val formattedDate = currentDate.format(formatter)
        val addReviewRequest = addReviewRequest(userId, propertyId, rating, comment,formattedDate)
        Log.e("add Review Request Request===", addReviewRequest.toString())
        Helper.showProgressMessage(this@BookingDetailAct,getString(R.string.please_wait))
        userDataViewModel.addReview(addReviewRequest)
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



}