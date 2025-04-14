package com.ludian.ui.booking

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.ludian.R
import com.ludian.databinding.ActivityBookingCompleteBinding
import com.ludian.ui.home.HomeAct

class BookingCompleteAct : AppCompatActivity() {
    private lateinit var binding : ActivityBookingCompleteBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_booking_complete)
        initViews()
    }

    private fun initViews() {

        binding.btnHome.setOnClickListener {
            startActivity(Intent(this@BookingCompleteAct, HomeAct::class.java))
            finish()
        }
    }

}
