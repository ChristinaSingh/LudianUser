package com.ludian.pushnotification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.ludian.R
import com.ludian.ui.home.HomeAct
import org.json.JSONObject

class MyFirebaseMessagingService2 : FirebaseMessagingService() {
    var notificationObj: JSONObject? = null
    var result: String = ""
    var key:String? = ""
    var message:String? = ""
    var type:String? = ""
    override fun onNewToken(token: String) {
        super.onNewToken(token)
        // Log or send the token to your server
        Log.d("FCMToken", "New token: $token")
        // You can also send the token to your server here
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)
        // Handle the received message
        Log.d("FCMMessage", "Message received: ${remoteMessage.data}")


        if (remoteMessage.data.isNotEmpty()) {
            Log.e("", "Message data payload : " + remoteMessage.data)
            Log.d("FCMMessage", "Message received: ${remoteMessage.data}")

            val data = remoteMessage.data


            notificationObj = JSONObject(data["data"])
            result = notificationObj!!.getString("result")
            key = notificationObj!!.getString("key")
          //  type = notificationObj!!.getString("type")
            message = notificationObj!!.getString("message")
            Log.d("FCMService", "data====: " + notificationObj.toString())


            // Process the data as needed
            Log.d("FCMService", "Result: $result")
            Log.d("FCMService", "Key: $key")
            Log.d("FCMService", "Message: $message")
         //   Log.d("FCMService", "Type: $type")
            // You can extract and use data or send a notification here
            sendNotification(message!!)
        }
    }

    private fun sendNotification(messageBody: String) {
        val channelId = "default_channel_id"
        val notificationId = 1

        val intent = Intent(this, HomeAct::class.java)
        val pendingIntent = PendingIntent.getActivity(
            this, 0, intent,
            PendingIntent.FLAG_ONE_SHOT or PendingIntent.FLAG_IMMUTABLE
        )
        val sound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)


        val notificationBuilder = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.ic_logo) // Replace with your notification icon
            .setContentTitle("New Message")
            //.setContentText(messageBody)
            .setStyle( NotificationCompat.BigTextStyle().bigText(messageBody))
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .setSound(sound)
            .setVibrate(longArrayOf(0, 500, 1000))
            .setPriority(NotificationCompat.PRIORITY_HIGH)



        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Default Channel",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION), null)
                enableVibration(true)
            }
            notificationManager.createNotificationChannel(channel)
        }


        notificationManager.notify(notificationId, notificationBuilder.build())
    }


}