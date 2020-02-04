package com.example.scan;
import androidx.appcompat.app.AppCompatActivity;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.apache.http.NameValuePair;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import java.util.ArrayList;
import java.util.List;

public class New_login extends Activity {

    SharedPreferences sPref;//Настройка
    private MenuItem logoutMI;
    String url = "http://www.zaural-vodokanal.ru/php/auth.php";
    final String LOG_TAG = "myLogs";
    Button btnReg; //кнопнка регестрации
    TextView loginName;
    RequestTask log;

    @Override
    protected void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);
        Log.d(LOG_TAG, "onStartlogin");

        setContentView(R.layout.new_login);
        btnReg = (Button) findViewById(R.id.btnReg);
        loginName = (TextView) findViewById(R.id.NewLogin);
        btnReg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.print("asddddddddasdasddddddddd=");
                if (loginName.getText().toString() != "") {
                    System.out.print("asdddddddddasdasd");
                    log = new RequestTask();
                    log.execute(loginName.getText().toString());
                   try {
                       log.get();
                   }
                   catch (Exception e) {
                       Log.d(LOG_TAG,"Exp=" + e);
                   }

                } else {
                    System.out.print("asddddddddddddddddddddddddddddd=");
                    System.out.print(loginName.getText().toString());
                    loginName.setText("adsasd");
                }
            }
        });
    }
        class RequestTask extends AsyncTask<String, String, String> {

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
                    nameValuePairs.add(new BasicNameValuePair("login_name",params[0]));
                    //пароль
                    //собераем их вместе и посылаем на сервер
                    postMethod.setEntity(new UrlEncodedFormEntity(nameValuePairs));
                    //получаем ответ от сервера
                    String response = hc.execute(postMethod, res);
                    if (response!="not")
                    {
                        int num = Integer.parseInt(response);
                        sPref = getSharedPreferences("prefs",MODE_PRIVATE);
                        SharedPreferences.Editor ed = sPref.edit();
                        ed.putInt("login_id",num);
                        ed.putString("login_name",params[0]);
                        ed.apply();
                        finish();
                    }

                } catch (Exception e) {
                    System.out.println("Exp=" + e);
                }
                return null;
            }
    }
}