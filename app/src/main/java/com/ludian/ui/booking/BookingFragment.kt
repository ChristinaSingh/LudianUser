package com.ludian.ui.booking

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.google.gson.Gson
import com.ludian.R
import com.ludian.databinding.FragmentBookingBinding
import com.ludian.models.BookingModel
import com.ludian.ui.booking.adapter.BookingCancelAdapter
import com.ludian.ui.booking.adapter.BookingCompleteAdapter
import com.ludian.ui.booking.adapter.BookingInProgressAdapter
import com.ludian.viewmodels.UserDataViewModel
import com.ludian.utils.Helper
import com.ludian.utils.NetworkResult
import com.ludian.utils.SharedPrf
import dagger.hilt.android.AndroidEntryPoint
import org.json.JSONObject
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@AndroidEntryPoint
class BookingFragment :  Fragment(), BookingInProgressAdapter.OnBookingCompleteListener,AddReviewBottomSheet.AddReviewListener {

    private var _binding: FragmentBookingBinding? = null
    private val binding get() = _binding!!
    private lateinit var userDataViewModel: UserDataViewModel

    private lateinit var bookingInProgressAdapter: BookingInProgressAdapter
    private lateinit var bookingCancelAdapter: BookingCancelAdapter
    private lateinit var bookingCompleteAdapter: BookingCompleteAdapter
    private lateinit var bookingData: BookingModel.Data

    private var arrayList : ArrayList<BookingModel.Data>?=null
    private var tabSelection : Int =1
    private val sharedPrf by lazy { SharedPrf(requireActivity()) }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentBookingBinding.inflate(inflater, container, false)
        userDataViewModel = ViewModelProvider(this).get(UserDataViewModel::class.java)

