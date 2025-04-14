package com.ludian.ui.booking

import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import android.widget.Toast
import androidx.viewpager2.widget.ViewPager2
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.ludian.R
import com.ludian.models.BookingModel
import com.ludian.models.PropertyModel
import com.ludian.ui.booking.adapter.ImageSliderAdapter
import com.tbuonomo.viewpagerdotsindicator.DotsIndicator
import de.hdodenhof.circleimageview.CircleImageView
import java.util.Timer
import java.util.TimerTask

class AddReviewBottomSheet : BottomSheetDialogFragment() {
    private lateinit var bottomSheetView: View
    private var userId: String? = null
    private lateinit var listener: AddReviewListener

    private var bookingData : BookingModel.Data? =null
    private var imageArrayList : ArrayList<String> = ArrayList()
    private var viewPager:ViewPager2?=null

    private var currentPage = 0
    private val DELAY_MS: Long = 3000 // Delay in milliseconds before auto sliding starts
    private val PERIOD_MS: Long = 3000 // Period in milliseconds between each slide

    private val handler = Handler()
    private val timer = Timer()


    companion object {
        fun newInstance(
            userID: String,
            listener: AddReviewListener,
            bookingData : BookingModel.Data
        ): AddReviewBottomSheet {
            val fragment = AddReviewBottomSheet()
            fragment.userId = userID
            fragment.listener = listener
            fragment.bookingData = bookingData

            return fragment
        }
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        bottomSheetView = inflater.inflate(R.layout.bottomsheet_add_review, container, false)

        return bottomSheetView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val ivBack = bottomSheetView.findViewById<ImageView>(R.id.ivClose)
        viewPager = bottomSheetView.findViewById<ViewPager2>(R.id.viewPager)
        val dotsIndicator = bottomSheetView.findViewById<DotsIndicator>(R.id.dots_indicator)

        val tvName = bottomSheetView.findViewById<TextView>(R.id.tvName)
        val ivProvider = bottomSheetView.findViewById<CircleImageView>(R.id.ivProvider)

        val tvAddress = bottomSheetView.findViewById<TextView>(R.id.tvAddress)
        val ratingBar = bottomSheetView.findViewById<RatingBar>(R.id.ratingBar)
        val edReview = bottomSheetView.findViewById<EditText>(R.id.edReview)
        val btnSubmit = bottomSheetView.findViewById<Button>(R.id.btnSubmit)

     //   imageArrayList.clear()
     //   imageArrayList.addAll(propertyModel!!.image_urls)

     //   viewPager!!.adapter = ImageSliderAdapter(requireActivity(),imageArrayList)
    //    dotsIndicator.setViewPager2(viewPager!!)
   //     startAutoSlide()
        tvName.text = bookingData!!.property.user_name
       // tvAddress.text = propertyModel!!.address



        Glide.with(requireActivity())
            .load(bookingData!!.property.image)
            .diskCacheStrategy(DiskCacheStrategy.ALL)
            .placeholder(R.drawable.user_default)
            .error(R.drawable.user_default)
            .centerInside()
            .into(ivProvider)


        ivBack.setOnClickListener {
            dialog!!.dismiss()
        }


      btnSubmit.setOnClickListener {
          if(edReview.text.toString()=="")
              Toast.makeText(requireActivity(),getString(R.string.please_add_comment),Toast.LENGTH_LONG).show()
          else {
              dialog!!.dismiss()
              listener.addReview(userId!!,bookingData!!.property.property_id,ratingBar.rating.toString(),edReview.text.toString())
          }
      }



    }

/*
    private fun startAutoSlide() {
        val update = Runnable {
            if (currentPage == propertyModel!!.image_urls.size) {
                currentPage = 0
            }
            viewPager!!.setCurrentItem(currentPage++, true)
        }

        timer.schedule(object : TimerTask() {
            override fun run() {
                handler.post(update)
            }
        }, DELAY_MS, PERIOD_MS)
    }
*/





    interface AddReviewListener {
        fun addReview(userId:String,propertyId:String,rating:String,comment:String)
    }

}