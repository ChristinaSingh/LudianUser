package com.ludian.ui.auth

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import com.bumptech.glide.Glide
import com.google.firebase.messaging.FirebaseMessaging
import com.ludian.R
import com.ludian.databinding.ActivitySplashBinding
import com.ludian.ui.home.HomeAct
import com.ludian.utils.SharedPrf
import java.util.Locale

class SplashAct : AppCompatActivity() {
    private lateinit var binding: ActivitySplashBinding
    private val splashTimeOut : Long = 3000 // 3 seconds
    private val sharedPrf by lazy { SharedPrf(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val window = window
        if (window != null) {
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            window.statusBarColor = Color.TRANSPARENT
        }
        binding = DataBindingUtil.setContentView(this, R.layout.activity_splash)

      /*  val crashButton = Button(this)
        crashButton.text = "Test Crash"
        crashButton.setOnClickListener {
            throw RuntimeException("Test Crash") // Force a crash
        }

        addContentView(crashButton, ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT))*/

        Glide.with(this)
            .asGif()  // Specify that you are loading a GIF
            .load(R.drawable.logo_gifff)  // Load the GIF from the URL
            .into(binding.ivImg)


        FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
            if (!task.isSuccessful) {
                Log.w("FCMToken", "Fetching FCM registration token failed", task.exception)
                return@addOnCompleteListener
            }

            // Get the new FCM registration token
           val token = task.result
            Log.d("FCMToken", "FCM registration token: $token")

            // Send the token to your server or handle it as needed
        }

        if (Build.VERSION.SDK_INT >= 33) {
            checkPermission()
        }
        else openToNext()

    }


    private fun checkPermission() {
        if (ContextCompat.checkSelfPermission(
                this@SplashAct,
                Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED

        ) {

            ActivityCompat.requestPermissions(
                this@SplashAct,
                arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                1000
            )
        } else {
            openToNext()
        }
    }

    private fun openToNext() {

        val lang: String = sharedPrf.getStoredTag(SharedPrf.LANGUAGE)
        Log.e("Language====", lang)
        when (lang) {
            "en" -> setLocale("en")
            "ar" -> setLocale("ar")
            else -> setLocale("en")
        }


        Handler(Looper.getMainLooper()).postDelayed({
            if (sharedPrf.getStoredTag(SharedPrf.LOGIN) == "true") {
                Log.e("check user====", sharedPrf.getUser().id.toString())
                startActivity(Intent(this, HomeAct::class.java))
                finish()
            } else {
                startActivity(Intent(this, LoginAct::class.java))
                finish()
            }
        }, splashTimeOut)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == 1000) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, you can proceed with using the camera or accessing the gallery
                openToNext()
            } else {
                Toast.makeText(this@SplashAct, R.string.permission_denied, Toast.LENGTH_LONG)
                    .show()
                // Permission denied, handle accordingly (e.g., display a message, disable certain features)
            }
        }
    }

    private fun setLocale(lang: String) {
        val locale = Locale(lang)
        Locale.setDefault(locale)

        val config = Configuration()
        config.setLocale(locale)
        resources.updateConfiguration(config, resources.displayMetrics)
        sharedPrf.setStoredTag(SharedPrf.LANGUAGE, lang)


    }

}
