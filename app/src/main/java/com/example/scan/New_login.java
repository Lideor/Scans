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
import org.apache.http.protocol.HTTP;

import java.util.ArrayList;
import java.util.List;

public class New_login extends Activity {

    SharedPreferences sPref;//Настройка
    private MenuItem logoutMI;
    String url = "http://www.zaural-vodokanal.ru/php/auth.php";
    final String LOG_TAG = "myLogs";
    Button btnReg; //кнопнка регестрации
    TextView loginName;
    TextView info;
    RequestTask log;

    @Override
    protected void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);
        Log.d(LOG_TAG, "onStartlogin");
loadLogin_id();
        setContentView(R.layout.new_login);
        btnReg = (Button) findViewById(R.id.btnReg);
        loginName = (TextView) findViewById(R.id.NewLogin);
        info = (TextView) findViewById(R.id.info);
        btnReg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.print("asddddddddasdasddddddddd=");

                if (!(loginName.getText().toString()).equals("")) {
                    log = new RequestTask();

                   try {

                       log.execute( new String(loginName.getText().toString().getBytes("UTF-8")));
                      if(log.get()==0)  info.setText("Такое имя уже есть ┐(￣ヘ￣)┌");
                      else if(log.get()==1) info.setText("Что то не работает, попробуй позже (⋟﹏⋞)");
                   }
                   catch (Exception e) {
                       Log.d(LOG_TAG,"Exp=" + e);
                   }

                } else {
                    System.out.print("asddddddddddddddddddddddddddddd=");
                    System.out.print(loginName.getText().toString());
                    info.setText("Надо хоть что то ввести (╬ Ò﹏Ó)");
                }
            }
        });
    }
        class RequestTask extends AsyncTask<String, Integer, Integer> {

            @Override
            protected Integer doInBackground(String... params) {

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
                    postMethod.setEntity(new UrlEncodedFormEntity(nameValuePairs, HTTP.UTF_8));
                    //получаем ответ от сервера
                    String response = hc.execute(postMethod, res);
                    if (response.equals("-2"))return 0;
                    if (!response.equals("-1"))
                    {
                        int num = Integer.parseInt(response);
                        sPref = getSharedPreferences("prefs",MODE_PRIVATE);
                        SharedPreferences.Editor ed = sPref.edit();
                        ed.putInt("login_id",num);
                        ed.putString("login_name",params[0]);
                        ed.apply();
                        finish();
                    }

                    else return 1;
                } catch (Exception e) {
                    System.out.println("Exp=" + e);
                }
                return null;
            }
    }

    int loadLogin_id() {
        sPref = getSharedPreferences("prefs",MODE_PRIVATE);
        int login_id = sPref.getInt("login_id", -1);
        if (login_id == -1) return 0;
        else return 1;

    }
}