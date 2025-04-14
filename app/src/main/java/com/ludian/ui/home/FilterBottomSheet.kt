package com.ludian.ui.home

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.widget.Autocomplete
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.ludian.R
import com.ludian.ui.calender.CalendarAct
import com.ludian.viewmodels.UserDataViewModel
import com.mohammedalaa.seekbar.RangeSeekBarView

class FilterBottomSheet : BottomSheetDialogFragment() {
    private lateinit var bottomSheetView: View
    private lateinit var listener: FilterSelectListener
    private var rating:Int= 1
    private var tvLocation:TextView?=null
    private var ll1:LinearLayout?=null
    private var ll2:LinearLayout?=null
    private var ll3:LinearLayout?=null
    private var ll4:LinearLayout?=null
    private var ll5:LinearLayout?=null

    private var lat:String= ""
    private var lon:String= ""

    companion object {
        private const val PLACE_PICKER_REQUEST_CODE = 1
        fun newInstance(
            listener: FilterSelectListener
        ): FilterBottomSheet {
            val fragment = FilterBottomSheet()
            fragment.listener = listener
            return fragment
        }
    }




    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        bottomSheetView = inflater.inflate(R.layout.fragment_filter, container, false)

        return bottomSheetView


    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val btnApply = bottomSheetView.findViewById<Button>(R.id.btnApply)
        val ivBack = bottomSheetView.findViewById<ImageView>(R.id.ivBack)
        val btnDate = bottomSheetView.findViewById<RelativeLayout>(R.id.btnDate)
        val ivDate = bottomSheetView.findViewById<ImageView>(R.id.ivDate)
        val rangeSeekbar = bottomSheetView.findViewById<RangeSeekBarView>(R.id.range_seekbar11)
         ll1 = bottomSheetView.findViewById<LinearLayout>(R.id.ll1)
         ll2 = bottomSheetView.findViewById<LinearLayout>(R.id.ll2)
         ll3 = bottomSheetView.findViewById<LinearLayout>(R.id.ll3)
         ll4 = bottomSheetView.findViewById<LinearLayout>(R.id.ll4)
         ll5 = bottomSheetView.findViewById<LinearLayout>(R.id.ll5)



        val rlLocation = bottomSheetView.findViewById<RelativeLayout>(R.id.rlLocation)
         tvLocation = bottomSheetView.findViewById<TextView>(R.id.tvLocation)



        rangeSeekbar.minValue= 0
        rangeSeekbar.maxValue= 10000

        rangeSeekbar.currentValue


        ivBack.setOnClickListener {
            dismiss()
        }

        // Initialize Places API
        if (!Places.isInitialized()) {
            Places.initialize(requireActivity(), getString(R.string.location_api))
        }

        rlLocation.setOnClickListener {
            checkPermission()
        }



        btnApply.setOnClickListener {
            dismiss()
            listener.onFilter(rating.toString(),lat,lon,rangeSeekbar.currentValue.toString())

        }

        btnDate.setOnClickListener {
            startActivity(Intent(requireActivity(), CalendarAct::class.java))
        }

        ivDate.setOnClickListener {
            startActivity(Intent(requireActivity(), CalendarAct::class.java))

        }


        ll1!!.setOnClickListener {
            rating = 1
            rateSelection(rating)
        }

        ll2!!.setOnClickListener {
            rating = 2
            rateSelection(rating)
        }

        ll3!!.setOnClickListener {
            rating = 3
            rateSelection(rating)
        }

        ll4!!.setOnClickListener {
            rating = 4
            rateSelection(rating)
        }

        ll5!!.setOnClickListener {
            rating = 5
            rateSelection(rating)
        }






        ivBack.setOnClickListener {
            dialog!!.dismiss()
        }

        rateSelection(rating)

    }

    private fun rateSelection(rating: Int) {

        when (rating) {
            1 -> {
                ll1!!.setBackgroundResource(R.drawable.rating_selection_bg)
                ll2!!.setBackgroundResource(R.drawable.rounded_gray_btn_bg)
                ll3!!.setBackgroundResource(R.drawable.rounded_gray_btn_bg)
                ll4!!.setBackgroundResource(R.drawable.rounded_gray_btn_bg)
                ll5!!.setBackgroundResource(R.drawable.rounded_gray_btn_bg)

            }
            2 -> {
                ll1!!.setBackgroundResource(R.drawable.rounded_gray_btn_bg)
                ll2!!.setBackgroundResource(R.drawable.rating_selection_bg)
                ll3!!.setBackgroundResource(R.drawable.rounded_gray_btn_bg)
                ll4!!.setBackgroundResource(R.drawable.rounded_gray_btn_bg)
                ll5!!.setBackgroundResource(R.drawable.rounded_gray_btn_bg)
            }
            3 -> {
                ll1!!.setBackgroundResource(R.drawable.rounded_gray_btn_bg)
                ll2!!.setBackgroundResource(R.drawable.rounded_gray_btn_bg)
                ll3!!.setBackgroundResource(R.drawable.rating_selection_bg)
                ll4!!.setBackgroundResource(R.drawable.rounded_gray_btn_bg)
                ll5!!.setBackgroundResource(R.drawable.rounded_gray_btn_bg)
            }
            4 -> {
                ll1!!.setBackgroundResource(R.drawable.rounded_gray_btn_bg)
                ll2!!.setBackgroundResource(R.drawable.rounded_gray_btn_bg)
                ll3!!.setBackgroundResource(R.drawable.rounded_gray_btn_bg)
                ll4!!.setBackgroundResource(R.drawable.rating_selection_bg)
                ll5!!.setBackgroundResource(R.drawable.rounded_gray_btn_bg)
            }
            5 -> {
                ll1!!.setBackgroundResource(R.drawable.rounded_gray_btn_bg)
                ll2!!.setBackgroundResource(R.drawable.rounded_gray_btn_bg)
                ll3!!.setBackgroundResource(R.drawable.rounded_gray_btn_bg)
                ll4!!.setBackgroundResource(R.drawable.rounded_gray_btn_bg)
                ll5!!.setBackgroundResource(R.drawable.rating_selection_bg)
            }
        }

    }

    interface FilterSelectListener {
            fun onFilter(rating:String,lat:String,lon:String,priceRange:String)
        }


    private fun openPlacePicker() {
        // Create a new intent for the Autocomplete activity
        val fields = listOf(Place.Field.ID, Place.Field.NAME, Place.Field.ADDRESS, Place.Field.LAT_LNG)
        val intent = Autocomplete.IntentBuilder(AutocompleteActivityMode.FULLSCREEN, fields)
            .build(requireActivity())
        startActivityForResult(intent, PLACE_PICKER_REQUEST_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PLACE_PICKER_REQUEST_CODE) {
            val place = Autocomplete.getPlaceFromIntent(data!!)
            // Update TextView with the selected place's details
            tvLocation!!.text = place.name
            lat = place.latLng.latitude.toString()
            lon = place.latLng.longitude.toString()

            // Optionally, you can also get other details like address, lat/lng
            // val address = place.address
            // val latLng = place.latLng
        }
    }


    private fun checkPermission() {
        if (ContextCompat.checkSelfPermission(
                requireActivity(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
            && ContextCompat.checkSelfPermission(
                requireActivity(),
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {

            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION),
                1000
            )
        } else {
            openPlacePicker()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == 1000) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, you can proceed with using the camera or accessing the gallery
                openPlacePicker()
            } else {
                Toast.makeText(requireActivity(), R.string.permission_denied, Toast.LENGTH_LONG)
                    .show()
                // Permission denied, handle accordingly (e.g., display a message, disable certain features)
            }
        }
    }




}