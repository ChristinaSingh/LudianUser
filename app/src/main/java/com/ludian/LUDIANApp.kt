package com.ludian

import android.app.Activity
import android.app.Application
import android.content.Context
import android.content.IntentFilter
import android.net.ConnectivityManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import com.ludian.network.NetworkChangeReceiver
import com.ludian.utils.Helper.Companion.showSnackbar
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class LUDIANApp : Application() {
    private lateinit var networkReceiver: NetworkChangeReceiver

    override fun onCreate() {
        super.onCreate()
      /*  networkReceiver = NetworkChangeReceiver { isConnected ->
            // Handle network status change
          // val network = if(isConnected) "Network is available" else "Network not found."
         //   Log.e("Check Network State...",network)

            val rootView = (getSystemService(Activity.LAYOUT_INFLATER_SERVICE) as LayoutInflater)
                .inflate(R.layout.activity_home, null) // Replace with your layout resource
                .findViewById<View>(R.id.container) // Replace with your root view ID

            val message = if (isConnected) "Network Available" else "No Network Connection"
            showSnackbar(rootView, message, !isConnected)
        }
        val filter = IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION)
        registerReceiver(networkReceiver, filter)*/
    }


/*
    override fun onTerminate() {
        super.onTerminate()
        unregisterReceiver(networkReceiver)
    }
*/
}