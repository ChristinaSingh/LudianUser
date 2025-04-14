package com.ludian.network

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class NetworkChangeReceiver(private val onNetworkChange: (Boolean) -> Unit) : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val networkMonitor = NetworkMonitor(context)
        onNetworkChange(networkMonitor.isNetworkAvailable())
    }
}