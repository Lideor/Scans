package com.example.scan;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;

import static android.content.Context.NOTIFICATION_SERVICE;
import static androidx.core.app.NotificationCompat.PRIORITY_HIGH;
import static com.example.scan.MainActivity.STARTFOREGROUND_ACTION;
import static com.example.scan.ServiceGps.CHANNEL_ID;

public class StartSer  extends BroadcastReceiver {
    private static final int NOTIFY_ID = 100 ;

    public StartSer() {
    }
    int login_id = -1;//логин пользователя

    final String LOG_TAG = "myLogs";
    public static String NOTIFICATION_ID = "notification-id" ;
    public static String NOTIFICATION = "notification" ;
    public static final String NOTIFICATION_CHANNEL_ID = "10001" ;

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void onReceive(Context context, Intent intent) {

        String action = intent.getAction();
        String action2 = intent.getStringExtra("Restart");
        Log.d(LOG_TAG, "FFFFFFFF");
        Toast.makeText(context, "Тута", Toast.LENGTH_LONG).show();
;
        String message = "BootDeviceReceiver onReceive, action is " + action;

            // ваш код здесь
            Log.d(LOG_TAG, "kuk");
            Log.d(LOG_TAG, "k213123123uk");
            try{


     //       try{
              Intent stopIntent = new Intent(context, ServiceGps.class);
              //stopIntent.setAction("START");
              intent.setPackage(context.getPackageName());
              stopIntent.setAction(STARTFOREGROUND_ACTION);

              context.startForegroundService(stopIntent);

            } catch (Exception e) {
                Log.d(LOG_TAG,"Exp=" + e);
                message = "Exp=" + e;

                Toast.makeText(context, message, Toast.LENGTH_LONG).show();
            }
            Log.d(LOG_TAG, "kuk2");

    }

}