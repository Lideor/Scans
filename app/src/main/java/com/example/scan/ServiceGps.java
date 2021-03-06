package com.example.scan;

import android.Manifest;
import android.app.Activity;
import android.app.AlarmManager;
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
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.content.ContextCompat;

import org.apache.http.NameValuePair;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import static com.example.scan.MainActivity.STARTFOREGROUND_ACTION;
import static com.example.scan.MainActivity.STARTFOREGROUND_RESTART;
import static com.example.scan.MainActivity.STARTFOREGROUND_STOP;

public class ServiceGps extends Service {

    public static final int TWO_MINUTES = 120000; // 120 seconds
    public static Boolean isRunning = false;
    public static final String CHANNEL_ID = "ForegroundServiceChannel";

    String RESTART = "com.example.scan.action.RESTAR";

    //работа с инетом
    private int fl=0;
    private String urlOne = "http://10.0.2.2:8000/cgi-bin/add_events.py"; // отправка одного пакета локации
    private String urlRem = "http://www.zaural-vodokanal.ru/php/get_pos_rem.php"; // отправка одного пакета локации
    Intent inten;
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


    //расчет скорости
    private double lastLon=0;
    private double lastLat=0;
    private double speed=0;
    private double lastTime=0;

    private long currentTime;
    private long deltTime=10*60*1000;


    public void onCreate() {
        super.onCreate();

        Log.d(LOG_TAG, "StartServics");

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        int permissionStatus = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);
        if (permissionStatus == PackageManager.PERMISSION_GRANTED) {
            int permissonStatus =1;
            float kakogo = 5.5F;
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,5000 * 1,5.5F, locationListener);
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
        inten = intent;
        try {
            if(intent!=null)
            {
               if(intent.getAction().equals(STARTFOREGROUND_STOP)){
                Log.d(LOG_TAG, "tut2");
                stopCommand();
            }
               if(intent.getAction().equals(STARTFOREGROUND_RESTART)){
                Log.d(LOG_TAG, "tut3");
                stopForeground(true);
                   if (loadSaveId() == 1) {
                       Start();

                   }
                fl=0;
            }
               if((fl!=1)&& intent.getAction().equals(STARTFOREGROUND_ACTION)) {
                Log.d(LOG_TAG, "tut");
                if (loadSaveId() == 1) {
                    Start();

                }
                fl=1;
            }
            }
        }

        catch (Exception e) {
            Log.d(LOG_TAG,"ExpStartcommand=" + e);
        }

        return Service.START_STICKY;
    }

    private void stopCommand(){
        stopForeground(true);
        stopSelf();
    }


    private void Start(){
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
                .setSmallIcon(R.drawable.ic_brightness_2_black_24dp)
                .setContentIntent(pendingIntent)
                .setOngoing(true)
                .addAction(R.drawable.ic_brightness_2_black_24dp, "Другой вариант", closeIntent)
                .build();

        currentTime = System.currentTimeMillis();
        startForeground(1, notification);
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



        /*TODO: тута
        if ((System.currentTimeMillis()-currentTime)>deltTime) {
            Intent stopIntent = new Intent(this, ServiceGps.class);
            stopIntent.setAction(STARTFOREGROUND_RESTART);
            startService(stopIntent);
        }
        */


        String speed2 = String.format(Locale.ENGLISH,"%1$.4f",location.getSpeed());

        int fl = 1;//отлдка

        if (location.getProvider().equals(LocationManager.GPS_PROVIDER)) {
            provider = "GPS";
            fl = 1;
        }
        else if (location.getProvider().equals(LocationManager.NETWORK_PROVIDER)) {
            provider = "NETWORK";
            fl = 2;
        }
        else provider = "OTHER";
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            String speed = String.format(Locale.ENGLISH,"%1$.4f",location.getSpeedAccuracyMetersPerSecond());
            Log.d(LOG_TAG,speed+"-"+provider);

        }

        //отладка


        Log.d(LOG_TAG,speed2+"-"+provider);
        if(isOnline()) {

            int buf = 1;
            try {

                sender = new SendOnePackage();
                sender.execute(lat, lon, date, provider);
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