        initViews()
        return binding.root
    }

    private fun initViews() {

        arrayList = ArrayList()


        binding.llUpcoming.setOnClickListener {
            tabSelection =1
            tabBarChange(tabSelection)
        }
        binding.llPrevious.setOnClickListener {
            tabSelection =2
            tabBarChange(tabSelection)
        }

        binding.llCancelled.setOnClickListener {
            tabSelection =3
            tabBarChange(tabSelection)
        }


        tabBarChange(tabSelection)

        bindObserver()

    }

    private fun tabBarChange(i : Int){
        when (i) {
            1 -> {
                binding.view1.visibility =View.VISIBLE
                binding.view2.visibility =View.GONE
                binding.view3.visibility =View.GONE

                val getBookingRequest = getBookingRequest("upcoming")
                Log.e("Get Booking Request===", getBookingRequest.toString())
                Helper.showProgressMessage(requireActivity(),getString(R.string.please_wait))
                userDataViewModel.getBooking(getBookingRequest)

            }
            2 -> {
                binding.view1.visibility =View.GONE
                binding.view2.visibility =View.VISIBLE
                binding.view3.visibility =View.GONE

                val getBookingRequest = getBookingRequest("complete")
                Log.e("Get Booking Request===", getBookingRequest.toString())
                Helper.showProgressMessage(requireActivity(),getString(R.string.please_wait))
                userDataViewModel.getBooking(getBookingRequest)

            }
            else -> {
                binding.view1.visibility =View.GONE
                binding.view2.visibility =View.GONE
                binding.view3.visibility =View.VISIBLE

                val getBookingRequest = getBookingRequest("cancel")
                Log.e("Get Booking Request===", getBookingRequest.toString())
                Helper.showProgressMessage(requireActivity(),getString(R.string.please_wait))
                userDataViewModel.getBooking(getBookingRequest)
            }
        }
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



    private fun bindObserver(){
        userDataViewModel.propertyBookingLiveData.observe(viewLifecycleOwner, Observer {
            Helper.hideProgressMessage()
            when (it) {
                is NetworkResult.Success -> {
                    try {
                        it.data?.let {
                            val jsonObject = JSONObject(it.string())
                            Log.e("get booking Response===",jsonObject.toString())
                            if (jsonObject.getInt("status") == 1) {

                                val bookingModel: BookingModel = Gson().fromJson(
                                    jsonObject.toString(),
                                    BookingModel::class.java
                                )
                                arrayList!!.clear()
                                arrayList!!.addAll(bookingModel.data)
                                binding.tvNotFound.visibility =View.GONE


                                when (tabSelection) {
                                    1 -> {
                                        bookingInProgressAdapter = BookingInProgressAdapter(requireActivity(),arrayList,this@BookingFragment)
                                        binding.rvBooking.adapter = bookingInProgressAdapter
                                        bookingInProgressAdapter.notifyAdapter(arrayList!!)
                                    }
                                    2 -> {
                                        bookingCompleteAdapter = BookingCompleteAdapter(requireActivity(),arrayList)
                                        binding.rvBooking.adapter = bookingCompleteAdapter
                                        bookingCompleteAdapter.notifyAdapter(arrayList!!)
                                    }
                                    3 -> {
                                        bookingCancelAdapter = BookingCancelAdapter(requireActivity(),arrayList)
                                        binding.rvBooking.adapter = bookingCancelAdapter
                                        bookingCancelAdapter.notifyAdapter(arrayList!!)
                                    }
                                }

                                userDataViewModel.clearBookingData()

                            }
                            else {

                                when (tabSelection) {
                                    1 -> {
                                        arrayList!!.clear()
                                        bookingInProgressAdapter = BookingInProgressAdapter(requireActivity(),arrayList,this@BookingFragment)
                                        binding.rvBooking.adapter = bookingInProgressAdapter
                                        bookingInProgressAdapter.notifyAdapter(arrayList!!)
                                    }
                                    2 -> {
                                        arrayList!!.clear()
                                        bookingCompleteAdapter = BookingCompleteAdapter(requireActivity(),arrayList)
                                        binding.rvBooking.adapter = bookingCompleteAdapter
                                        bookingCompleteAdapter.notifyAdapter(arrayList!!)
                                    }
                                    3 -> {
                                        arrayList!!.clear()
                                        bookingCancelAdapter = BookingCancelAdapter(requireActivity(),arrayList)
                                        binding.rvBooking.adapter = bookingCancelAdapter
                                        bookingCancelAdapter.notifyAdapter(arrayList!!)
                                    }
                                }
                                binding.tvNotFound.visibility =View.VISIBLE
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
                    Helper.showProgressMessage(requireActivity(),getString(R.string.please_wait))
                }

                else -> {}
            }
        })

        userDataViewModel.bookingCompleteLiveData.observe(viewLifecycleOwner, Observer {
            Helper.hideProgressMessage()
            when (it) {
                is NetworkResult.Success -> {
                    try {
                        it.data?.let {
                            val jsonObject = JSONObject(it.string())
                            Log.e("booking Complete Response===",jsonObject.toString())
                            if (jsonObject.getInt("status") == 1) {

                                tabBarChange(tabSelection)
                                val addReviewBottomSheet = AddReviewBottomSheet
                                    .newInstance(sharedPrf.getStoredTag(SharedPrf.USER_ID),this@BookingFragment,bookingData)
                                addReviewBottomSheet.show(childFragmentManager, "")
                                userDataViewModel.clearBookingCompleteData()

                            }
                            else {
                                Toast.makeText(
                                    requireActivity(),
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
                    Helper.showProgressMessage(requireActivity(),getString(R.string.please_wait))
                }

                else -> {}
            }
        })


        userDataViewModel.addReviewResponseLiveData.observe(viewLifecycleOwner, Observer { it ->
            Helper.hideProgressMessage()
            when (it) {
                is NetworkResult.Success -> {
                    try {
                        it.data?.let {
                            val jsonObject = JSONObject(it.string())
                            Log.e("add review on property Response===", jsonObject.toString())
                            if (jsonObject.getString("status") == "1") {
                                tabBarChange(tabSelection)
                                Toast.makeText(requireActivity(),getString(R.string.rate_sucessfully),Toast.LENGTH_LONG).show()
                                userDataViewModel.clearAddReviewData()
                            } else {
                                 Toast.makeText(
                                     requireActivity(),
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
                    Helper.showProgressMessage(requireActivity(), getString(R.string.please_wait))
                }

                else -> {}
            }
        })


    }


    @SuppressLint("StringFormatMatches")
    private fun showValidationErrors(error: String) {
        val text = String.format(resources.getString(R.string.txt_error_message, error))
        Toast.makeText(requireActivity(),text, Toast.LENGTH_SHORT).show()
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onComplete(model: BookingModel.Data, position: Int, type: String) {
        bookingData= model
        val bookingCompleteRequest = bookingCompleteRequest(model.booking_request_id)
        Log.e("Booking Complete Request===", bookingCompleteRequest.toString())
        Helper.showProgressMessage(requireActivity(),getString(R.string.please_wait))
        userDataViewModel.bookingComplete(bookingCompleteRequest)
    }

    override fun addReview(userId: String, propertyId: String, rating: String, comment: String) {
        val currentDate = LocalDate.now()
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
        val formattedDate = currentDate.format(formatter)
        val addReviewRequest = addReviewRequest(userId, propertyId, rating, comment,formattedDate)
        Log.e("add Review Request Request===", addReviewRequest.toString())
        Helper.showProgressMessage(requireActivity(),getString(R.string.please_wait))
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