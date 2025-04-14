package com.ludian.ui.booking

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.ludian.R
import com.ludian.databinding.ActivityCheckGuestBinding
import com.ludian.models.PropertyModel

class CheckGuestAct : AppCompatActivity() {
    private lateinit var binding : ActivityCheckGuestBinding
    private var count: Int = 1
    private var childCount: Int = 1
    private var propertyModel : PropertyModel.Property? =null
    private var startDate: String?=null
    private var endDate: String?=null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_check_guest)
        initViews()
    }

    private fun initViews() {

        propertyModel = intent.getSerializableExtra("propertyData") as PropertyModel.Property?
        startDate = intent?.getStringExtra("startDate")
        endDate = intent?.getStringExtra("endDate")



        binding.ivBack.setOnClickListener {
            finish()
        }

        binding.btnApply.setOnClickListener {
            startActivity(Intent(this@CheckGuestAct,BookingPaymentAct::class.java)
                .putExtra("propertyData",propertyModel)
                .putExtra("startDate",startDate)
                .putExtra("endDate",endDate)
                .putExtra("guestCount",count.toString())
                .putExtra("childCount",childCount.toString()))        }



        binding.ivPlus.setOnClickListener {
            count++
            Log.e("count value===",count.toString())
            binding.tvCount.text = count.toString()
        }


        binding.ivMinus.setOnClickListener {
            if(count>0) {
                count--
                binding.tvCount.text = count.toString()

            }

        }


        binding.ivChildPlus.setOnClickListener {
            childCount++
            Log.e("count value===",childCount.toString())
            binding.tvChildCount.text = childCount.toString()
        }


        binding.ivChildMinus.setOnClickListener {
            if(childCount>0) {
                childCount--
                binding.tvChildCount.text = childCount.toString()

            }

        }



    }

}
