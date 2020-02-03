package com.example.scan;

import android.Manifest;
import android.app.Activity;
import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
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
    private int fl=0;
    private String urlOne = "http://www.zaural-vodokanal.ru/php/get_pos.php"; // отправка одного пакета локации
    private String urlRem = "http://www.zaural-vodokanal.ru/php/get_pos_rem.php"; // отправка одного пакета локации

    private static final int NOTIFY_ID = 101;

    private SendOnePackage sender;// класс единичной отправки пакета
    //private SendRemPackage senderRem; // класс отправки остаточных пакета

    //настройки на телефоне
    private int login_id = -1;//айдишник пользователя
    SharedPreferences sPref;// файл с настройками

    //отладка
    final String LOG_TAG = "myLogs";//тег консоли

    //работа с gps
    private LocationManager locationManager;// слушатель
    Context Ctn = this;// текущий контент, нужен для получения списка разрешений
    private Location lastPos;

    //сохранение пакетов если отключен интернет
    private List<SaveOnePackage> allPackage;
    private JsonParse parseJson;

    Handler mHandler = new Handler();

    //уууу сука
    public static String STARTFOREGROUND_ACTION = "Stop";

    public void onCreate() {
        super.onCreate();

        Log.d(LOG_TAG, "StartServics");

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        int permissionStatus = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);
        if (permissionStatus == PackageManager.PERMISSION_GRANTED) {
            int permissonStatus =1;
            float kakogo = 5.5F;
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,5000 * 1,0, locationListener);
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,5000 * 1,5.5F, locationListener);

        }

        allPackage = new ArrayList<>();
        parseJson = new JsonParse();
    }

    //получепния айди пользователя
    private int loadSaveId(){
        sPref = getSharedPreferences("prefs",MODE_PRIVATE);
        login_id = sPref.getInt("login_id", -1);
        if (login_id == -1) return 0;
        else return 1;

    }

    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(LOG_TAG, "tut1");

        if(fl!=1) {
            Log.d(LOG_TAG, "tut");
            if (loadSaveId() == 1) {
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

                Intent notificationIntent = new Intent(ServiceGps.this, ServiceGps.class);

                PendingIntent pendingIntent = PendingIntent.getActivity(ServiceGps.this, 0,
                        notificationIntent, 0);

                PendingIntent closeIntent = PendingIntent.getActivity(ServiceGps.this, 0,
                        notificationIntent, PendingIntent.FLAG_CANCEL_CURRENT);


                Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                        .setContentTitle("Я лисичка")
                        .setContentText("Фыр фыр фыр")
                        .setSmallIcon(R.drawable.ic_launcher_background)
                        .setContentIntent(pendingIntent)
                        .setOngoing(true)
                        .addAction(R.drawable.ic_launcher_background, "Другой вариант", closeIntent)
                        .build();

                startForeground(1, notification);

            }
            fl=1;
        }
        try {
            if(intent.getAction().equals(STARTFOREGROUND_ACTION)){
                Log.d(LOG_TAG, "tut2");
                stopCommand();
            }
        }

        catch (Exception e) {
            Log.d(LOG_TAG,"Exp=" + e);
        }
        return START_NOT_STICKY;
    }

    private void stopCommand(){
        stopForeground(true);
        stopSelf();
    }

    public void onStop() {
        Log.d(LOG_TAG, "onDestroy");
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

    // отправка пакетов
    private void showLocation(Location location) {

        String lat = String.format(Locale.ENGLISH,"%1$.4f",location.getLatitude());
        String lon = String.format(Locale.ENGLISH,"%1$.4f",location.getLongitude());
        String date = String.format(String.format("%1$tF %1$tT",location.getTime()));
        String provider;

        if (location.getProvider().equals(LocationManager.GPS_PROVIDER)) provider = "GPS";
        else if (location.getProvider().equals(LocationManager.NETWORK_PROVIDER)) provider = "NETWORK";
        else provider = "OTHER";
        if(isOnline()) {

            sender = new SendOnePackage();
            sender.execute(lat, lon, date, provider);
            int buf = 1;
            try {
                sender.get();
                int flag =sender.get();
                if (sender.get()!= 1) buf=0;
            } catch (Exception e) {
                Log.d(LOG_TAG,"Exp=" + e);
                buf = 1;
            }
            if (buf==1){

                Log.d(LOG_TAG,"error 1");
                allPackage.add(new SaveOnePackage(location));
                if(parseJson.exportJsonInFile(allPackage,Ctn)) setCountPackage();
            }
        }
        else{

            Log.d(LOG_TAG,"error 1");
            allPackage.add(new SaveOnePackage(location));
            if(parseJson.exportJsonInFile(allPackage,Ctn)) setCountPackage();
        }
    }

    private void checkEnabled() {

    }

    //работа с колличеством пакетов в памяти
    private void setCountPackage(){
        sPref = getSharedPreferences("prefs",MODE_PRIVATE);
        SharedPreferences.Editor ed = sPref.edit();
        ed.putInt("package",sPref.getInt("package", 0)+1);
        ed.apply();
    }
    private int getCountPackage(){
         sPref = getSharedPreferences("prefs",MODE_PRIVATE);
         return sPref.getInt("package", 0);
     }
    private void deleteCountPackage(){
        sPref = getSharedPreferences("prefs",MODE_PRIVATE);
        SharedPreferences.Editor ed = sPref.edit();
        ed.putInt("package",0);
        ed.apply();
    }

    //асинхронный класс, для отправки единичного пакета
    class SendOnePackage extends AsyncTask<String, String, Integer> {

        @Override
        protected Integer doInBackground(String... params) {

            try {
                //создаем запрос на сервер
                DefaultHttpClient hc = new DefaultHttpClient();
                ResponseHandler<String> res = new BasicResponseHandler();
                //он у нас будет посылать post запрос
                HttpPost postMethod = new HttpPost(urlOne);
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
                Log.d(LOG_TAG,params[3]);
                if(!response.equals("1")){

                    Log.d(LOG_TAG,"error=11");
                    return 1;
                }

                if(getCountPackage()!=0){
                    postMethod = new HttpPost(urlRem);
                    nameValuePairs = new ArrayList<NameValuePair>(1);
                //посылаем на вторую активность полученные параметры
                    nameValuePairs.add(new BasicNameValuePair("login_id",Integer.toString(login_id)));
                    nameValuePairs.add(new BasicNameValuePair("count",String.format(Locale.ENGLISH,"1",getCountPackage())));
                    nameValuePairs.add(new BasicNameValuePair("value",parseJson.importJsonInFile(Ctn)));
                    postMethod.setEntity(new UrlEncodedFormEntity(nameValuePairs));
                    response = hc.execute(postMethod, res);
                    Log.d(LOG_TAG,response);

                   // if(!response.equals("1")) Log.d(LOG_TAG,"yyyyyyyyyyy");
                    if(!response.equals("1")) Log.d(LOG_TAG,"error=12");
                    else{

                        parseJson.deleteFile(Ctn);
                        deleteCountPackage();
                    }
                }
            } catch (Exception e) {
                Log.d(LOG_TAG,"Exp=" + e);
                Log.d(LOG_TAG,"error=11");
                return 1;
            }


            return 0;
        }
    }

    //проверка наличия интернет соединения
    protected boolean isOnline() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isConnected()) {
            return true;
        } else {
            return false;
        }
    }



}
