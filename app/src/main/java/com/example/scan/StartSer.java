package com.example.scan;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.util.Log;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class StartSer  extends BroadcastReceiver {
    public StartSer() {
    }
    int login_id = -1;//логин пользователя
    SharedPreferences sPref;// файл с настройками
    final int REQUEST_CODE_ACCESS_FINE_LOCATION = 200; // код ддя проверки разрешения на геолокаццию
    final int REQUEST_WRITE_EXTERNAL_STORAGE = 300; // код ддя проверки разрешения на запись
    final int REQUEST_READ_EXTERNAL_STORAGE = 400; // код ддя проверки разрешения на запись файла
    final int REQUEST_ACCESS_COARSE_LOCATION = 500;
    final String LOG_TAG = "myLogs";

    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)) {
            // ваш код здесь
            Log.d(LOG_TAG, "kuk");
            Log.d(LOG_TAG, "k213123123uk");

            try{
              Intent stopIntent = new Intent(context, ServiceGps.class);
              //stopIntent.setAction("START");
              intent.setPackage(context.getPackageName());
              context.startService(stopIntent);

            } catch (Exception e) {
                Log.d(LOG_TAG,"Exp=" + e);

            }
            Log.d(LOG_TAG, "kuk2");
        }
    }

}