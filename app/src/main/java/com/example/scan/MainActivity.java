package com.example.scan;
import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;

//асинхрон
import android.net.Network;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;
import android.content.Context;

import android.content.Intent;
//сохранение текста


import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

// работа с инетом
import org.apache.http.NameValuePair;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

//работа с локацией
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import static androidx.core.app.NotificationCompat.PRIORITY_HIGH;
import static com.example.scan.ServiceGps.CHANNEL_ID;

public class MainActivity extends AppCompatActivity {
    int login_id = -1;//логин пользователя
    SharedPreferences sPref;// файл с настройками

    String url = "http://www.zaural-vodokanal.ru/php/get_pos.php"; // отправка локации
    final int REQUEST_CODE_ACCESS_FINE_LOCATION = 200; // код ддя проверки разрешения на геолокаццию
    final int REQUEST_WRITE_EXTERNAL_STORAGE = 300; // код ддя проверки разрешения на запись
    final int REQUEST_READ_EXTERNAL_STORAGE = 400; // код ддя проверки разрешения на запись файла
    final int REQUEST_RECEIVE_BOOT_COMPLETED = 500;

    private static final int NOTIFY_ID = 101;

    TextView MainText; // бокс основного текста

    TextView Netw; // бокс основного текста

    private LocationManager locationManager;// локация
    Context Ctn = this;//контекст
    private int flag = 0;
    public static String STARTFOREGROUND_STOP = "Stop";
    //отладка
    final String LOG_TAG = "myLogs";
    Button btnReg; //кнопнка регестрации

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        MainText =(TextView) findViewById(R.id.MainText);
        Netw =(TextView) findViewById(R.id.Network);

        btnReg = (Button) findViewById(R.id.Start);
        btnReg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent stopIntent = new Intent(MainActivity.this, ServiceGps.class);
                stopIntent.setAction(STARTFOREGROUND_STOP);
                startService(stopIntent);

            }
        });
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        int permissionStatus = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);
        if (permissionStatus == PackageManager.PERMISSION_GRANTED) {
            int permissonStatus =1;
            float kakogo = 5.5F;
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,5000 * 1,5.5F, locationListener);
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,5000 * 1,5.5F, locationListener);

        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(LOG_TAG, "onReturnMain");
        int permissionStatus = ContextCompat.checkSelfPermission(Ctn, Manifest.permission.ACCESS_FINE_LOCATION);
        if(permissionStatus != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE_ACCESS_FINE_LOCATION);
        }
        permissionStatus = ContextCompat.checkSelfPermission(Ctn, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if(permissionStatus != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_WRITE_EXTERNAL_STORAGE);
        }
        permissionStatus = ContextCompat.checkSelfPermission(Ctn, Manifest.permission.RECEIVE_BOOT_COMPLETED);
        if(permissionStatus != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.RECEIVE_BOOT_COMPLETED}, REQUEST_RECEIVE_BOOT_COMPLETED);
        }

        Log.d(LOG_TAG, "onStartCommand");
        if (loadLogin_id() == 0) {
            Intent stopIntent = new Intent(MainActivity.this, New_login.class);
            startActivity(stopIntent);
        }
        else{
            try {

                Intent ServiceIntent = new Intent();
                PendingIntent pi = createPendingResult(1, ServiceIntent, 0);
                Intent stopIntent = new Intent(MainActivity.this, ServiceGps.class).putExtra("2", pi);


                stopIntent.setAction("START");
                startService(stopIntent);
                flag = 1;
            }
            catch (Exception e) {
                Log.d(LOG_TAG,"ExpMain=" + e);

            }
        }


    }
    @Override
    protected void onDestroy() {
        super.onDestroy();

    }
    @Override
    protected void onStop() {
        super.onStop();
    }

    //работа с разрешениями
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE_ACCESS_FINE_LOCATION:
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission granted

                } else {
                    // permission denied
                }

            case REQUEST_WRITE_EXTERNAL_STORAGE:
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission granted

                } else {
                    // permission denied
                }
            case REQUEST_READ_EXTERNAL_STORAGE:
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission granted

                } else {
                    // permission denied
                }
                return;
        }
    }

    int loadLogin_id() {
        sPref = getSharedPreferences("prefs",MODE_PRIVATE);
        login_id = sPref.getInt("login_id", -1);
        if (login_id == -1) return 0;
        else return 1;

    }

    // меню сетиинг
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            sPref = getPreferences(MODE_PRIVATE);
            Intent intent = new Intent(MainActivity.this, New_login.class);
            SharedPreferences.Editor ed = sPref.edit();
            ed.putInt("login_id", -1);
            ed.apply();
            startActivity(intent);

        }

        return super.onOptionsItemSelected(item);
    }

    //отлдка

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

        String speed2 = String.format(Locale.ENGLISH,"%1$.4f",location.getSpeed());
        String speed="";


        if (location.getProvider().equals(LocationManager.GPS_PROVIDER)) {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                speed = String.format(Locale.ENGLISH,"%1$.4f",location.getSpeedAccuracyMetersPerSecond());
            }
            provider = "GPS";
            MainText.setText("Gps: " + speed2+"/"+speed+" date: "+date);
        }
        else if (location.getProvider().equals(LocationManager.NETWORK_PROVIDER)) {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                    speed = String.format(Locale.ENGLISH,"%1$.4f",location.getSpeedAccuracyMetersPerSecond());
            }
            provider = "NETWORK";
            Netw.setText("NETWORK: " + speed2+"/"+speed+" date: "+date);
        }
        else provider = "OTHER";
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
             speed = String.format(Locale.ENGLISH,"%1$.4f",location.getSpeedAccuracyMetersPerSecond());
            Log.d(LOG_TAG,speed+"-"+provider);

        }
    }

    private void checkEnabled() {

    }
}


