package com.ludian.pushnotification;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.ludian.R;
import com.ludian.ui.home.HomeAct;
import com.ludian.utils.Helper;
import com.ludian.utils.SharedPrf;

import org.json.JSONObject;

import java.util.Map;
import java.util.Random;

public class MyFirebaseMessagingService extends FirebaseMessagingService {
    private static final String TAG = "";
    private static final String CHANNEL_ID = "my_channel_id";
    JSONObject notificationObj;
    String title="",result = "", key = "", message = "", type = "",msg="",keyEng="",lang="";
    SharedPrf sharedPrf;
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        try {
            Log.e("remoteMessage>>", "" + remoteMessage);
             sharedPrf = new SharedPrf(getApplicationContext());
            if (!remoteMessage.getData().isEmpty()) {
                Log.e(TAG, "Message data payload : " + remoteMessage.getData());
                Map<String, String> data = remoteMessage.getData();


                notificationObj = new JSONObject(data.get("data"));
                result = notificationObj.getString("result");


               lang = sharedPrf.getStoredTag(SharedPrf.LANGUAGE);
                Log.e("Language====", lang);
                if(lang.equals("en") || lang.equals("")){
                    key = notificationObj.getString("key");
                    message = notificationObj.getString("message");
                    title = notificationObj.getString("title");
                }
                else {

                    if(notificationObj.has("key_ar") && notificationObj.has("message_ar") && notificationObj.has("title_ar")){
                        key = Helper.Companion.decodeUnicodeToString(notificationObj.getString("key_ar"));
                        message =  Helper.Companion.decodeUnicodeToString(notificationObj.getString("message_ar"));
                        title =  Helper.Companion.decodeUnicodeToString(notificationObj.getString("title_ar"));
                        Log.e("Arabic ka param=====","======"+ Helper.Companion.decodeUnicodeToString(notificationObj.getString("key_ar")));
                    }
                    else{
                        key = notificationObj.getString("key");
                        message = notificationObj.getString("message");
                        title = notificationObj.getString("title");
                        Log.e("Arabic ka param not available=====","======");

                    }

                }

                sendNotification(title
                        , result, message, type, notificationObj,msg,keyEng);
            }





        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void sendNotification(String title, String messageBody, String msg, String type, JSONObject remoteMessage,String msgEng,String keyEng) {
        try {
            Intent intent = null;
            intent = new Intent(getApplicationContext(), HomeAct.class);
            final int not_nu = generateRandom();
            Bitmap largeIcon = BitmapFactory.decodeResource(getResources(), R.drawable.ic_logo);

            long[] vibrationPattern = new long[]{1000, 1000, 1000, 1000};
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            PendingIntent contentIntent = PendingIntent.getActivity(getApplicationContext(), not_nu, intent, PendingIntent.FLAG_ONE_SHOT | PendingIntent.FLAG_IMMUTABLE);
            Uri sound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, CHANNEL_ID)
                    .setSmallIcon(R.drawable.ic_logo)
                    .setLargeIcon(largeIcon)
                    .setContentTitle(title)
                     // .setContentText(msg)
                    .setStyle(new NotificationCompat.BigTextStyle().bigText(msg))
                    .setAutoCancel(true)
                    .setContentIntent(contentIntent)
                    .setVibrate(vibrationPattern)
                    .setPriority(NotificationCompat.PRIORITY_HIGH);

            NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                NotificationChannel channel = new NotificationChannel(CHANNEL_ID, "Channel name", NotificationManager.IMPORTANCE_HIGH);
                notificationManager.createNotificationChannel(channel);
            }

            notificationManager.notify(0, notificationBuilder.build());
        } catch (Exception exception) {
            exception.printStackTrace();
        }


    }


    public int generateRandom() {
        Random random = new Random();
        return random.nextInt(9999 - 1000) + 1000;
    }




}
