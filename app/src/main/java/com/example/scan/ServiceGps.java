package com.example.scan;

import android.Manifest;
import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;

import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;
import android.content.Context;
import android.Manifest;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Handler;

import android.util.Log;

import androidx.core.content.ContextCompat;

import org.apache.http.NameValuePair;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class ServiceGps extends Service {

    public static final int TWO_MINUTES = 120000; // 120 seconds
    public static Boolean isRunning = false;
    public static final String CHANNEL_ID = "ForegroundServiceChannel";

    //работа с инетом
    private int off=0;
    private String url = "http://www.zaural-vodokanal.ru/php/get_pos.php"; // отправка локации
    private SendOnePackage sender; // класс единичной отправки пакета

    //настройки на телефоне
    private int login_id = -1;//айдишник пользователя
    SharedPreferences sPref;// файл с настройками

    //отладка
    final String LOG_TAG = "myLogs";//тег консоли

    //работа с gps
    private LocationManager locationManager;// слушатель
    Context Ctn = this;// текущий контент, нужен для получения списка разрешений
    private Location lastPos;

    Handler mHandler = new Handler();

    public void onCreate() {
        super.onCreate();
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        Log.d(LOG_TAG, "StartServics");
        int permissionStatus = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);

        if (permissionStatus == PackageManager.PERMISSION_GRANTED) {
            int permissonStatus =1;
            float kakogo = 5.5F;
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,5000 * 1,5.5F, locationListener);
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                    5000 * 1,5.5F, locationListener);

        }

    }

    private int loadSaveId(){
        sPref = getSharedPreferences("prefs",MODE_PRIVATE);
        login_id = sPref.getInt("login_id", -1);
        if (login_id == -1) return 0;
        else return 1;

    }

    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(LOG_TAG, "tut");

        if(loadSaveId()==1) {
            NotificationManager notificationManager =
                    (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                NotificationChannel channel = new NotificationChannel(CHANNEL_ID, "My channel",
                        NotificationManager.IMPORTANCE_HIGH);
                channel.setDescription("My channel description");
                channel.enableLights(true);
                channel.setLightColor(Color.RED);
                channel.enableVibration(false);
                notificationManager.createNotificationChannel(channel);
            }

            Intent notificationIntent = new Intent(this, MainActivity.class);

            PendingIntent pendingIntent = PendingIntent.getActivity(this, 0,
                    notificationIntent, 0);

            Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                    .setContentTitle("Я лисичка")
                    .setContentText("Фыр фыр фыр")
                    .setSmallIcon(R.drawable.ic_launcher_background)
                    .setContentIntent(pendingIntent)
                    .build();

            startForeground(1, notification);

        }

        return super.onStartCommand(intent, flags, startId);
    }

    private void stopCommand(){
        stopForeground(true);
        stopSelf();
    }

    public void onDestroy() {
        super.onDestroy();
        Log.d(LOG_TAG, "onDestroy");
    }

    public IBinder onBind(Intent intent) {
        Log.d(LOG_TAG, "onBind");
        return null;
    }

    //класс слушателя гпс, реагирующего на изменения геоданных
    private LocationListener locationListener = new LocationListener() {

        @Override
        public void onLocationChanged(Location loc) {
            showLocation(loc);
        }

        @Override
        public void onProviderDisabled(String provider) {
            checkEnabled();
        }

        @Override
        public void onProviderEnabled(String provider) {
            checkEnabled();
            int permissionStatus = ContextCompat.checkSelfPermission(Ctn, Manifest.permission.ACCESS_FINE_LOCATION);
            showLocation(locationManager.getLastKnownLocation(provider));
 //           lastPos = locationManager.getLastKnownLocation(provider);
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
            Log.d(LOG_TAG, "off");
            if (provider.equals(LocationManager.GPS_PROVIDER)) {
                //MainText.setText("Status: " + String.valueOf(status));
            } else if (provider.equals(LocationManager.NETWORK_PROVIDER)) {
                //   MainText.setText("Status: " + String.valueOf(status));
            }
        }
    };

    private void showLocation(Location location) {
        if (location == null)
            return;
        lastPos = location;
        if (location.getProvider().equals(LocationManager.GPS_PROVIDER)) {
            Log.d(LOG_TAG,formatLocation(location));

        } else if (location.getProvider().equals(
                LocationManager.NETWORK_PROVIDER)) {
            Log.d(LOG_TAG,formatLocation(location));
        }
    }

    private String formatLocation(Location location) {
        if (location == null)
            return "";
        System.out.println("----------------------------");
        String lat = String.format(Locale.ENGLISH,"%1$.4f",location.getLatitude());
        String lon = String.format(Locale.ENGLISH,"%1$.4f",location.getLongitude());
        String date = String.format(String.format("%1$tF %1$tT",location.getTime()));
        String provider="23";
        if (location.getProvider().equals(LocationManager.GPS_PROVIDER)) provider = "GPS";
        else if (location.getProvider().equals(LocationManager.NETWORK_PROVIDER)) provider = "NETWORK";
        else provider = "OTHER";
        sender = new SendOnePackage();
        sender.execute(lat,lon,date,provider);
        System.out.println(String.format("Coordinates: lat = %1$.4f, lon = %2$.4f, time = %3$tF %3$tT",
                location.getLatitude(), location.getLongitude(), new Date(
                        location.getTime())));
        return String.format(
                "Coordinates: lat = %1$.4f, lon = %2$.4f, time = %3$tF %3$tT",
                location.getLatitude(), location.getLongitude(), new Date(
                        location.getTime()));
    }

    private void checkEnabled() {

    }

    //асинхронный класс, для отправки единичного пакета
    class SendOnePackage extends AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(String... params) {

            try {
                //создаем запрос на сервер
                DefaultHttpClient hc = new DefaultHttpClient();
                ResponseHandler<String> res = new BasicResponseHandler();
                //он у нас будет посылать post запрос
                HttpPost postMethod = new HttpPost(url);
                //будем передавать два параметра
                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(1);
                //передаем параметры из наших текстбоксов
                //лоигн
                nameValuePairs.add(new BasicNameValuePair("login_id",Integer.toString(login_id)));
                nameValuePairs.add(new BasicNameValuePair("lat",params[0]));
                nameValuePairs.add(new BasicNameValuePair("lon",params[1]));
                nameValuePairs.add(new BasicNameValuePair("date",params[2]));
                nameValuePairs.add(new BasicNameValuePair("provider",params[3]));
                //пароль
                //собераем их вместе и посылаем на сервер
                postMethod.setEntity(new UrlEncodedFormEntity(nameValuePairs));
                //получаем ответ от сервера
                String response = hc.execute(postMethod, res);
                Log.d(LOG_TAG,response);
                if(response!="1");
                //посылаем на вторую активность полученные параметры

            } catch (Exception e) {
                System.out.println("Exp=" + e);
            }
            return null;
        }
    }

    //асинхронный класс, для отправки единичного пакета

}
