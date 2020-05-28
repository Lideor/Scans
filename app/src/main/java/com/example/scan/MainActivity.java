package com.example.scan;

import android.Manifest;
import android.app.Activity;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;

//????????
import android.net.Network;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;

import android.content.Intent;
//?????????? ??????


import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

// ?????? ? ??????
import org.apache.http.NameValuePair;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

//?????? ? ????????
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.widget.Toast;

import com.example.scan.ItemModel.DayNoCycle;
import com.example.scan.ItemModel.Event;
import com.example.scan.ItemModel.Model;
import com.example.scan.ItemModel.MonthNoCycle;
import com.example.scan.ItemModel.Time;

import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Queue;
import java.util.Calendar;

import static androidx.core.app.NotificationCompat.PRIORITY_HIGH;
import static com.example.scan.ServiceGps.CHANNEL_ID;

public class MainActivity extends AppCompatActivity {
    int login_id = -1;//????? ????????????
    String login_name = "";
    int modelInFile = 0;
    SharedPreferences sPref;// ???? ? ???????????
    String url = "http://www.zaural-vodokanal.ru/php/get_pos.php"; // ???????? ???????
    final int REQUEST_CODE_ACCESS_FINE_LOCATION = 200; // ??? ??? ???????? ?????????? ?? ???????????
    final int REQUEST_WRITE_EXTERNAL_STORAGE = 300; // ??? ??? ???????? ?????????? ?? ??????
    final int REQUEST_READ_EXTERNAL_STORAGE = 400; // ??? ??? ???????? ?????????? ?? ?????? ?????
    final int REQUEST_RECEIVE_BOOT_COMPLETED = 500;

    //?????? ?? ?????????


    private Context Ctn = this;//????????
    private int flag = 0;
    public static String STARTFOREGROUND_ACTION = "Start";
    public static String STARTFOREGROUND_STOP = "Stop";
    public static String STARTFOREGROUND_RESTART = "Restart";

    //???????
    public static String LOG_TAG = "myLogs";
    Button btnReg; //??????? ???????????

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        View btm=(View) findViewById(R.id.button);
        btm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, EventActivity.class);

                startActivity(intent);

            }

        });
        ///new JsonParse().exportModelToJson(this,new JsonParse().convertJsonModel(new OnBD().GetJsonModel(11)));
       // Model model = new JsonParse().importModelToJson(this);
     //   model.getCluster(1);
    }

    private void showContent(final int year, final int month, final int days) {
        RecyclerView rv = (RecyclerView) findViewById(R.id.rv);

        LinearLayoutManager llm = new LinearLayoutManager(this);
        rv.setLayoutManager(llm);
        Model model = null;
        if (modelInFile != 0) model = new JsonParse().importModelToJson(this);
        else {
            model = new JsonParse().convertJsonModel(new OnBD().GetJsonModel(11));// поменять на логин айди
            new JsonParse().exportModelToJson(this, model);
        }
        RVAdapterDay adapter = new RVAdapterDay(model, new Time(year, month, days), 11, this);
        rv.setAdapter(adapter);
        RelativeLayout list = (RelativeLayout) findViewById(R.id.list);

        Toolbar tool = (Toolbar) list.findViewById(R.id.toolbar);
        setSupportActionBar(tool);
        TextView day = (TextView) tool.findViewById(R.id.day);
        tool.setTitle("");
        day.setText("" + days + "." + month + "." + year);
        day.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, CalendarActivity.class);
                intent.putExtra("year", year);
                intent.putExtra("month", month);
                intent.putExtra("day", days);
                startActivity(intent);

            }

        });
    }

    ;


    @Override
    protected void onResume() {
        super.onResume();
        Log.d(LOG_TAG, "onReturnMain");
        int permissionStatus = ContextCompat.checkSelfPermission(Ctn, Manifest.permission.ACCESS_FINE_LOCATION);
        if (permissionStatus != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE_ACCESS_FINE_LOCATION);
        }
        permissionStatus = ContextCompat.checkSelfPermission(Ctn, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (permissionStatus != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_WRITE_EXTERNAL_STORAGE);
        }
        permissionStatus = ContextCompat.checkSelfPermission(Ctn, Manifest.permission.RECEIVE_BOOT_COMPLETED);
        if (permissionStatus != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECEIVE_BOOT_COMPLETED}, REQUEST_RECEIVE_BOOT_COMPLETED);
        }

        Log.d(LOG_TAG, "onStartCommand");
        if (loadLogin_id() == 0) {
            Intent stopIntent = new Intent(MainActivity.this, New_login.class);
            startActivity(stopIntent);
        } else {

            Intent intent = getIntent();

            Calendar calendar = Calendar.getInstance();
            int year = intent.getIntExtra("year", calendar.get(Calendar.YEAR));
            int month = intent.getIntExtra("month", calendar.get(Calendar.MONTH)) + 1;
            int days = intent.getIntExtra("day", calendar.get(Calendar.DAY_OF_MONTH));
            this.showContent(year, month, days);
            try {

                Intent ServiceIntent = new Intent();
                PendingIntent pi = createPendingResult(1, ServiceIntent, 0);
                Intent stopIntent = new Intent(MainActivity.this, ServiceGps.class).putExtra("2", pi);
                stopIntent.setAction(STARTFOREGROUND_ACTION);

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    startForegroundService(stopIntent);
                    Log.d(LOG_TAG, "uuuuu");

                } else {
                    startService(stopIntent);
                }
                flag = 1;
            } catch (Exception e) {
                Log.d(LOG_TAG, "ExpMain=" + e);

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

    //?????? ? ????????????
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
        sPref = getSharedPreferences("prefs", MODE_PRIVATE);
        login_id = sPref.getInt("login_id", -1);
        login_name = sPref.getString("login_name", "");
        modelInFile = sPref.getInt("model", 0);
        if (login_id == -1) return 0;
        else return 1;

    }

    // ???? ???????
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
            sPref = getSharedPreferences("prefs", MODE_PRIVATE);
            Intent intent = new Intent(MainActivity.this, New_login.class);
            SharedPreferences.Editor ed = sPref.edit();
            ed.clear().commit();
            startActivity(intent);

        }
        if (id == R.id.stop) {
            Intent stopIntent = new Intent(MainActivity.this, ServiceGps.class);
            stopIntent.setAction(STARTFOREGROUND_STOP);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                startForegroundService(stopIntent);
            } else {
                startService(stopIntent);
            }
        }
        if (id == R.id.start) {
            if (loadLogin_id() == 0) {
                Intent stopIntent = new Intent(MainActivity.this, New_login.class);
                startActivity(stopIntent);
            } else {
                try {

                    Intent ServiceIntent = new Intent();
                    PendingIntent pi = createPendingResult(1, ServiceIntent, 0);
                    Intent stopIntent = new Intent(MainActivity.this, ServiceGps.class).putExtra("2", pi);
                    stopIntent.setAction(STARTFOREGROUND_ACTION);

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        startForegroundService(stopIntent);
                        Log.d(LOG_TAG, "uuuuu");

                    } else {
                        startService(stopIntent);
                    }
                    flag = 1;
                } catch (Exception e) {
                    Log.d(LOG_TAG, "ExpMain=" + e);

                }
            }
        }
        return super.onOptionsItemSelected(item);
    }

    private void checkEnabled() {

    }

}


