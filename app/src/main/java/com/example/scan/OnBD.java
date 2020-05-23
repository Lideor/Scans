package com.example.scan;


import android.os.AsyncTask;
import android.util.Log;

import org.apache.http.NameValuePair;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static com.example.scan.MainActivity.LOG_TAG;

public class OnBD {
    private final String getModel = "http://10.0.2.2:8000/cgi-bin/get_model.py?login_id="; // Файл расписания

    public String GetJsonModel(int id){

        String jsonModel="";
        try {
            GetModel sender = new GetModel();
            sender.execute(String.valueOf(id));
            sender.get();
            jsonModel=sender.get();

        } catch (Exception e) {
            Log.d(LOG_TAG,"Exp=" + e);
        }
        return jsonModel;
    }


    private class GetModel extends AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(String... params) {

            try {
                DefaultHttpClient hc = new DefaultHttpClient();
                ResponseHandler<String> res = new BasicResponseHandler();
                HttpGet getMethod = new HttpGet(getModel+params[0]);

                //получаем ответ от сервера
                String response = hc.execute(getMethod, res);

                Log.d(LOG_TAG,new String(response.getBytes("ISO-8859-1")));
                response=new String(response.getBytes("ISO-8859-1"));
                return response;


            } catch (Exception e) {
                Log.d(LOG_TAG,"Exp=" + e);
                Log.d(LOG_TAG,"error=11");
                return "0";
            }
        }
    }
}
