package com.ludian.ui.profile

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.ludian.R
import com.ludian.databinding.ActivityNotificationBinding

class NotificationAct : AppCompatActivity() {
    private lateinit var binding : ActivityNotificationBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this,R.layout.activity_notification)
        initViews()
    }

    private fun initViews() {

        binding.ivBack.setOnClickListener {
            finish()
        }
    }

}